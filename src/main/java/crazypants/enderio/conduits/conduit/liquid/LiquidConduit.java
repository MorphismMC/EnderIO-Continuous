package crazypants.enderio.conduits.conduit.liquid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitExtractor;
import crazypants.enderio.base.conduit.ConduitServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LiquidConduit extends IFluidHandler, ConduitExtractor, ConduitServer, ConduitClient {

    boolean canOutputToDir(@NotNull EnumFacing direction);

    boolean canExtractFromDir(@NotNull EnumFacing direction);

    boolean canInputToDir(@NotNull EnumFacing direction);

    /**
     * Used to get the capability of the conduit for the given direction.
     *
     * @param from The side for the capability.
     * @return     Returns the connection with reference to the relevant side.
     */
    IFluidHandler getFluidDir(@Nullable EnumFacing from);

    boolean canFill(EnumFacing direction, FluidStack resource);

    boolean canDrain(EnumFacing direction, FluidStack resource);

}
