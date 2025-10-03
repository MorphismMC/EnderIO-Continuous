package crazypants.enderio.base.conduit;

import net.minecraft.util.EnumFacing;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import org.jetbrains.annotations.NotNull;

public interface ConduitExtractor extends Conduit {

    void setExtractionRedstoneMode(@NotNull RedstoneControlMode mode, @NotNull EnumFacing direction);

    @NotNull
    RedstoneControlMode getExtractionRedstoneMode(@NotNull EnumFacing direction);

    void setExtractionSignalColor(@NotNull EnumFacing direction, @NotNull DyeColor color);

    @NotNull
    DyeColor getExtractionSignalColor(@NotNull EnumFacing direction);

}
