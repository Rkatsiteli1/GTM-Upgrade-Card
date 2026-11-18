package com.gtmplus.item;

import com.gtmplus.UpgradeTier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An upgrade card. Each card may only be installed in a machine whose voltage tier matches
 * the card's tier.
 */
public class UpgradeCardItem extends Item {

    private final UpgradeTier upgradeTier;

    public UpgradeCardItem(UpgradeTier upgradeTier, Properties properties) {
        super(properties);
        this.upgradeTier = upgradeTier;
    }

    public UpgradeTier getUpgradeTier() {
        return upgradeTier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("gtmplus.tooltip.tier", Component.translatable(upgradeTier.machineTierTranslationKey()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("gtmplus.tooltip.voltage", upgradeTier.voltage())
                .withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("gtmplus.tooltip.power", upgradeTier.powerMultiplier())
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("gtmplus.tooltip.time", (int) (upgradeTier.timeReduction() * 100))
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("gtmplus.tooltip.slot_hint")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }
}
