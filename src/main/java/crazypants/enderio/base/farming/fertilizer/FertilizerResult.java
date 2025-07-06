package crazypants.enderio.base.farming.fertilizer;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import crazypants.enderio.api.farm.IFertilizerResult;

public class FertilizerResult implements IFertilizerResult {

    private final @Nonnull ItemStack stack;
    private final boolean wasApplied;

    public FertilizerResult(@Nonnull ItemStack stack, boolean wasApplied) {
        this.stack = stack;
        this.wasApplied = wasApplied;
    }

    @Override
    public @Nonnull ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean wasApplied() {
        return wasApplied;
    }
}
