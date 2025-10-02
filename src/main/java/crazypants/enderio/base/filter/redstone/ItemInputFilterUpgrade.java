package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import crazypants.enderio.base.filter.ItemFilterUpgrade;

public interface ItemInputFilterUpgrade extends ItemFilterUpgrade<IInputSignalFilter> {

    @Nullable
    IInputSignalFilter createInputSignalFilterFromStack(@Nonnull ItemStack stack);
}
