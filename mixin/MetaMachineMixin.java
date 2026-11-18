package com.gtmplus.mixin;

import com.gtmplus.upgrade.IUpgradeSlotMachine;
import com.gtmplus.upgrade.UpgradeHelper;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IDropSaveMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.DropSaved;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.nbt.CompoundTag;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Gives every GregTech machine a single, persisted, client-synced upgrade slot.
 *
 * <p>The slot is stored as a plain {@link CustomItemStackHandler} field. It is deliberately
 * <strong>not</strong> a {@code MachineTrait}: GT multiblocks collect every
 * {@code IRecipeHandlerTrait} from the controller's traits when the structure forms, and an extra
 * IO.NONE handler there can break multiblock recipe handling.</p>
 *
 * <p>The machine also implements {@link IDropSaveMachine} so a card in a single-block machine is
 * saved into the dropped machine item. Multiblock controllers and parts opt out of drop-saving, so
 * their break/place behavior is completely unchanged.</p>
 */
@Mixin(value = MetaMachine.class, remap = false)
public abstract class MetaMachineMixin implements IUpgradeSlotMachine, IDropSaveMachine {

    @Unique
    @Persisted
    @DescSynced
    @DropSaved
    private CustomItemStackHandler gtmplus$upgradeSlot;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gtmplus$initUpgradeSlot(CallbackInfo ci) {
        MetaMachine self = (MetaMachine) (Object) this;
        CustomItemStackHandler slot = new CustomItemStackHandler(1);
        slot.setFilter(stack -> UpgradeHelper.isValidCard(self, stack));
        slot.setOnContentsChanged(() -> {
            if (self.isRemote()) return;
            if (self instanceof IRecipeLogicMachine logicMachine && logicMachine.getRecipeLogic() != null) {
                logicMachine.getRecipeLogic().markLastRecipeDirty();
            }
        });
        this.gtmplus$upgradeSlot = slot;
    }

    @Override
    public CustomItemStackHandler gtmplus$getUpgradeSlot() {
        return this.gtmplus$upgradeSlot;
    }

    @Override
    public boolean saveBreak() {
        MetaMachine self = (MetaMachine) (Object) this;
        return !(self instanceof IMultiController) && !(self instanceof IMultiPart);
    }

    @Override
    public boolean savePickClone() {
        return saveBreak();
    }

    @Override
    public void loadFromItem(CompoundTag tag) {
        MetaMachine self = (MetaMachine) (Object) this;
        // Never let an item's NBT overwrite multiblock controller/part state.
        if (saveBreak()) {
            self.getHolder().loadManagedPersistentData(tag);
        }
    }
}
