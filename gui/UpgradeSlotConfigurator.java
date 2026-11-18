package com.gtmplus.gui;

import com.gtmplus.UpgradeTier;
import com.gtmplus.item.UpgradeCardItem;
import com.gtmplus.registry.ModItems;
import com.gtmplus.upgrade.UpgradeHelper;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

/**
 * A fancy-UI panel that exposes the machine's single upgrade slot.
 */
public class UpgradeSlotConfigurator implements IFancyConfigurator {

    private final MetaMachine machine;
    private final CustomItemStackHandler handler;

    public UpgradeSlotConfigurator(MetaMachine machine) {
        this.machine = machine;
        this.handler = ((com.gtmplus.upgrade.IUpgradeSlotMachine) machine).gtmplus$getUpgradeSlot();
    }

    @Nullable
    private UpgradeTier machineTier() {
        return UpgradeTier.byTier(machine.getDefinition().getTier());
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtmplus.gui.upgrade.title");
    }

    @Override
    public IGuiTexture getIcon() {
        ItemStack installed = handler.getStackInSlot(0);
        if (!installed.isEmpty()) {
            return new ItemStackTexture(installed);
        }
        UpgradeTier tier = machineTier();
        ItemStack preview = new ItemStack(ModItems.card(tier == null ? UpgradeTier.LV : tier).get());
        return new ItemStackTexture(preview);
    }

    @Override
    public Widget createConfigurator() {
        WidgetGroup group = new WidgetGroup(0, 0, 176, 96);
        group.addWidget(new LabelWidget(6, 6, Component.translatable("gtmplus.gui.upgrade.title"))
                .setTextColor(ChatFormatting.WHITE.getColor()));

        SlotWidget slotWidget = new SlotWidget(handler, 0, 79, 22, true, true);
        slotWidget.setBackground(GuiTextures.SLOT);
        slotWidget.setOnAddedTooltips((widget, tooltips) -> {
            UpgradeTier tier = machineTier();
            tooltips.add(Component.translatable("gtmplus.gui.upgrade.slot_tip",
                    Component.translatable(tier == null ? "gtmplus.tier.unknown" : tier.machineTierTranslationKey())));
        });
        group.addWidget(slotWidget);
        group.addWidget(new LabelWidget(6, 46, (java.util.function.Supplier<String>) this::statusText));
        group.addWidget(new LabelWidget(6, 60, (java.util.function.Supplier<String>) this::effectText));
        group.addWidget(new LabelWidget(6, 76, (java.util.function.Supplier<String>) this::tierText));
        return group;
    }

    private String statusText() {
        ItemStack stack = handler.getStackInSlot(0);
        UpgradeTier tier = machineTier();
        Component tierName = Component.translatable(tier == null ? "gtmplus.tier.unknown" : tier.machineTierTranslationKey());
        if (stack.isEmpty()) {
            return Component.translatable("gtmplus.gui.upgrade.status.empty", tierName).getString();
        }
        if (stack.getItem() instanceof UpgradeCardItem card) {
            return Component.translatable("gtmplus.gui.upgrade.status.installed",
                    Component.translatable(card.getUpgradeTier().machineTierTranslationKey())).getString();
        }
        return Component.translatable("gtmplus.gui.upgrade.status.invalid", tierName).getString();
    }

    private String effectText() {
        UpgradeTier tier = UpgradeHelper.getCardTier(machine);
        if (tier == null) {
            return Component.translatable("gtmplus.gui.upgrade.effect.none").getString();
        }
        return Component.translatable("gtmplus.gui.upgrade.effect",
                tier.powerMultiplier(), (int) (tier.timeReduction() * 100)).getString();
    }

    private String tierText() {
        UpgradeTier tier = machineTier();
        return Component.translatable("gtmplus.gui.upgrade.tier",
                Component.translatable(tier == null ? "gtmplus.tier.unknown" : tier.machineTierTranslationKey()))
                .getString();
    }
}
