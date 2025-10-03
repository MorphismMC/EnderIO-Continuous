package crazypants.enderio.conduit.gas.common.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitExtractor;
import crazypants.enderio.base.conduit.ConduitServer;
import mekanism.api.gas.IGasHandler;

public interface IGasConduit extends IGasHandler, ConduitExtractor, ConduitServer, ConduitClient {

    boolean canOutputToDir(@Nonnull EnumFacing dir);

    boolean canExtractFromDir(@Nonnull EnumFacing dir);

    boolean canInputToDir(@Nonnull EnumFacing dir);

    /**
     * Used to get the capability of the conduit for the given direction
     *
     * @param from side for the capability
     *
     * @return returns the connection with reference to the relevant side
     */
    IGasHandler getGasDir(@Nullable EnumFacing from);
}
