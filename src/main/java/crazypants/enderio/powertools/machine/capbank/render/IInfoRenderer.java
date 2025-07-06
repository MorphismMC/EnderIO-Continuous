package crazypants.enderio.powertools.machine.capbank.render;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;

public interface IInfoRenderer {

    void render(@Nonnull TileCapBank cb, @Nonnull EnumFacing dir, float partialTick);
}
