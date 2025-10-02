package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;

import net.minecraft.world.World;

import crazypants.enderio.base.handler.ServerTickHandler.IServerTickListener;
import crazypants.enderio.base.handler.ServerTickHandler.ITickListener;
import org.jetbrains.annotations.NotNull;

/**
 * TODO: Rework ITickListener, IServerTickListener with Morphism Lib TickCounter.
 *
 * @param <C> The conduit in this network.
 * @param <T> The conduit type for this network, used implemented conduit container.
 */
public interface ConduitNetwork<C extends ConduitServer, T extends C> extends ITickListener, IServerTickListener {

    /**
     * Initialized a conduit network from existed conduit bundle.
     *
     * @param bundle      The conduit bundle.
     * @param connections The connections of the conduit.
     * @param world       The world which the conduit network in.
     *
     * @throws UnloadedBlockException If the conduit network should be checked but its block not be loaded.
     */
    void init(@NotNull ConduitBundle bundle,
              Collection<T> connections,
              @NotNull World world) throws UnloadedBlockException;

    /**
     * @see Conduit#getBaseConduitType()
     */
    @NotNull
    Class<C> getBaseConduitType();

    void setNetwork(@NotNull World world, @NotNull ConduitBundle bundle) throws UnloadedBlockException;

    void addConduit(@NotNull T newConduit);

    void destroyNetwork();

    @NotNull
    List<T> getConduits();

    void sendBlockUpdatesForEntireNetwork();

}
