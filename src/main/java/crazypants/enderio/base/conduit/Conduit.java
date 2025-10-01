package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import info.loenwind.autosave.annotations.Storable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Conduit {

    // region Base Functionality

    /**
     * The resource type of the conduit, such as item or liquid.
     *
     * @return The conduit container class, used to represent the resource type.
     */
    @NotNull
    Class<? extends Conduit> getBaseConduitType();

    /**
     * Create a single {@link ItemStack} for the conduit.
     * <p>
     * It is useful for recipe ingredients or drop items when picking the block and determining if a held item is the
     * same as a placed conduit (means it has same resource type).
     *
     * @return The {@link ItemStack} of the conduit (with <tt>1</tt> amount).
     */
    @NotNull
    ItemStack createItem();

    /**
     * Get all drops for the conduit.
     * <p>
     * This includes filters and upgrades and be useful for some external checking.
     *
     * @return The non-null list of {@link ItemStack}s which the conduit will drop.
     */
    @NotNull
    default NNList<ItemStack> getDrops() {
        return new NNList<>(createItem());
    }

    /**
     * Proxied of the light value from vanilla block class.
     *
     * @return The light value of the conduit.
     *
     * @see Block#getLightValue
     */
    default int getLightValue() { // TODO: Change all Block#getLightValue calling to IBlockState#getLightValue.
        return 0;
    }

    /**
     * Read NBT data from the save file (for server side) or the update packets (for client side).
     * <p>
     * Note that this will not be called for conduits that implement {@link Storable} (once conduits can do that).
     *
     * @param data The NBT tag as it was written by {@link ConduitServer#writeToNBT(NBTTagCompound)}.
     */
    void readFromNBT(@NotNull NBTTagCompound data);

    // endregion

    // region Container

    /**
     * Set the bundle the conduit belongs to.
     * <p>
     * Will be called just before the conduit is placed into the list of conduits in the bundle. It can be used to
     * initialize transient values, but should not access the world or other conduits of the bundle. It will also only
     * ever be called once (for each conduit, this is only an initialization).
     *
     * @param bundle The bundle that this conduit belongs to.
     */
    void setBundle(@Nullable ConduitBundle bundle);

    /**
     * Get the bundle the conduit belongs to.
     *
     * @return The bundle that this conduit belongs to.
     *
     * @throws NullPointerException When the conduit is not belongs to any bundle or it not be initialized.
     */
    @NotNull
    ConduitBundle getBundle() throws NullPointerException;

    // endregion

    // region Connections

    /**
     * Check if the conduit has any connections, either to other conduits or to external blocks.
     *
     * @return Returns {@code true} if the conduit has conduit or non-conduit connections, otherwise returns {@code false}.
     */
    default boolean hasConnections() {
        return hasConduitConnections() || hasExternalConnections();
    }

    /**
     * Check if the conduit has external connections.
     * <p>
     * External connections are those to non-conduit neighbors. They are rendered with a connector plate and are
     * required to have a connection settings GUI.
     *
     * @return Returns {@code true} if the conduit has connections to non-conduits, otherwise returns {@code false}.
     */
    boolean hasExternalConnections();

    /**
     * Conduit connections are connections to the same conduit type in neighboring conduit bundles.
     * <p>
     * They cannot have a GUI, but they can be added or removed with the wrench.
     *
     * @return Returns {@code true} if the conduit has conduit connections, otherwise returns {@code false}.
     *
     * @see ConduitServer#canConnectToConduit(EnumFacing, Conduit)
     */
    boolean hasConduitConnections();

    // endregion

    // region Conduit Connections

    /**
     * All directions which the conduit connected.
     *
     * @return Returns the set of the directions of connection for the conduits.
     */
    @NotNull
    Set<EnumFacing> getConduitConnections();

    /**
     * Checks if the conduit has a connection in the direction given.
     *
     * @param direction The direction to check for connection.
     * @return          Returns {@code true} if the conduit has a connection in the given direction.
     */
    default boolean containsConduitConnection(@NotNull EnumFacing direction) {
        return getConduitConnections().contains(direction);
    }

    // endregion

    // region External Connections

    /**
     * Checks if the conduit can connect to non-conduit blocks.
     *
     * @param direction            The direction of the non-conduit block.
     * @param ignoreConnectionMode Used {@code true} if the conduit should connect regardless of the block.
     * @return                     Returns {@code true} if the conduit can connect, otherwise returns {@code false}.
     */
    boolean canConnectToExternal(@NotNull EnumFacing direction, boolean ignoreConnectionMode);

    /**
     * All directions which the conduit external connected.
     *
     * @return Returns the set of the directions of all non-conduit connections
     */
    @NotNull
    Set<EnumFacing> getExternalConnections();

    /**
     * Checks if the conduit is connected to a non-conduit block in the given direction.
     *
     * @param direction The direction to check for an external connection.
     * @return          Returns {@code true} if the given direction is an external connection, otherwise returns {@code false}.
     */
    default boolean containsExternalConnection(@NotNull EnumFacing direction) {
        return getExternalConnections().contains(direction);
    }

    /**
     * Checks if the conduit has a connection in the given direction.
     *
     * @param direction The direction to check for a connection.
     * @return          Returns {@code true} if the conduit has a connection in that direction, otherwise returns {@code false}.
     */
    default boolean isConnectedTo(@NotNull EnumFacing direction) {
        return containsConduitConnection(direction) || containsExternalConnection(direction);
    }

    /**
     * Gets the connection mode in the given direction.
     *
     * @param direction The direction of the connection.
     * @return          The connection mode of the connection (<tt>IN, OUT, IN_OUT, DISABLED, NONE</tt>).
     */
    @NotNull
    ConnectionMode getConnectionMode(@NotNull EnumFacing direction);

    /**
     * Gets the effective connection mode for the given direction.
     * <p>
     * This is a computed value that reflects both the selected mode and the state of the connection.
     * 
     * @param direction The direction of the connection.
     * @return          The connection mode of the connection (<tt>IN, OUT, IN_OUT, DISABLED, NONE</tt>).
     */
    @NotNull
    default ConnectionMode getEffectiveConnectionMode(@NotNull EnumFacing direction) {
        return containsExternalConnection(direction) ? getConnectionMode(direction) : ConnectionMode.DISABLED;
    }

    /**
     * Checks if the given direction has an external connection that can be acted upon.
     * <p>
     * Note that this does not mean that there is anything going on there, just that code is allowed to talk to the
     * outside world there.
     * 
     * @param direction The direction of the connection.
     * @return          Returns {@code true} if there is a connection that is not disabled, otherwise returns {@code false}.
     */
    default boolean isActiveExternalConnection(@NotNull EnumFacing direction) {
        return getEffectiveConnectionMode(direction).isActive();
    }

    // endregion

    // region Geometry contents (For Server Side)

    boolean haveCollidablesChangedSinceLastCall();

    @NotNull
    Collection<CollidableComponent> getCollidableComponents();

    @NotNull
    Collection<CollidableComponent> createCollidables(@NotNull CacheKey key);

    @NotNull
    Class<? extends Conduit> getCollidableType();

    // endregion

    // region Actions

    // TODO: Should we deleted it and redo related contents?
    boolean onBlockActivated(@NotNull EntityPlayer player, @NotNull EnumHand hand,
                             @NotNull RaytraceResult res, @NotNull List<RaytraceResult> all);

    /**
     * <strong>Please, Do not tick unless really, really needed!</strong>
     */
    void updateEntity(@NotNull World world);

    // TODO: Check why this method be deprecated, if needed, deleted @Deprecated annotation, otherwise redo related contents.
    @Deprecated
    @NotNull
    default String getConduitProbeInfo(@NotNull EntityPlayer player) {
        return "";
    }

    /**
     * @see ICapabilityProvider#hasCapability(Capability, EnumFacing)
     */
    default boolean hasInternalCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    /**
     * @see ICapabilityProvider#getCapability(Capability, EnumFacing)
     */
    @Nullable
    default <T> T getInternalCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }

    // endregion

}
