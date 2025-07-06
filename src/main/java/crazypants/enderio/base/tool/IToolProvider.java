package crazypants.enderio.base.tool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import crazypants.enderio.api.tool.ITool;

public interface IToolProvider {

    @Nullable
    ITool getTool(@Nonnull ItemStack stack);
}
