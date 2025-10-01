package crazypants.enderio.base.conduit;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.bsideup.jabel.Desugar;
import lombok.experimental.UtilityClass;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitBundle.FacadeRenderState;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.sound.IModSound;
import crazypants.enderio.base.sound.SoundHelper;
import org.jetbrains.annotations.NotNull;

// TODO: It those logger info is useful? If not, then we should remove these.
@SuppressWarnings({ "rawtypes", "unchecked" })
@UtilityClass
public class ConduitUtil {

    /**
     * Ensure the conduit network is valid for a conduit which in.
     *
     * @param conduit The existed conduit.
     */
    public void ensureValidNetwork(@NotNull ConduitServer conduit) {
        try {
            TileEntity te = conduit.getBundle().getTileEntity();
            World world = te.getWorld();
            Collection<? extends ConduitServer> connections = getConnectedConduits(world, te.getPos(),
                    (Class<? extends ConduitServer>) conduit.getBaseConduitType()); // TODO: The EIO devs said this won't works, should be checks in the future.

            if (reuseNetwork(conduit, connections, world)) {
                // Log.warn("Re-Using network at " + conduit.getBundle().getLocation() + " for " + conduit);
                return;
            }

            // Log.warn("Re-Building network at " + conduit.getBundle().getLocation() + " for " + conduit);
            ConduitNetwork res = conduit.createNetworkForType();
            res.init(conduit.getBundle(), connections, world);
        } catch (UnloadedBlockException e) {
            ConduitNetwork<?, ?> networkToDestroy = e.getNetworkToDestroy();
            if (networkToDestroy != null) {
                for (Conduit conduitInNetwork : networkToDestroy.getConduits()) {
                    // This is just to reduce server load by avoiding that all those conduits try to form a network one
                    // by one. It failed for one of them, it will fail for all of them.
                    if (conduitInNetwork instanceof ConduitServer conduitServer) {
                        conduitServer.setNetworkBuildFailed();
                    }
                }
                networkToDestroy.destroyNetwork();
                // Log.warn("Failed building network at " + conduit.getBundle().getLocation() + " for " + conduit);
            }
        }
    }

    private boolean reuseNetwork(ConduitServer conduit,
                                 @NotNull Collection<? extends ConduitServer> connections,
                                 @NotNull World world) { // TODO: It's this param must required?
        ConduitNetwork network = null;
        for (ConduitServer conduitConnected : connections) {
            if (network == null) {
                network = conduitConnected.getNetwork();
            } else if (network != conduitConnected.getNetwork()) {
                return false;
            }
        }

        if (network == null) return false;

        if (conduit.setNetwork(network)) {
            network.addConduit(conduit);
            return true;
        }
        return false;
    }

    /**
     * Disconnects a conduit from the network in a direction
     * 
     * @param conduit    The conduit to disconnect as selected by the player.
     * @param conduitDir The direction that is being disconnected.
     * @param <C>        The conduit type, used to represent the specific resource conduit.
     */
    public <C extends ConduitServer> void disconnectConduits(@NotNull C conduit,
                                                             @NotNull EnumFacing conduitDir) {
        conduit.conduitConnectionRemoved(conduitDir);
        BlockPos pos = conduit.getBundle().getLocation().offset(conduitDir);
        Conduit conduitNeighbor = getConduit(conduit.getBundle().getTileEntity().getWorld(), pos, conduit.getBaseConduitType());

        if (conduitNeighbor instanceof ConduitServer conduitEntry) {
            conduitEntry.conduitConnectionRemoved(conduitDir.getOpposite());
            final ConduitNetwork<?, ?> neighbourNetwork = conduitEntry.getNetwork();
            if (neighbourNetwork != null) {
                neighbourNetwork.destroyNetwork();
            }
        }

        final ConduitNetwork<?, ?> network = conduit.getNetwork();
        // This should have been destroyed when destroying the neighbour's network but lets just make sure.
        if (network != null) {
            network.destroyNetwork();
        }
        conduit.connectionsChanged();
        if (conduitNeighbor instanceof ConduitServer conduitEntry) {
            conduitEntry.connectionsChanged();
        }
    }

    /** TODO: //
     * Connects two conduits together
     * 
     * @param con
     *                Conduit to connect
     * @param faceHit
     *                Direction the conduit is connecting to
     * @param <T>
     *                Type of Conduit
     * @return True if the conduit can be connected, false otherwise
     */

    public <T extends ConduitServer> boolean connectConduits(@Nonnull T con, @Nonnull EnumFacing faceHit) {
        BlockPos pos = con.getBundle().getLocation().offset(faceHit);
        Conduit neighbour = ConduitUtil.getConduit(con.getBundle().getTileEntity().getWorld(), pos,
                con.getBaseConduitType());
        if (neighbour instanceof ConduitServer && con.canConnectToConduit(faceHit, neighbour) &&
                ((ConduitServer) neighbour).canConnectToConduit(faceHit.getOpposite(), con)) {
            con.conduitConnectionAdded(faceHit);
            ((ConduitServer) neighbour).conduitConnectionAdded(faceHit.getOpposite());
            final ConduitNetwork<?, ?> network = con.getNetwork();
            if (network != null) {
                network.destroyNetwork();
            }
            final ConduitNetwork<?, ?> neighbourNetwork = ((ConduitServer) neighbour).getNetwork();
            if (neighbourNetwork != null) {
                neighbourNetwork.destroyNetwork();
            }
            con.connectionsChanged();
            ((ConduitServer) neighbour).connectionsChanged();
            return true;
        }
        return false;
    }

