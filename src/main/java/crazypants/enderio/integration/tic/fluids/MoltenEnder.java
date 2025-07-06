package crazypants.enderio.integration.tic.fluids;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import com.enderio.core.common.fluid.BlockFluidEnder;

import crazypants.enderio.base.teleport.RandomTeleportUtil;

public class MoltenEnder extends BlockFluidEnder {

    public MoltenEnder(@Nonnull Fluid fluid, @Nonnull Material material, int fogColor) { // 0xff0000
        super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,
                                  @Nonnull Entity entity) {
        if (!world.isRemote && entity.timeUntilPortal == 0) {
            RandomTeleportUtil.teleportEntity(world, entity, false, false, 32);
        }
        super.onEntityCollision(world, pos, state, entity);
    }
}
