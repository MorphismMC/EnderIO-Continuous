package crazypants.enderio.util;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;

import com.enderio.core.common.util.NNList;

public class SidedInt {

    private final @Nonnull int[] data = new int[6];

    public SidedInt() {
        this(0);
    }

    public SidedInt(int defaultValue) {
        NNList.FACING.apply((NNList.Callback<EnumFacing>) side -> set(side, defaultValue));
    }

    public int get(@Nonnull EnumFacing side) {
        return data[side.ordinal()];
    }

    /**
     * Set a new value and return it (for chaining).
     */
    public int set(@Nonnull EnumFacing side, int value) {
        return data[side.ordinal()] = value;
    }
}
