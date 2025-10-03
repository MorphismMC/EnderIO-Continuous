package crazypants.enderio.conduits.conduit;

import java.util.Map;

import net.minecraft.util.EnumFacing;

import com.enderio.core.common.util.DyeColor;
import org.jetbrains.annotations.NotNull;

/**
 * The conduits that have advanced networking features, including priorities, self-feed, color channels and round robin.
 * <p>
 * Note: Input in the context of conduits means "into the conduit network" and output means "out of the conduit network".
 */
public interface ConduitEnder {

    /**
     * Get the conduit output priority for a given direction.
     * 
     * @param direction The direction of the conduit connection.
     * @return          The output priority of the conduit.
     */
    default int getOutputPriority(@NotNull EnumFacing direction) {
        Integer priority = getOutputPriorities().get(direction);
        if (priority == null) return 0;
        return priority;
    }

    /**
     * Set the conduit output priority for a given direction.
     * 
     * @param direction The direction of the conduit connection.
     * @param priority  The output priority of the conduit..
     */
    default void setOutputPriority(@NotNull EnumFacing direction, int priority) {
        if (priority == 0) {
            getOutputPriorities().remove(direction);
        } else {
            getOutputPriorities().put(direction, priority);
        }
        refreshConnection(direction);
    }

    /**
     * Checks if the given conduit connection has self feed on.
     * 
     * @param direction The direction of the conduit connection.
     * @return          Returns {@code true} if self feed is active, otherwise returns {@code false}.
     */
    default boolean isSelfFeedEnabled(@NotNull EnumFacing direction) {
        Boolean enabled = getSelfFeed().get(direction);
        if (enabled == null) return false;
        return enabled;
    }

    /**
     * Set self feed for a given conduit connection,
     * 
     * @param direction The direction of the conduit connection.
     * @param enabled   Returns {@code true} to enable self feed, {@code false} to disable it
     */
    default void setSelfFeedEnabled(@NotNull EnumFacing direction, boolean enabled) {
        if (!enabled) {
            getSelfFeed().remove(direction);
        } else {
            getSelfFeed().put(direction, enabled);
        }
        refreshConnection(direction);
    }

    /**
     * Check if the given conduit connection is using round robin to route items it takes as an input.
     * 
     * @param direction The direction of the conduit connection.
     * @return          Returns {@code true} if the given input connection has round robin enabled.
     */
    default boolean isRoundRobinEnabled(@NotNull EnumFacing direction) {
        Boolean enabled = getRoundRobin().get(direction);
        if (enabled == null) return false;
        return enabled;
    }

    /**
     * Set round robin for a given conduit connection
     * 
     * @param direction The direction of a conduit connection.
     * @param enabled   Returns {@code true} to enable round robin, {@code false} to disable it.
     */
    default void setRoundRobinEnabled(@NotNull EnumFacing direction, boolean enabled) {
        if (!enabled) {
            getRoundRobin().remove(direction);
        } else {
            getRoundRobin().put(direction, enabled);
        }
        refreshConnection(direction);
    }

    /**
     * Get the color channel for the given conduit input connection.
     * 
     * @param direction The direction of the conduit connection.
     * @return          The color channel of the input.
     */
    @NotNull
    default DyeColor getInputColor(@NotNull EnumFacing direction) {
        DyeColor result = getInputColors().get(direction);
        if (result == null) {
            return DyeColor.GREEN;
        }
        return result;
    }

    /**
     * Get the color channel for the given conduit output connection.
     * 
     * @param direction The direction of the conduit connection.
     * @return          The color channel of the output.
     */
    @NotNull
    default DyeColor getOutputColor(@NotNull EnumFacing direction) {
        DyeColor result = getOutputColors().get(direction);
        if (result == null) {
            return DyeColor.GREEN;
        }
        return result;
    }

    /**
     * Set the color channel for the given conduit input connection.
     * 
     * @param direction The direction of the conduit connection.
     * @param color     The color to set the input connection channel to.
     */
    default void setInputColor(@NotNull EnumFacing direction, @NotNull DyeColor color) {
        getInputColors().put(direction, color);
        refreshConnection(direction);
        markDirty();
    }

    /**
     * Set the color channel for the given conduit output connection.
     * 
     * @param direction The direction of the conduit connection.
     * @param color     The color to set the output connection channel to.
     */
    default void setOutputColor(@NotNull EnumFacing direction, @NotNull DyeColor color) {
        getOutputColors().put(direction, color);
        refreshConnection(direction);
        markDirty();
    }

    /**
     * Gets the input connection colors for the conduit.
     * 
     * @return The map of input colors to their respective directions.
     */
    @NotNull
    Map<EnumFacing, DyeColor> getInputColors();

    /**
     * Get the output connection colors for the conduit.
     * 
     * @return The map of the output colors to the respective directions.
     */
    @NotNull
    Map<EnumFacing, DyeColor> getOutputColors();

    /**
     * Get the map of self feed status for each connection.
     * 
     * @return The map of self feed status for each direction.
     */
    @NotNull
    Map<EnumFacing, Boolean> getSelfFeed();

    /**
     * Get the map of round robin status for each connection.
     * 
     * @return The map of round robin status for each direction.
     */
    @NotNull
    Map<EnumFacing, Boolean> getRoundRobin();

    /**
     * Get the output priority for each conduit connection.
     * 
     * @return The map of the output priority for each direction.
     */
    @NotNull
    Map<EnumFacing, Integer> getOutputPriorities();

    /**
     * Refreshes a given conduit connection.
     * 
     * @param direction The direction of the conduit connection.
     */
    void refreshConnection(@NotNull EnumFacing direction);

    /**
     * Trigger a client side update is execute, it is only for client side.
     */
    void markDirty();

}
