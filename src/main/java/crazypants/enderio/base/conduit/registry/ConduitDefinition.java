package crazypants.enderio.base.conduit.registry;

import java.util.UUID;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class ConduitDefinition {

    @NotNull
    private final ConduitTypeDefinition network;
    @NotNull
    private final UUID conduitUUID;
    @NotNull
    private final NNList<UUID> aliases = new NNList<>();
    @NotNull
    private final Class<? extends ConduitServer> serverClass;
    @NotNull
    private final Class<? extends ConduitClient> clientClass;

    public ConduitDefinition(@NotNull ConduitTypeDefinition network, @NotNull UUID conduitUUID,
                             @NotNull Class<? extends ConduitServer> serverClass,
                             @NotNull Class<? extends ConduitClient> clientClass) {
        this.network = network;
        this.conduitUUID = conduitUUID;
        this.serverClass = serverClass;
        this.clientClass = clientClass;
        aliases.add(UUID.nameUUIDFromBytes(serverClass.getName().getBytes())); // Compatibility with early 1.12.
        if (serverClass != clientClass) {
            aliases.add(UUID.nameUUIDFromBytes(clientClass.getName().getBytes())); // Compatibility with early 1.12.
        }
        network.addMember(this);
    }

    public <T extends ConduitServer & ConduitClient> ConduitDefinition(@NotNull ConduitTypeDefinition network,
                                                                       @NotNull UUID conduitUUID,
                                                                       @NotNull Class<? extends T> serverClass) {
        this(network, conduitUUID, serverClass, serverClass);
    }

    @Override
    public int hashCode() {
        return conduitUUID.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        ConduitDefinition other = (ConduitDefinition) o;
        return conduitUUID.equals(other.conduitUUID);
    }

}
