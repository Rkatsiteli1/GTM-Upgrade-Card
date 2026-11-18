package com.gtmplus.mixin;

import com.gtmplus.UpgradeTier;
import com.gtmplus.upgrade.UpgradeHelper;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets a machine (or a multiblock controller through its energy hatches) draw more amperage
 * while an upgrade card is installed, so that the increased recipe EU/t can actually be supplied.
 */
@Mixin(value = NotifiableEnergyContainer.class, remap = false)
public class NotifiableEnergyContainerMixin {

    @Inject(method = "getInputAmperage", at = @At("RETURN"), cancellable = true)
    private void gtmplus$scaleInputAmperage(CallbackInfoReturnable<Long> cir) {
        MetaMachine machine = ((MachineTrait) (Object) this).getMachine();
        UpgradeTier tier = UpgradeHelper.getCardTier(machine);
        if (tier != null) {
            cir.setReturnValue(cir.getReturnValue() * tier.powerMultiplier());
        }
    }
}
