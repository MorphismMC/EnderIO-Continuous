package crazypants.enderio.base.block.charge;

import javax.annotation.Nonnull;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import crazypants.enderio.api.IModObject;

public class BlockConcussionCharge extends BlockConfusionCharge {

    public static BlockConcussionCharge create(@Nonnull IModObject modObject) {
        return new BlockConcussionCharge(modObject);
    }

    public BlockConcussionCharge(@Nonnull IModObject modObject) {
        super(modObject);
    }

    @Override
    public void explode(@Nonnull EntityPrimedCharge entity) {
        super.explode(entity);
        BlockEnderCharge.doEntityTeleport(entity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void explodeEffect(@Nonnull World world, double x, double y, double z) {
        super.explodeEffect(world, x, y, z);
        BlockEnderCharge.doTeleportEffect(world, x, y, z);
    }
}
