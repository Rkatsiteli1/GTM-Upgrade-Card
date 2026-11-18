package com.gtmplus.upgrade;

import com.gtmplus.UpgradeTier;
import com.gtmplus.item.UpgradeCardItem;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shared logic for reading, validating and applying upgrade cards.
 */
public final class UpgradeHelper {

    /** NBT marker used to make recipe modification idempotent. */
    public static final String RECIPE_MARKER = "gtmplus_upgraded";

    /**
     * Whether the machine can accept an upgrade card at all. Only single-block, EU-consuming
     * recipe machines are supported. Generators, multiblock controllers and multiblock parts
     * are not.
     */
    public static boolean supports(MetaMachine machine) {
        if (machine == null || machine.getDefinition() == null) return false;
        int tier = machine.getDefinition().getTier();
        if (tier < 1 || tier > 9) return false;
        // Multiblock support has been removed: neither controllers nor their parts accept cards.
        if (machine instanceof IMultiController || machine instanceof IMultiPart) return false;
        if (!(machine instanceof IRecipeLogicMachine logicMachine)) return false;
        if (logicMachine.getRecipeTypes() == null) return false;
        for (GTRecipeType type : logicMachine.getRecipeTypes()) {
            if (type != null && type.getMaxInputs(EURecipeCapability.CAP) > 0) {
                return true;
            }
        }
        return false;
    }

    /** Returns the upgrade inventory of a machine, or {@code null} if it has none. */
    @Nullable
    public static CustomItemStackHandler getSlot(MetaMachine machine) {
        if (machine instanceof IUpgradeSlotMachine holder) {
            return holder.gtmplus$getUpgradeSlot();
        }
        return null;
    }

    /** The upgrade tier installed in the machine itself, or {@code null}. */
    @Nullable
    public static UpgradeTier getCardTier(MetaMachine machine) {
        if (machine == null || machine.getDefinition() == null) return null;
        // Multiblock controllers and parts never use upgrade cards.
        if (machine instanceof IMultiController || machine instanceof IMultiPart) return null;
        CustomItemStackHandler handler = getSlot(machine);
        if (handler == null) return null;
        ItemStack stack = handler.getStackInSlot(0);
        if (!(stack.getItem() instanceof UpgradeCardItem card)) return null;
        if (card.getUpgradeTier().tier() != machine.getDefinition().getTier()) return null;
        return card.getUpgradeTier();
    }

    public static boolean isValidCard(MetaMachine machine, ItemStack stack) {
        if (!(stack.getItem() instanceof UpgradeCardItem card)) return false;
        if (!supports(machine)) return false;
        return card.getUpgradeTier().tier() == machine.getDefinition().getTier();
    }

    /** True if the recipe has a non-empty EU input (tick or one-shot). */
    public static boolean recipeConsumesEU(GTRecipe recipe) {
        return hasEUContent(recipe.tickInputs) || hasEUContent(recipe.inputs);
    }

    private static boolean hasEUContent(Map<RecipeCapability<?>, List<Content>> contents) {
        List<Content> eu = contents.get(EURecipeCapability.CAP);
        return eu != null && !eu.isEmpty();
    }

    /**
     * Applies the installed card to a recipe. The recipe is copied so that the global recipe
     * instance is never mutated. A marker in the recipe data makes the modification idempotent.
     */
    public static GTRecipe apply(MetaMachine machine, GTRecipe recipe) {
        UpgradeTier tier = getCardTier(machine);
        if (tier == null) return recipe;
        if (!recipeConsumesEU(recipe)) return recipe;

        CompoundTag data = recipe.data;
        if (data.getBoolean(RECIPE_MARKER)) return recipe;

        GTRecipe copy = recipe.copy();
        copy.data = data.copy();
        copy.data.putBoolean(RECIPE_MARKER, true);

        if (copy.duration > 0) {
            double timeFactor = Math.max(0.0, 1.0 - tier.timeReduction());
            copy.duration = Math.max(1, (int) Math.ceil(copy.duration * timeFactor));
        }

        scaleEU(copy.tickInputs, tier.powerMultiplier());
        scaleEU(copy.inputs, tier.powerMultiplier());
        return copy;
    }

    private static void scaleEU(Map<RecipeCapability<?>, List<Content>> contents, int factor) {
        if (contents == null) return;
        List<Content> original = contents.get(EURecipeCapability.CAP);
        if (original == null || original.isEmpty()) return;
        List<Content> scaled = new ArrayList<>(original.size());
        for (Content content : original) {
            EnergyStack stack = (EnergyStack) content.content;
            EnergyStack multiplied = new EnergyStack(stack.voltage(), stack.amperage() * factor);
            scaled.add(new Content(multiplied, content.chance, content.maxChance, content.tierChanceBoost));
        }
        contents.put(EURecipeCapability.CAP, scaled);
    }

    private UpgradeHelper() {}
}
