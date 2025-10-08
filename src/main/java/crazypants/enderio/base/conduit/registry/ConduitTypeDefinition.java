package crazypants.enderio.base.conduit.registry;

import java.util.UUID;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.geom.Offset;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class ConduitTypeDefinition {

    @NotNull
    private final UUID networkUUID;

    @NotNull
    private final NNList<UUID> aliases = new NNList<>();

    @NotNull
    private final Class<? extends Conduit> baseType;
    @NotNull
    private final Offset none, x, y, z;
    private final boolean canConnectToAnything;

    @NotNull
    private final NNList<ConduitDefinition> members = new NNList<>();

    public ConduitTypeDefinition(@NotNull UUID networkUUID, @NotNull Class<? extends Conduit> baseType,
                                 @NotNull Offset none, @NotNull Offset x, @NotNull Offset y, @NotNull Offset z,
                                 boolean canConnectToAnything) {
        this.networkUUID = networkUUID;
        this.baseType = baseType;
        this.none = none;
        this.x = x;
        this.y = y;
        this.z = z;
        this.canConnectToAnything = canConnectToAnything;
        aliases.add(UUID.nameUUIDFromBytes(baseType.getName().getBytes())); // Compatibility with early 1.12.
    }

    public ConduitTypeDefinition(@NotNull UUID networkUUID, @NotNull Class<? extends Conduit> baseType,
                                 @NotNull Offset none, @NotNull Offset x, @NotNull Offset y, @NotNull Offset z) {
        this(networkUUID, baseType, none, x, y, z, false);
    }

    public ConduitTypeDefinition(@NotNull UUID networkUUID, @NotNull Class<? extends Conduit> baseType,
                                 boolean canConnectToAnything) {
        this(networkUUID, baseType, Offset.NONE, Offset.NONE, Offset.NONE, Offset.NONE, canConnectToAnything);
    }

    public ConduitTypeDefinition(@NotNull UUID networkUUID, @NotNull Class<? extends Conduit> baseType) {
        this(networkUUID, baseType, Offset.NONE, Offset.NONE, Offset.NONE, Offset.NONE, false);
    }

    void addMember(ConduitDefinition member) {
        members.add(member);
    }

    @NotNull
    public Offset getPreferredOffsetForNone() {
        return none;
    }

    @NotNull
    public Offset getPreferredOffsetForX() {
        return x;
    }

    @NotNull
    public Offset getPreferredOffsetForY() {
        return y;
    }

    @NotNull
    public Offset getPreferredOffsetForZ() {
        return z;
    }

    public boolean canConnectToAnything() {
        return canConnectToAnything;
    }

    @Override
    public int hashCode() {
        return networkUUID.hashCode();
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
        ConduitTypeDefinition other = (ConduitTypeDefinition) o;
        return networkUUID.equals(other.networkUUID);
    }

}
