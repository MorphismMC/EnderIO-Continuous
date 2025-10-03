package crazypants.enderio.base.conduit;

import static crazypants.enderio.base.init.ModObject.itemConduitProbe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitBundle.FacadeRenderState;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.sound.ModSound;
import crazypants.enderio.base.sound.SoundHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * Connects two conduits together.
     * 
     * @param conduit The other conduit to connect.
     * @param facing  The hit direction the conduit is connecting to.
     * @param <C>     The type of the conduit.
     * @return        Returns {@code true} if the conduit can be connected, otherwise returns {@code false}.
     */
    public <C extends ConduitServer> boolean connectConduits(@NotNull C conduit, @NotNull EnumFacing facing) {
        BlockPos pos = conduit.getBundle().getLocation().offset(facing);
        Conduit conduitNeighbor = getConduit(conduit.getBundle().getTileEntity().getWorld(),
                pos, conduit.getBaseConduitType());

        if (conduitNeighbor instanceof ConduitServer conduitEntry
                && conduit.canConnectToConduit(facing, conduitNeighbor)
                && conduitEntry.canConnectToConduit(facing.getOpposite(), conduit)) {
            conduit.conduitConnectionAdded(facing);
            conduitEntry.conduitConnectionAdded(facing.getOpposite());
            final ConduitNetwork<?, ?> network = conduit.getNetwork();
            if (network != null) {
                network.destroyNetwork();
            }
            final ConduitNetwork<?, ?> networkNeighbor = conduitEntry.getNetwork();
            if (networkNeighbor != null) {
                networkNeighbor.destroyNetwork();
            }
            conduit.connectionsChanged();
            conduitEntry.connectionsChanged();
            return true;
        }
        return false;
    }

    public boolean forceSkylightRecalculation(@NotNull World world, int x, int y, int z) {
        return forceSkylightRecalculation(world, new BlockPos(x, y, z));
    }

    public boolean forceSkylightRecalculation(@NotNull World world, @NotNull BlockPos pos) {
        int height = world.getHeight(pos).getY();
        if (height <= pos.getY()) {
            for (int i = 1; i < 12; i++) {
                final BlockPos offset = pos.offset(EnumFacing.UP, i);
                if (world.isAirBlock(offset)) {
                    // We need to force the re-lighting of the column due to a change in the light reaching below the
                    // block from the sky. To avoid modifying core classes to expose this functionality I am just
                    // placing then breaking a block above this one to force the check.
                    world.setBlockState(offset, Blocks.STONE.getDefaultState(), 3);
                    world.setBlockToAir(offset);
                    return true;
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public FacadeRenderState getRequiredFacadeRenderState(@NotNull ConduitBundle bundle,
                                                          @NotNull EntityPlayer player) {
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

    public boolean isConduitEquipped(@Nullable EntityPlayer player, @NotNull EnumHand hand) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player; // TODO: Used Morphism Lib clientPlayer.
        if (player == null) {
            return false;
        }
        ItemStack equippedStack = player.getHeldItem(hand);
        return equippedStack.getItem() instanceof ConduitItem;
    }

    public boolean isProbeEquipped(@Nullable EntityPlayer player, @NotNull EnumHand hand) {
        player = player == null ? EnderIO.proxy.getClientPlayer() : player; // TODO: Used Morphism Lib clientPlayer.
        if (player == null) {
            return false;
        }
        ItemStack equippedStack = player.getHeldItem(hand);
        return equippedStack.getItem() == itemConduitProbe.getItemNN();
    }

    /**
     * Get specific type conduit at the existed and fixed block pos.
     *
     * @param world The world which the conduit in.
     * @param pos   The {@link BlockPos} which the conduit at in the {@code world}.
     * @param type  The conduit type of the conduit.
     * @param <C>   The type of the conduit.
     * @return      The conduit container of the conduit.
     */
    public <C extends Conduit> C getConduit(@NotNull World world, @NotNull BlockPos pos, @NotNull Class<C> type) {
        ConduitBundle conduit = BlockEnder.getAnyTileEntitySafe(world, pos, ConduitBundle.class);
        if (conduit != null) {
            return conduit.getConduit(type);
        }
        return null;
    }

    /**
     * Get specific type conduit from {@link TileEntity} and its facing.
     *
     * @param world      The world which the conduit in.
     * @param tileEntity The {@link TileEntity} of the conduit.
     * @param direction  The direction the conduit is connecting to.
     * @param type       The conduit type of the conduit.
     * @param <C>        The type of the conduit.
     * @return           The conduit container of the conduit.
     */
    public <C extends Conduit> C getConduit(@NotNull World world, @NotNull TileEntity tileEntity,
                                            @NotNull EnumFacing direction, @NotNull Class<C> type) {
        return ConduitUtil.getConduit(world, tileEntity.getPos().offset(direction), type);
    }

    public <C extends ConduitServer> Collection<C> getConnectedConduits(@NotNull World world,
                                                                        @NotNull BlockPos pos,
                                                                        @NotNull Class<C> type) throws UnloadedBlockException {
        ConduitBundle bundle = BlockEnder.getAnyTileEntitySafe(world, pos, ConduitBundle.class);
        if (bundle == null) {
            return Collections.emptyList(); // TODO: Change to Morphism Lib ListOps#of.
        }
        List<C> result = new ArrayList<>();
        C conduit = bundle.getConduit(type);
        if (conduit != null) {
            for (EnumFacing direction : conduit.getConduitConnections()) {
                if (direction != null) {
                    if (!world.isBlockLoaded(pos.offset(direction))) {
                        throw new UnloadedBlockException(conduit.getNetwork());
                    }
                    C conduitConnected = getConduit(world, bundle.getTileEntity(), direction, type);
                    if (conduitConnected != null) {
                        result.add(conduitConnected);
                    }
                }
            }
        }
        return result;
    }

    public static void writeToNBT(ConduitServer conduit, @NotNull NBTTagCompound conduitRoot) {
        if (conduit == null) {
            conduitRoot.setString("UUID", UUID.nameUUIDFromBytes("null".getBytes()).toString());
        } else {
            conduitRoot.setString("UUID", ConduitRegistry.get(conduit).getUUID().toString());
            conduit.writeToNBT(conduitRoot);
        }
    }

    @Nullable
    public static ConduitServer readConduitFromNBT(@NotNull NBTTagCompound conduitRoot) {
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
    public static ConduitClient readClientConduitFromNBT(@NotNull NBTTagCompound conduitRoot) {
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

    public static boolean isRedstoneControlModeMet(@NotNull ConduitServer conduit,
                                                   @NotNull RedstoneControlMode mode,
                                                   @NotNull DyeColor color,
                                                   @NotNull EnumFacing direction) {
        if (mode == RedstoneControlMode.IGNORE) {
            return true;
        } else if (mode == RedstoneControlMode.NEVER) {
            return false;
        }

        int signalStrength = conduit.getBundle().getInternalRedstoneSignalForColor(color, direction);
        if (signalStrength < RedstoneControlMode.MIN_ON_LEVEL && DyeColor.RED == color) {
            signalStrength = Math.max(signalStrength, conduit.getExternalRedstoneLevel());
        }
        return RedstoneControlMode.isConditionMet(mode, signalStrength);
    }

    public static int isBlockIndirectlyGettingPoweredIfLoaded(@NotNull World world, @NotNull BlockPos pos) {
        int i = 0;
        for (EnumFacing enumfacing : NNList.FACING) {
            final BlockPos offset = pos.offset(enumfacing);
            if (world.isBlockLoaded(offset)) {
                int j = world.getRedstonePower(offset, enumfacing);

                if (j >= 15) return 15;

                if (j > i)
                    i = j;
            }
        }

        return i;
    }

    public static boolean isFluidValid(FluidStack fluidStack) {
        if (fluidStack != null) {
            String name = FluidRegistry.getFluidName(fluidStack);
            return name != null && !name.trim().isEmpty();
        }
        return false;
    }

    public static void openConduitGui(@NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        openConduitGui(world, pos.getX(), pos.getY(), pos.getZ(), player);
    }

    public static void openConduitGui(@NotNull World world, int x, int y, int z, @NotNull EntityPlayer player) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (!(te instanceof ConduitBundle bundle)) {
            return;
        }

        Set<EnumFacing> connections = new HashSet<>();

        boolean conduitConnections = false;
        boolean hasInsulated = false;

        for (ConduitClient conduit : bundle.getClientConduits()) {
            connections.addAll(conduit.getExternalConnections());
            if (ConduitRegistry.getNetwork(conduit).canConnectToAnything()) {
                hasInsulated = true;
            }
            conduitConnections = conduitConnections || conduit.hasConduitConnections();
        }

        if (connections.isEmpty() && !hasInsulated && !conduitConnections) {
            return;
        }

        if (connections.size() == 1) {
            EnumFacing facing = connections.iterator().next();
            if (facing != null) {
                PacketHandler.INSTANCE.sendToServer(new PacketOpenConduitUI(te, facing));
                return;
            }
        }
        ConduitRegistry.getConduitModObjectNN().openClientGui(world, new BlockPos(x, y, z), player, null, 0);
    }

    public static void playBreakSound(@NotNull SoundType sound, @NotNull World world, @NotNull BlockPos pos) {
        SoundHelper.playSound(world, pos, new ModSoundConduit(sound.getBreakSound()),
                (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F);
    }

    public static void playHitSound(@NotNull SoundType sound, @NotNull World world, @NotNull BlockPos pos) {
        SoundHelper.playSound(world, pos, new ModSoundConduit(sound.getHitSound()),
                (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F);
    }

    public static void playStepSound(@NotNull SoundType sound, @NotNull World world, @NotNull BlockPos pos) {
        SoundHelper.playSound(world, pos, new ModSoundConduit(sound.getStepSound()),
                (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F);
    }

    public static void playPlaceSound(@NotNull SoundType sound, @NotNull World world, @NotNull BlockPos pos) {
        SoundHelper.playSound(world, pos, new ModSoundConduit(sound.getPlaceSound()),
                (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F);
    }

    @Desugar
    private record ModSoundConduit(@NotNull SoundEvent event) implements ModSound {

        @Override
        public boolean isValid() {
            return true;
        }

        @NotNull
        @Override
        public SoundEvent getSoundEvent() {
            return event;
        }

        @NotNull
        @Override
        public SoundCategory getSoundCategory() {
            return SoundCategory.BLOCKS;
        }

    }

}
