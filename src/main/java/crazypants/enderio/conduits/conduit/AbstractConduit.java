package crazypants.enderio.conduits.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.Localizable;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.ConduitNetwork;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.base.machine.interfaces.Notifiable;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractConduit implements ConduitServer, ConduitClient.WithDefaultRendering,
                                                 ConduitComponent, Notifiable {

    @NotNull
    protected final Set<EnumFacing> conduitConnections = EnumSet.noneOf(EnumFacing.class);
    @NotNull
    protected final Set<EnumFacing> externalConnections = EnumSet.noneOf(EnumFacing.class);
    @NotNull
    protected final EnumMap<EnumFacing, ConnectionMode> connectionModes = new EnumMap<>(EnumFacing.class);

    // NB: This is a transient field controlled by the owning bundle. It is not written to the NBT e.t.c.
    @Nullable
    protected ConduitBundle bundle;

    protected List<CollidableComponent> collidables;

    protected boolean active;

    protected boolean collidablesDirty = true;

    private boolean clientStateDirty = true;

    private boolean dodgyChangeSinceLastCallFlagForBundle = true;

    protected boolean connectionsDirty = true;

    protected boolean readFromNbt = false;

    private Integer lastExternalRedstoneLevel = null;

    /**
     * Client-only value. The server sends this depending on {@link #getNetwork()}. If false, the conduit will render in
     * an error state. Initialized as <code>true</code> because most conduits will have no issue to form a network.
     */
    private boolean hasNetwork = true;

    public static final float TRANSMISSION_SCALE = 0.3f;

    protected AbstractConduit() {}

    @Override
    public boolean writeConnectionSettingsToNBT(@NotNull EnumFacing direction, @NotNull NBTTagCompound data) {
        if (!getExternalConnections().contains(direction))
            return false;

        NBTTagCompound nbtData = getNBTDataForType(data, true);
        nbtData.setShort("connectionMode", (short) getConnectionMode(direction).ordinal());
        writeTypeSettingsToNBT(direction, nbtData);
        return true;
    }

    @Override
    public boolean readConduitSettingsFromNBT(@NotNull EnumFacing direction, @NotNull NBTTagCompound data) {
        if (!getExternalConnections().contains(direction) && !canConnectToExternal(direction, true))
            return false;

        NBTTagCompound nbtData = getNBTDataForType(data, false);
        if (nbtData == null)
            return false;

        if (nbtData.hasKey("connectionMode")) {
            ConnectionMode mode = NullHelper.first(ConnectionMode.values()[nbtData.getShort("connectionMode")],
                    getDefaultConnectionMode());
            setConnectionMode(direction, mode);
        }

        readTypeSettings(direction, nbtData);
        getBundle().dirty();
        return true;
    }

    protected void readTypeSettings(@NotNull EnumFacing direction, @NotNull NBTTagCompound data) {}

    protected void writeTypeSettingsToNBT(@NotNull EnumFacing dir, @NotNull NBTTagCompound data) {}

    protected NBTTagCompound getNBTDataForType(@NotNull NBTTagCompound data, boolean createIfNull) {
        Class<? extends Conduit> conduitType = getBaseConduitType();
        String dataRootName = NullHelper.notnullJ(conduitType.getSimpleName(), "Class#getSimpleName");
        NBTTagCompound nbtData = null;
        if (data.hasKey(dataRootName)) {
            nbtData = data.getCompoundTag(dataRootName);
        }
        if (nbtData == null && createIfNull) {
            nbtData = new NBTTagCompound();
            data.setTag(dataRootName, nbtData);
        }
        return nbtData;
    }

    @NotNull
    @Override
    public ConnectionMode getConnectionMode(@NotNull EnumFacing direction) {
        ConnectionMode mode = connectionModes.get(direction);
        if (mode == null) {
            return getDefaultConnectionMode();
        }
        return mode;
    }

    @NotNull
    protected ConnectionMode getDefaultConnectionMode() {
        return ConnectionMode.IN_OUT;
    }

    @Override
    public void setConnectionMode(@NotNull EnumFacing direction, @NotNull ConnectionMode mode) {
        ConnectionMode oldVal = connectionModes.get(direction);
        if (oldVal == mode) return;

        if (mode == getDefaultConnectionMode()) {
            connectionModes.remove(direction);
        } else {
            connectionModes.put(direction, mode);
        }

        connectionsChanged();
    }

    @Override
    public boolean supportsConnectionMode(@NotNull ConnectionMode mode) {
        if (mode == getDefaultConnectionMode() && connectionModes.size() != 6)
            return true;

        for (ConnectionMode connectionMode : connectionModes.values()) {
            if (connectionMode == mode)
                return true;
        }
        return false;
    }

    @NotNull
    @Override
    public ConnectionMode getNextConnectionMode(@NotNull EnumFacing direction) {
        ConnectionMode currentMode = getConnectionMode(direction);
        ConnectionMode nextMode = ConnectionMode.getNext(currentMode);
        if (nextMode == ConnectionMode.NOT_SET) {
            nextMode = ConnectionMode.IN_OUT;
        }
        return nextMode;
    }

    @NotNull
    @Override
    public ConnectionMode getPreviousConnectionMode(@NotNull EnumFacing direction) {
        ConnectionMode currentMode = getConnectionMode(direction);
        ConnectionMode prevMode = ConnectionMode.getPrevious(currentMode);
        if (prevMode == ConnectionMode.NOT_SET) {
            prevMode = ConnectionMode.DISABLED;
        }
        return prevMode;
    }

    @Override
    public boolean haveCollidablesChangedSinceLastCall() {
        if (dodgyChangeSinceLastCallFlagForBundle) {
            dodgyChangeSinceLastCallFlagForBundle = false;
            return true;
        }
        return false;
    }

    @Override
    public void setBundle(@Nullable ConduitBundle tileConduitBundle) {
        bundle = tileConduitBundle;
    }

    @NotNull
    @Override
    public ConduitBundle getBundle() {
        return NullHelper.notnull(bundle, "Logic error in conduit---no bundle set");
    }

    @NotNull
    @Override
    public Set<EnumFacing> getConduitConnections() {
        return conduitConnections;
    }

    @Override
    public boolean containsConduitConnection(@NotNull EnumFacing direction) {
        return conduitConnections.contains(direction);
    }

    @Override
    public void conduitConnectionAdded(@NotNull EnumFacing fromDirection) {
        conduitConnections.add(fromDirection);
    }

    @Override
    public void conduitConnectionRemoved(@NotNull EnumFacing fromDirection) {
        conduitConnections.remove(fromDirection);
    }

    @Override
    public boolean canConnectToConduit(@NotNull EnumFacing direction, @NotNull Conduit conduit) {
        return getConnectionMode(direction) != ConnectionMode.DISABLED &&
                conduit.getConnectionMode(direction.getOpposite()) != ConnectionMode.DISABLED;
    }

    @Override
    public boolean canConnectToExternal(@NotNull EnumFacing direction, boolean ignoreConnectionMode) {
        return false;
    }

    @NotNull
    @Override
    public Set<EnumFacing> getExternalConnections() {
        return externalConnections;
    }

    @Override
    public boolean hasExternalConnections() {
        return !externalConnections.isEmpty();
    }

    @Override
    public boolean hasConduitConnections() {
        return !conduitConnections.isEmpty();
    }

    @Override
    public boolean containsExternalConnection(@NotNull EnumFacing direction) {
        return externalConnections.contains(direction);
    }

    @Override
    public void externalConnectionAdded(@NotNull EnumFacing fromDirection) {
        externalConnections.add(fromDirection);
    }

    @Override
    public void externalConnectionRemoved(@NotNull EnumFacing fromDirection) {
        externalConnections.remove(fromDirection);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        if (active != this.active) {
            clientStateDirty = true;
        }
        this.active = active;
    }

    @Override
    public void writeToNBT(@NotNull NBTTagCompound data) {
        int[] dirs = new int[conduitConnections.size()];
        Iterator<EnumFacing> cons = conduitConnections.iterator();
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = cons.next().ordinal();
        }
        data.setIntArray("connections", dirs);

        dirs = new int[externalConnections.size()];
        cons = externalConnections.iterator();
        for (int i = 0; i < dirs.length; i++) {
            dirs[i] = cons.next().ordinal();
        }
        data.setIntArray("externalConnections", dirs);
        data.setBoolean("signalActive", active);

        if (!connectionModes.isEmpty()) {
            byte[] modes = new byte[6];
            int i = 0;
            for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
                modes[i] = (byte) getConnectionMode(itr.next()).ordinal();
                i++;
            }
            data.setByteArray("conModes", modes);
        }

        // Note: Don't tell the client that there's no network if we didn't actually try to form one yet
        data.setBoolean("hasNetwork", getNetwork() != null || nextNetworkTry == -1L);
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound data) {
        conduitConnections.clear();
        int[] dirs = data.getIntArray("connections");
        for (int dir : dirs) {
            conduitConnections.add(EnumFacing.values()[dir]);
        }

        externalConnections.clear();
        dirs = data.getIntArray("externalConnections");
        for (int dir : dirs) {
            externalConnections.add(EnumFacing.values()[dir]);
        }
        active = data.getBoolean("signalActive");

        connectionModes.clear();
        byte[] modes = data.getByteArray("conModes");
        if (modes.length == 6) {
            int i = 0;
            for (EnumFacing dir : EnumFacing.VALUES) {
                connectionModes.put(dir, ConnectionMode.values()[modes[i]]);
                i++;
            }
        }

        hasNetwork = data.getBoolean("hasNetwork");

        readFromNbt = true;
    }

    @Override
    public int getLightValue() {
        return 0;
    }

    @Override
    public boolean onBlockActivated(@NotNull EntityPlayer player, @NotNull EnumHand hand, @NotNull RaytraceResult res,
                                    @NotNull List<RaytraceResult> all) {
        return false;
    }

    @Override
    public float getSelfIlluminationForState(@NotNull CollidableComponent component) {
        return isActive() ? 1 : 0;
    }

    @Override
    public float getTransmitionGeometryScale() {
        return TRANSMISSION_SCALE;
    }

    @Override
    public void onChunkUnload() {
        ConduitNetwork<?, ?> network = getNetwork();
        if (network != null) {
            network.destroyNetwork();
        }
    }

    @Override
    public void updateEntity(@NotNull World world) {
        if (world.isRemote) return;

        Prof.start(world, "updateNetwork");
        updateNetwork(world);
        Prof.next(world, "updateConnections");
        updateConnections();
        readFromNbt = false; // the two update*()s react to this on their first run
        if (clientStateDirty) {
            getBundle().dirty();
            clientStateDirty = false;
        }
        Prof.stop(world);
    }

    private void updateConnections() {
        if (!connectionsDirty && !readFromNbt) {
            return;
        }

        boolean externalConnectionsChanged = false;
        NNList<EnumFacing> copy = new NNList<>(externalConnections);
        // remove any no longer valid connections
        for (NNIterator<EnumFacing> itr = copy.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            if (!canConnectToExternal(dir, false) || readFromNbt) {
                externalConnectionRemoved(dir);
                externalConnectionsChanged = true;
            }
        }

        // then check for new ones
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            if (!conduitConnections.contains(dir) && !externalConnections.contains(dir)) {
                if (canConnectToExternal(dir, false)) {
                    externalConnectionAdded(dir);
                    externalConnectionsChanged = true;
                }
            }
        }
        if (externalConnectionsChanged) {
            connectionsChanged();
        }

        connectionsDirty = false;
    }

    @Override
    public void connectionsChanged() {
        collidablesDirty = true;
        clientStateDirty = true;
        dodgyChangeSinceLastCallFlagForBundle = true;
    }

    protected void setClientStateDirty() {
        clientStateDirty = true;
    }

    private long nextNetworkTry = -1L;

    protected void updateNetwork(World world) {
        long tickCount = EnderIO.proxy.getServerTickCount();
        if (tickCount < nextNetworkTry && getNetwork() == null) {
            return;
        }
        if (getNetwork() == null) {
            BlockPos pos = getBundle().getLocation();
            if (world.isBlockLoaded(pos)) {
                ConduitUtil.ensureValidNetwork(this);
                ConduitNetwork<?, ?> network = getNetwork();
                if (network != null) {
                    nextNetworkTry = -1L;
                    network.sendBlockUpdatesForEntireNetwork();
                    if (readFromNbt) {
                        connectionsChanged();
                    }
                } else {
                    setNetworkBuildFailed();
                }
            }
        } else if (nextNetworkTry > -1L) {
            nextNetworkTry = -1L;
            setClientStateDirty();
        }
    }

    @Override
    public void setNetworkBuildFailed() {
        if (nextNetworkTry == -1L) {
            setClientStateDirty();
        }
        nextNetworkTry = EnderIO.proxy.getServerTickCount() + 200 + (long) (Math.random() * 100);
    }

    @NotNull
    @Override
    public Set<? extends Localizable> getNotification() {
        Set<Localizable> result = new HashSet<>();
        if (nextNetworkTry > -1L) {
            result.add(Lang.GUI_NETWORK_PARTIALLY_UNLOADED::getKey);
        }
        return result;
    }

    @NotNull
    @Override
    public NNList<ITextComponent> getConduitProbeInformation(@NotNull EntityPlayer player) {
        NNList<ITextComponent> result = new NNList<>();
        if (nextNetworkTry > -1L) {
            result.add(Lang.GUI_NETWORK_PARTIALLY_UNLOADED.toChatServer()
                    .setStyle(new Style().setColor(TextFormatting.RED)));
        }
        return result;
    }

    @Override
    public boolean setNetwork(@NotNull ConduitNetwork<?, ?> network) {
        return true;
    }

    @Override
    public void onAddedToBundle() {
        TileEntity te = getBundle().getTileEntity();
        World world = te.getWorld();

        conduitConnections.clear();
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            Conduit conduitNeighbor = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
            if (conduitNeighbor instanceof ConduitServer conduitEntry
                    && conduitEntry.canConnectToConduit(dir.getOpposite(), this)
                    && canConnectToConduit(dir, conduitNeighbor)) {
                conduitConnections.add(dir);
                conduitEntry.conduitConnectionAdded(dir.getOpposite());
                conduitEntry.connectionsChanged();
            }
        }

        externalConnections.clear();
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            if (!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
                externalConnectionAdded(dir);
            }
        }

        connectionsChanged();
    }

    @Override
    public void onAfterRemovedFromBundle() {
        TileEntity te = getBundle().getTileEntity();
        World world = te.getWorld();

        for (EnumFacing dir : conduitConnections) {
            if (dir != null) {
                Conduit conduitNeighbor = ConduitUtil.getConduit(world, te, dir, getBaseConduitType());
                if (conduitNeighbor instanceof ConduitServer conduitEntry) {
                    conduitEntry.conduitConnectionRemoved(dir.getOpposite());
                    conduitEntry.connectionsChanged();
                }
            }
        }
        conduitConnections.clear();

        if (!externalConnections.isEmpty()) {
            world.notifyNeighborsOfStateChange(te.getPos(), te.getBlockType(), true);
        }
        externalConnections.clear();

        ConduitNetwork<?, ?> network = getNetwork();
        if (network != null) {
            network.destroyNetwork();
        }
        connectionsChanged();
    }

    @Override
    public boolean onNeighborBlockChange(@NotNull Block block) {
        // NB: No need to check externals if the neighbour that changed was a conduit bundle as this can't affect
        // external connections.
        if (block == ConduitRegistry.getConduitModObjectNN().getBlock()) {
            return false;
        }

        lastExternalRedstoneLevel = null;

        // Check for changes to external connections, connections to conduits are handled by the bundle.
        Set<EnumFacing> newCons = EnumSet.noneOf(EnumFacing.class);
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            if (!containsConduitConnection(dir) && canConnectToExternal(dir, false)) {
                newCons.add(dir);
            }
        }
        if (newCons.size() != externalConnections.size()) {
            connectionsDirty = true;
            return true;
        }
        for (EnumFacing dir : externalConnections) {
            if (!newCons.remove(dir)) {
                connectionsDirty = true;
                return true;
            }
        }
        if (!newCons.isEmpty()) {
            connectionsDirty = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean onNeighborChange(@NotNull BlockPos neighborPos) {
        return false;
    }

    @NotNull
    @Override
    public Collection<CollidableComponent> createCollidables(@NotNull CacheKey key) {
        return NullHelper.notnullJ(Collections.singletonList(new CollidableComponent(getCollidableType(),
                ConduitGeometryUtil.getINSTANCE().getBoundingBox(getBaseConduitType(), key.direction, key.offset), key.direction,
                null)), "Collections#singletonList");
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getCollidableType() {
        return getBaseConduitType();
    }

    @NotNull
    @Override
    public List<CollidableComponent> getCollidableComponents() {
        if (collidables != null && !collidablesDirty) {
            return collidables;
        }

        List<CollidableComponent> result = new ArrayList<>();
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            Collection<CollidableComponent> col = getCollidables(dir);
            if (col != null) {
                result.addAll(col);
            }
        }
        collidables = result;

        collidablesDirty = false;

        return result;
    }

    private Collection<CollidableComponent> getCollidables(@NotNull EnumFacing direction) {
        CollidableCache cc = CollidableCache.INSTANCE;
        Class<? extends Conduit> type = getCollidableType();
        if (isConnectedTo(direction) && getConnectionMode(direction) != ConnectionMode.DISABLED) {
            return cc.getCollidables(cc.createKey(type, getBundle().getOffset(getBaseConduitType(), direction), direction), this);
        }
        return null;
    }

    @Override
    public boolean shouldMirrorTexture() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
        hashCodes.add(this.getClass());
        hashCodes.add(conduitConnections, externalConnections, connectionModes);
        hashCodes.add(hasNetwork);
    }

    @Override
    public void invalidate() {
        // TODO: 1.13: Make abstract unless something goes in here
    }

    @Override
    public int getExternalRedstoneLevel() {
        if (lastExternalRedstoneLevel == null) {
            if (bundle == null) {
                return 0;
            }
            TileEntity te = getBundle().getTileEntity();
            lastExternalRedstoneLevel = ConduitUtil.isBlockIndirectlyGettingPoweredIfLoaded(te.getWorld(), te.getPos());
        }
        return lastExternalRedstoneLevel;
    }

    @Override
    public boolean renderError() {
        return !hasNetwork;
    }

    @Override
    public String toString() {
        return "AbstractConduit [getClass()=" + getClass() + ", lastExternalRedstoneLevel=" +
                lastExternalRedstoneLevel + ", getConduitConnections()=" + getConduitConnections() +
                ", getExternalConnections()=" + getExternalConnections() + ", getNetwork()=" + getNetwork() + "]";
    }

}
