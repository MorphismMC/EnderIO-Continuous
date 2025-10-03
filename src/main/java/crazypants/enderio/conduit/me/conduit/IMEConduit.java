package crazypants.enderio.conduit.me.conduit;

import java.util.EnumSet;

import net.minecraft.util.EnumFacing;

import appeng.api.networking.IGridNode;
import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitServer;

public interface IMEConduit extends ConduitServer, ConduitClient {

    MEConduitGrid getGrid();

    EnumSet<EnumFacing> getConnections();

    boolean isDense();

    int getChannelsInUse();

    IGridNode getGridNode();
}
