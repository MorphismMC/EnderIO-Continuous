package crazypants.enderio.base.conduit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import org.apache.logging.log4j.util.Strings;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConduitServer extends Conduit, ICapabilityProvider {

    // region Network Contexts

    /**
     * Creates a conduit network for the given type of conduit.
     *
     * @return The network for the given conduit type.
     */
    @NotNull
    ConduitNetwork<?, ?> createNetworkForType();

    /**
     * Get the conduit network for the existed conduit.
     *
     * @return The network of the conduit.
     *
     * @throws NullPointerException If the conduit is not part of an existed conduit network.
     */
    @Nullable
    ConduitNetwork<?, ?> getNetwork() throws NullPointerException;

    /**
     * Sets the network of this conduit to a new network.
     * <p>
     * Should called when the conduit is connected to a new network.
     *
     * @param network The network to make the conduit a part of.
     * @return        Returns {@code true} if a new network is successfully set, otherwise returns {@code false}.
     */
    boolean setNetwork(@NotNull ConduitNetwork<?, ?> network);

    /**
     * Unsets the network of this conduit.
     */
    void clearNetwork();

    /**
     * Tells the conduit that it has been part of an unsuccessful attempt to form a network.
     * <p>
     * It is recommended that the conduit waits a good amount of time before trying to form a network again.
     * <p>
     * This may be called while a half-formed network is still set.
     */
    void setNetworkBuildFailed();

    // endregion

    // region Connections

    /**
     * Called when a conduit has a connection added.
     *
     * @param fromDirection The direction of the connection.
     */
    void conduitConnectionAdded(@NotNull EnumFacing fromDirection);

    /**
     * Called when a conduit has a connection removed.
     *
     * @param fromDirection The direction that the connection was removed from.
     */
    void conduitConnectionRemoved(@NotNull EnumFacing fromDirection);

    /**
     * Called when conduit connections are changed.
     */
    void connectionsChanged();

    /**
     * Called when a connection to a non-conduit block is added.
     *
     * @param fromDirection The direction of the connection.
     */
    void externalConnectionAdded(@NotNull EnumFacing fromDirection);

    /**
     * Called when a connection to a non-conduit block is removed.
     *
     * @param fromDirection The direction of the connection.
     */
    void externalConnectionRemoved(@NotNull EnumFacing fromDirection);

    /**
     * Sets the connection mode of the conduit in the given direction.
     *
     * @param direction The direction of the connection.
     * @param mode      The connection mode (<tt>IN, OUT, IN_OUT, DISABLED, NONE</tt>).
     */
    void setConnectionMode(@NotNull EnumFacing direction, @NotNull ConnectionMode mode);

    /**
     * Checks if this conduit can use the given connection mode.
     *
     * @param mode The connection mode (<tt>IN, OUT, IN_OUT, DISABLED, NONE</tt>) to check.
     * @return     Returns {@code true} if the conduit can use the given connection mode, otherwise returns {@code false}.
     */
    boolean supportsConnectionMode(@NotNull ConnectionMode mode);

    /**
     * Gets the next connection mode in the cycle.
     *
     * @param direction The direction of the connection for getting its connection mode.
     * @return          The next connection mode in the list.
     */
    @NotNull
    default ConnectionMode getNextConnectionMode(@NotNull EnumFacing direction) {
        return NNList.of(ConnectionMode.class).next(getConnectionMode(direction));
    }

    /**
     * Gets the previous connection mode in the cycle.
     *
     * @param direction The direction of the connection for getting its connection mode.
     * @return          The previous connection mode in the list.
     */
    @NotNull
    default ConnectionMode getPreviousConnectionMode(@NotNull EnumFacing direction) {
        return NNList.of(ConnectionMode.class).prev(getConnectionMode(direction));
    }

    /**
     * Determines if this conduit can connect to another conduit.
     *
     * @param direction The direction of the conduit to connect to.
     * @param conduit   The other conduit to connect to.
     * @return          Returns {@code true} if the conduit can connect in that direction and if the conduit is of the
     *                  same or another valid type.
     */
    boolean canConnectToConduit(@NotNull EnumFacing direction, @NotNull Conduit conduit);

    // endregion

    // region Conduit Status

    /**
     * Set the state of the conduit to active.
     * <p>
     * Should Called when the conduit is operating.
     *
     * @param active Used {@code true} if the conduit is currently doing something.
     */
    void setActive(boolean active);

    /**
     * @see ConduitClient#readFromNBT(NBTTagCompound)
     */
    void writeToNBT(@NotNull NBTTagCompound data);

    void onAddedToBundle();

    default void onBeforeRemovedFromBundle() {}

    void onAfterRemovedFromBundle();

    void onChunkUnload();

    boolean onNeighborBlockChange(@NotNull Block block);

    boolean onNeighborChange(@NotNull BlockPos neighbourPos);

    void invalidate();

    boolean writeConnectionSettingsToNBT(@NotNull EnumFacing direction, @NotNull NBTTagCompound nbt);

    boolean readConduitSettingsFromNBT(@NotNull EnumFacing direction, @NotNull NBTTagCompound nbt);

    @Override
    default boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Nullable
    @Override
    default <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }

    default int getExternalRedstoneLevel() {
        return 0;
    }

    @NotNull
    default NNList<ITextComponent> getConduitProbeInformation(@NotNull EntityPlayer player) {
        String info = getConduitProbeInfo(player);
        if (Strings.isBlank(info)) {
            return NNList.emptyList();
        }
        NNList<ITextComponent> result = new NNList<>();
        for (String s : info.split("\n")) {
            result.add(new TextComponentString(NullHelper.first(s, "")));
        }
        return result;
    }

    // endregion

}
