package crazypants.enderio.integration.tic.fluids;

import javax.annotation.Nonnull;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import com.enderio.core.common.fluid.BlockFluidEnder;

public class MoltenGlowstone extends BlockFluidEnder {

    public MoltenGlowstone(@Nonnull Fluid fluid, @Nonnull Material material, int fogColor) { // 0xffbc5e
        super(fluid, material, fogColor);
    }

    @Override
    public void onEntityCollision(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,
                                  @Nonnull Entity entity) {
        if (!world.isRemote && entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 0, true, true));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.GLOWING, 2400, 0, true, true));
        }
        super.onEntityCollision(world, pos, state, entity);
    }
}
