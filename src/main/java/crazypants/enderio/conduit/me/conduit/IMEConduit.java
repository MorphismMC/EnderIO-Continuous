package crazypants.enderio.conduit.me.conduit;

import java.util.EnumSet;

import net.minecraft.util.EnumFacing;

import appeng.api.networking.IGridNode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;

public interface IMEConduit extends IServerConduit, IClientConduit {

    MEConduitGrid getGrid();

    EnumSet<EnumFacing> getConnections();

    boolean isDense();

    int getChannelsInUse();

    IGridNode getGridNode();
}
