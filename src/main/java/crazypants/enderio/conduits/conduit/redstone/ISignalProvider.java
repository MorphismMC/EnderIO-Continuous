package crazypants.enderio.conduits.conduit.redstone;

import java.util.Set;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import crazypants.enderio.base.conduit.redstone.signals.Signal;

public interface ISignalProvider {

    boolean connectsToNetwork(World world, BlockPos pos, EnumFacing side);

    Set<Signal> getNetworkInputs(World world, BlockPos pos, EnumFacing side);
}
