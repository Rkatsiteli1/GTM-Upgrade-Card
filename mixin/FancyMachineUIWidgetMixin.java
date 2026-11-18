package com.gtmplus.mixin;

import com.gtmplus.gui.UpgradeSlotConfigurator;
import com.gtmplus.upgrade.UpgradeHelper;

import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.machine.MetaMachine;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Attaches the upgrade slot configurator to every GregTech fancy machine UI that supports it.
 */
@Mixin(value = FancyMachineUIWidget.class, remap = false)
public class FancyMachineUIWidgetMixin {

    @Shadow
    @Final
    protected ConfiguratorPanel configuratorPanel;

    @Inject(method = "setupFancyUI(Lcom/gregtechceu/gtceu/api/gui/fancy/IFancyUIProvider;Z)V",
            at = @At("TAIL"))
    private void gtmplus$attachUpgradeConfigurator(IFancyUIProvider fancyUI, boolean showInventory, CallbackInfo ci) {
        if (fancyUI instanceof MetaMachine machine && UpgradeHelper.supports(machine)) {
            configuratorPanel.attachConfigurators(new UpgradeSlotConfigurator(machine));
        }
    }
}