    public boolean forceSkylightRecalculation(@Nonnull World world, int xCoord, int yCoord, int zCoord) {
        return forceSkylightRecalculation(world, new BlockPos(xCoord, yCoord, zCoord));
    }

    public boolean forceSkylightRecalculation(@Nonnull World world, @Nonnull BlockPos pos) {
        int height = world.getHeight(pos).getY();
        if (height <= pos.getY()) {
            for (int i = 1; i < 12; i++) {
                final BlockPos offset = pos.offset(EnumFacing.UP, i);
                if (world.isAirBlock(offset)) {
                    // We need to force the re-lighting of the column due to a change
                    // in the light reaching below the block from the sky. To avoid
                    // modifying core classes to expose this functionality I am just
                    // placing then breaking
                    // a block above this one to force the check

                    world.setBlockState(offset, Blocks.STONE.getDefaultState(), 3);
                    world.setBlockToAir(offset);

                    return true;
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public FacadeRenderState getRequiredFacadeRenderState(@Nonnull ConduitBundle bundle,
                                                                 @Nonnull EntityPlayer player) {
        if (!bundle.hasFacade()) {
            return FacadeRenderState.NONE;
        }
        if (YetaUtil.isFacadeHidden(bundle, player)) {
            return FacadeRenderState.WIRE_FRAME;
        }
        return FacadeRenderState.FULL;
    }

    public boolean isConduitEquipped(@Nullable EntityPlayer player) {
        return isConduitEquipped(player, EnumHand.MAIN_HAND);
    }

    public boolean isConduitEquipped(@Nullable EntityPlayer player, @Nonnull EnumHand hand) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player;
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getHeldItem(hand);
        return equipped.getItem() instanceof ConduitItem;
    }

    public boolean isProbeEquipped(@Nullable EntityPlayer player, @Nonnull EnumHand hand) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player;
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getHeldItem(hand);
        return equipped.getItem() == itemConduitProbe.getItemNN();
    }

    @Deprecated
    public <T extends Conduit> T getConduit(@Nonnull World world, int x, int y, int z, @Nonnull Class<T> type) {
        return getConduit(world, new BlockPos(x, y, z), type);
    }

    public <T extends Conduit> T getConduit(@Nonnull World world, @Nonnull BlockPos pos,
                                                   @Nonnull Class<T> type) {
        ConduitBundle con = BlockEnder.getAnyTileEntitySafe(world, pos, ConduitBundle.class);
        if (con != null) {
            return con.getConduit(type);
        }
        return null;
    }

    public <T extends Conduit> T getConduit(@Nonnull World world, @Nonnull TileEntity te,
                                                   @Nonnull EnumFacing dir, @Nonnull Class<T> type) {
        return ConduitUtil.getConduit(world, te.getPos().offset(dir), type);
    }

    public <
            T extends ConduitServer> Collection<T> getConnectedConduits(@Nonnull World world, int x, int y, int z,
                                                                        @Nonnull Class<T> type)
                                                                                                 throws UnloadedBlockException {
        return getConnectedConduits(world, new BlockPos(x, y, z), type);
    }

    public <
            T extends ConduitServer> Collection<T> getConnectedConduits(@Nonnull World world, @Nonnull BlockPos pos,
                                                                        @Nonnull Class<T> type)
                                                                                                 throws UnloadedBlockException {
        ConduitBundle root = BlockEnder.getAnyTileEntitySafe(world, pos, ConduitBundle.class);
        if (root == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<T>();
        T con = root.getConduit(type);
        if (con != null) {
            for (EnumFacing dir : con.getConduitConnections()) {
                if (dir != null) {
                    if (!world.isBlockLoaded(pos.offset(dir))) {
                        throw new UnloadedBlockException(con.getNetwork());
                    }
                    T connected = getConduit(world, root.getTileEntity(), dir, type);
                    if (connected != null) {
                        result.add(connected);
                    }
                }
            }
        }
        return result;
    }

    public static void writeToNBT(ConduitServer conduit, @Nonnull NBTTagCompound conduitRoot) {
        if (conduit == null) {
            conduitRoot.setString("UUID", UUID.nameUUIDFromBytes("null".getBytes()).toString());
        } else {
            conduitRoot.setString("UUID", ConduitRegistry.get(conduit).getUUID().toString());
            conduit.writeToNBT(conduitRoot);
        }
    }

    public static ConduitServer readConduitFromNBT(@Nonnull NBTTagCompound conduitRoot) {
        if (conduitRoot.hasKey("UUID")) {
            String UUIDString = conduitRoot.getString("UUID");
            ConduitServer result = ConduitRegistry.getServerInstance(UUID.fromString(UUIDString));
            if (result != null) {
                result.readFromNBT(conduitRoot);
            }
            return result;
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public static ConduitClient readClientConduitFromNBT(@Nonnull NBTTagCompound conduitRoot) {
        if (conduitRoot.hasKey("UUID")) {
            String UUIDString = conduitRoot.getString("UUID");
            ConduitClient result = ConduitRegistry.getClientInstance(UUID.fromString(UUIDString));
            if (result != null) {
                result.readFromNBT(conduitRoot);
            }
            return result;
        }
        return null;
    }

    @Deprecated
    public static boolean isRedstoneControlModeMet(@Nonnull ConduitServer conduit, @Nonnull RedstoneControlMode mode,
                                                   @Nonnull DyeColor col) {
        return mode != RedstoneControlMode.NEVER;
    }

    public static boolean isRedstoneControlModeMet(@Nonnull ConduitServer conduit, @Nonnull RedstoneControlMode mode,
                                                   @Nonnull DyeColor col,
                                                   @Nonnull EnumFacing dir) {
        if (mode == RedstoneControlMode.IGNORE) {
            return true;
        } else if (mode == RedstoneControlMode.NEVER) {
            return false;
        }

        int signalStrength = conduit.getBundle().getInternalRedstoneSignalForColor(col, dir);
        if (signalStrength < RedstoneControlMode.MIN_ON_LEVEL && DyeColor.RED == col) {
            signalStrength = Math.max(signalStrength, conduit.getExternalRedstoneLevel());
        }
        return RedstoneControlMode.isConditionMet(mode, signalStrength);
    }

    public static int isBlockIndirectlyGettingPoweredIfLoaded(@Nonnull World world, @Nonnull BlockPos pos) {
        int i = 0;

        NNIterator<EnumFacing> iterator = NNList.FACING.iterator();
        while (iterator.hasNext()) {
            EnumFacing enumfacing = iterator.next();
            final BlockPos offset = pos.offset(enumfacing);
            if (world.isBlockLoaded(offset)) {
                int j = world.getRedstonePower(offset, enumfacing);

                if (j >= 15) {
                    return 15;
                }

                if (j > i) {
                    i = j;
                }
            }
        }

        return i;
    }

    public static boolean isFluidValid(FluidStack fluidStack) {
        if (fluidStack != null) {
            String name = FluidRegistry.getFluidName(fluidStack);
            if (name != null && !name.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void openConduitGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
        openConduitGui(world, pos.getX(), pos.getY(), pos.getZ(), player);
    }

    public static void openConduitGui(@Nonnull World world, int x, int y, int z, @Nonnull EntityPlayer player) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (!(te instanceof ConduitBundle)) {
            return;
        }
        ConduitBundle cb = (ConduitBundle) te;
        Set<EnumFacing> cons = new HashSet<EnumFacing>();
        boolean conduitConnections = false;
        boolean hasInsulated = false;
        for (ConduitClient con : cb.getClientConduits()) {
            cons.addAll(con.getExternalConnections());
            if (ConduitRegistry.getNetwork(con).canConnectToAnything()) {
                hasInsulated = true;
            }
            conduitConnections = conduitConnections || con.hasConduitConnections();
        }
        if (cons.isEmpty() && !hasInsulated && !conduitConnections) {
            return;
        }
        if (cons.size() == 1) {
            EnumFacing facing = cons.iterator().next();
            if (facing != null) {
                PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(te, facing));
                return;
            }
        }
        ConduitRegistry.getConduitModObjectNN().openClientGui(world, new BlockPos(x, y, z), player, null, 0);
    }

    public static void playBreakSound(@Nonnull SoundType snd, @Nonnull World world, @Nonnull BlockPos pos) {
        SoundHelper.playSound(world, pos, new Sound(snd.getBreakSound()), (snd.getVolume() + 1.0F) / 2.0F,
                snd.getPitch() * 0.8F);
    }

    public static void playHitSound(@Nonnull SoundType snd, @Nonnull World world, @Nonnull BlockPos pos) {
        SoundHelper.playSound(world, pos, new Sound(snd.getHitSound()), (snd.getVolume() + 1.0F) / 2.0F,
                snd.getPitch() * 0.8F);
    }

    public static void playStepSound(@Nonnull SoundType snd, @Nonnull World world, @Nonnull BlockPos pos) {
        SoundHelper.playSound(world, pos, new Sound(snd.getStepSound()), (snd.getVolume() + 1.0F) / 2.0F,
                snd.getPitch() * 0.8F);
    }

    public static void playPlaceSound(@Nonnull SoundType snd, @Nonnull World world, @Nonnull BlockPos pos) {
        SoundHelper.playSound(world, pos, new Sound(snd.getPlaceSound()), (snd.getVolume() + 1.0F) / 2.0F,
                snd.getPitch() * 0.8F);
    }

    @Desugar
    private record Sound(@Nonnull SoundEvent event) implements IModSound {

        @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public @Nonnull SoundEvent getSoundEvent() {
                return event;
            }

            @Override
            public @Nonnull SoundCategory getSoundCategory() {
                return SoundCategory.BLOCKS;
            }
        }
}
