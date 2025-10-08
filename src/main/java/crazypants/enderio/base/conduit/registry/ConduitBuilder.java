package crazypants.enderio.base.conduit.registry;

import java.util.UUID;

import lombok.AllArgsConstructor;
import net.minecraft.util.ResourceLocation;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.conduit.geom.Offset;
import org.jetbrains.annotations.NotNull;

public final class ConduitBuilder {

    // region Network Data

    ConduitTypeDefinition network;

    private UUID networkUUID;
    @NotNull
    private final NNList<UUID> networkAliases = new NNList<>();
    private Class<? extends Conduit> baseType;
    private Offset none = Offset.NONE, x = Offset.NONE, y = Offset.NONE, z = Offset.NONE;
    private boolean canConnectToAnything;

    // endregion

    // region Conduit Data

    @NotNull
    private State state = State.EMPTY;

    private UUID conduitUUID;
    @NotNull
    private final NNList<UUID> conduitAliases = new NNList<>();
    private Class<? extends ConduitServer> serverClass;
    private Class<? extends ConduitClient> clientClass;

    // endregion

    // region Constructors

    private ConduitBuilder() {}

    public static ConduitBuilder builder() {
        return new ConduitBuilder();
    }

    // endregion

    // region UUID

    /**
     * Set the UUID info of the conduit with a UUID.
     *
     * @param uuid The UUID to set for the conduit.
     */
    public ConduitBuilder id(@NotNull UUID uuid) {
        checkState(state.acceptNetworkData || state.acceptConduitData);
        if (state.acceptNetworkData) {
            networkUUID = uuid;
            state = State.NETWORK;
        } else {
            conduitUUID = uuid;
            state = State.CONDUIT;
        }
        return this;
    }

    /**
     * Set the UUID info of the conduit with an existed name.
     *
     * @param name The name of the conduit.
     */
    public ConduitBuilder id(@NotNull String name) {
        return id(UUID.nameUUIDFromBytes(name.getBytes()));
    }

    /**
     * Set the UUID info of the conduit with conduit container class.
     * <p>
     * If this conduit container class should not import at there, please used {@link #id(String)}.
     *
     * @param container The container class of the conduit.
     */
    public ConduitBuilder id(@NotNull Class<? extends Conduit> container) {
        return id(container.getName());
    }

    /**
     * Set the UUID info of the conduit with an existed {@link ResourceLocation}.
     *
     * @param location The {@link ResourceLocation} of the conduit in.
     */
    public ConduitBuilder id(@NotNull ResourceLocation location) {
        return id(location.toString());
    }

    // endregion

    // region Alias

    /**
     * Set the alias UUID info of the conduit with a UUID.
     *
     * @param uuid The UUID to set for the conduit.
     */
    public ConduitBuilder alias(@NotNull UUID uuid) {
        checkState(state.acceptNetworkData || state.acceptConduitData);
        if (state.acceptNetworkData) {
            networkAliases.add(uuid);
            state = State.NETWORK;
        } else {
            conduitAliases.add(uuid);
            state = State.CONDUIT;
        }
        return this;
    }

    /**
     * Set the alias UUID info of the conduit with an existed name.
     *
     * @param name The name of the conduit.
     */
    public ConduitBuilder alias(@NotNull String name) {
        return alias(UUID.nameUUIDFromBytes(name.getBytes()));
    }

    /**
     * Set the alias UUID info of the conduit with conduit container class.
     * <p>
     * If this conduit container class should not import at there, please used {@link #alias(String)}.
     *
     * @param container The container class of the conduit.
     */
    public ConduitBuilder alias(@NotNull Class<? extends Conduit> container) {
        return alias(container.getName());
    }

    /**
     * Set the alias UUID info of the conduit with an existed {@link ResourceLocation}.
     *
     * @param location The {@link ResourceLocation} of the conduit in.
     */
    public ConduitBuilder alias(@NotNull ResourceLocation location) {
        return alias(location.toString());
    }

    // endregion

    // region Conduit Type

