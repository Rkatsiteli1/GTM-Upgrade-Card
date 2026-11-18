package com.gtmplus.upgrade;

import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

/**
 * Implemented by {@code MetaMachine} through a mixin. Gives every machine a single upgrade slot.
 *
 * <p>The slot is a plain {@link CustomItemStackHandler} machine field, not a {@code MachineTrait},
 * so it never participates in GT's recipe-handler or capability aggregation. This is important for
 * multiblock machines, whose controller collects all {@code IRecipeHandlerTrait}s of its own
 * traits.</p>
 */
public interface IUpgradeSlotMachine {

    CustomItemStackHandler gtmplus$getUpgradeSlot();
}
