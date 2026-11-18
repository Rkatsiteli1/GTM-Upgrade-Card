package com.gtmplus.mixin;

import com.gtmplus.upgrade.UpgradeHelper;

import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Applies the installed upgrade card to recipes before the recipe logic starts them.
 */
@Mixin(value = RecipeLogic.class, remap = false)
public class RecipeLogicMixin {

    @Shadow
    @Final
    public IRecipeLogicMachine machine;

    @ModifyVariable(method = "setupRecipe", at = @At("HEAD"), argsOnly = true)
    private GTRecipe gtmplus$applyUpgrade(GTRecipe recipe) {
        if (recipe == null) return null;
        return UpgradeHelper.apply(machine.self(), recipe);
    }
}