    /**
     * Set the base conduit type of the conduit.
     *
     * @param container The conduit container to represent its base type.
     */
    @SuppressWarnings("unchecked")
    public ConduitBuilder baseType(@NotNull Class<? extends Conduit> container) {
        checkState(state.acceptNetworkData || state.acceptConduitData);
        if (state.acceptNetworkData) {
            baseType = container;
            state = State.NETWORK;
        } else {
            if (ConduitServer.class.isAssignableFrom(container)) {
                serverClass = (Class<? extends ConduitServer>) container;
            }
            if (ConduitClient.class.isAssignableFrom(container)) {
                clientClass = (Class<? extends ConduitClient>) container;
            }
            state = State.CONDUIT;
        }
        return this;
    }

    // endregion

    // region Offsets

    public ConduitBuilder offsets(@NotNull Offset none, @NotNull Offset x, @NotNull Offset y, @NotNull Offset z) {
        checkState(state.acceptNetworkData);
        this.none = none;
        this.x = x;
        this.y = y;
        this.z = z;
        state = State.NETWORK;
        return this;
    }

    // endregion

    // region Connection Configurations

    /**
     * Marked the conduit can connect to anything without storage handler check.
     * <p>
     * It is useful for some specific type conduit like redstone signal.
     */
    public ConduitBuilder canConnectToAnything() {
        checkState(state.acceptNetworkData);
        this.canConnectToAnything = true;
        state = State.NETWORK;
        return this;
    }

    // endregion

    // region Network Building

    public ConduitBuilder build() {
        checkState(state.acceptNetworkBuild || state.acceptConduitBuild);
        if (state.acceptNetworkBuild) {
            final UUID networkId = networkUUID;
            if (networkId != null) {
                final Class<? extends Conduit> conduitBaseType = baseType;
                if (conduitBaseType != null) {
                    final Offset noneOffset = none;
                    if (noneOffset != null) {
                        final Offset xOffset = x;
                        if (xOffset != null) {
                            final Offset yOffset = y;
                            if (yOffset != null) {
                                final Offset zOffset = z;
                                if (zOffset != null) {
                                    network = new ConduitTypeDefinition(networkId, conduitBaseType,
                                            noneOffset, xOffset, yOffset, zOffset, canConnectToAnything);
                                    network.getAliases().addAll(networkAliases);
                                    state = State.PRE_CONDUIT;
                                    return this;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            final ConduitTypeDefinition networkDefinition = network;
            if (networkDefinition != null) {
                final UUID conduitId = conduitUUID;
                if (conduitId != null) {
                    final Class<? extends ConduitServer> conduitServer = serverClass;
                    if (conduitServer != null) {
                        final Class<? extends ConduitClient> conduitClient = clientClass;
                        if (conduitClient != null) {
                            new ConduitDefinition(networkDefinition, conduitId, conduitServer, conduitClient)
                                    .getAliases().addAll(conduitAliases);
                            conduitUUID = null;
                            conduitAliases.clear();
                            serverClass = null;
                            clientClass = null;
                            state = State.POST_CONDUIT;
                            return this;
                        }
                    }
                }
            }
        }
        throw new RuntimeException("State error in Conduit Builder---data missing");
    }

    @NotNull
    public ConduitTypeDefinition finish() {
        checkState(state.acceptFinalize);
        final ConduitTypeDefinition networkDefinition = network;
        if (networkDefinition != null) {
            return networkDefinition;
        } else {
            throw new RuntimeException("State error in Conduit Builder---data missing");
        }
    }

    // endregion
    
    private void checkState(boolean isReady) {
        if (!isReady) {
            throw new RuntimeException("State error in Conduit Builder (" + state + ")");
        }
    }

    @AllArgsConstructor
    private enum State {

        EMPTY(true, false, false, false, false),
        NETWORK(true, true, false, false, false),
        PRE_CONDUIT(false, false, true, false, false),
        CONDUIT(false, false, true, true, false),
        POST_CONDUIT(false, false, true, false, true);

        private final boolean acceptNetworkData, acceptNetworkBuild, acceptConduitData, acceptConduitBuild, acceptFinalize;
    }

}
