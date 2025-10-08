package crazypants.enderio.base.conduit.registry;

import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.geom.Offsets;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConduitRegistry {

    private static final Object2ObjectMap<UUID, ConduitTypeDefinition> UUID_TO_NETWORK = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<UUID, ConduitDefinition> UUID_TO_CONDUIT = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends Conduit>, UUID> CLASS_TO_UUID = new IdentityHashMap<>();

    private static final Comparator<Conduit> CONDUIT_COMPARATOR = Comparator.comparing(conduit -> getNetwork(conduit).getNetworkUUID());

    private static IModObject conduitBlock = null;
    private static boolean sortingSupported = true;

    /**
     * Register new conduit type from the type definition.
     *
     * @param info The type definition of the conduit.
     *
     * @throws RuntimeException If no location in the conduit bundle could be found for the conduit.
     */
    public static void register(ConduitTypeDefinition info) {
        UUID_TO_NETWORK.put(info.getNetworkUUID(), info);
        for (UUID uuid : info.getAliases()) {
            UUID_TO_NETWORK.put(uuid, info);
        }
        CLASS_TO_UUID.put(info.getBaseType(), info.getNetworkUUID());

        for (ConduitDefinition member : info.getMembers()) {
            UUID_TO_CONDUIT.put(member.getConduitUUID(), member);
            for (UUID uuid : member.getAliases()) {
                UUID_TO_CONDUIT.put(uuid, member);
            }
            CLASS_TO_UUID.put(member.getServerClass(), member.getConduitUUID());
            CLASS_TO_UUID.put(member.getClientClass(), member.getConduitUUID());

            // Pre-classload the instances.
            getServerInstance(member.getConduitUUID());
            if (!EnderIO.proxy.isDedicatedServer()) {
                getClientInstance(member.getConduitUUID());
            }
        }

        Offset none = info.getPreferredOffsetForNone(),
                x = info.getPreferredOffsetForX(),
                y = info.getPreferredOffsetForY(),
                z = info.getPreferredOffsetForZ();

        while (!Offsets.registerOffsets(info.getBaseType(), none, x, y, z)) {
            z = z.next();
            if (z == null) {
                z = Offset.first();
            }
            if (z == info.getPreferredOffsetForZ()) {
                y = y.next();
                if (y == null) {
                    y = Offset.first();
                }
                if (y == info.getPreferredOffsetForY()) {
                    x = x.next();
                    if (x == null) {
                        x = Offset.first();
                    }
                    if (x == info.getPreferredOffsetForX()) {
                        none = none.next();
                        if (none == null) {
                            none = Offset.first();
                        }
                    }
                }
            }
            if (z == info.getPreferredOffsetForZ() && y == info.getPreferredOffsetForY() &&
                    x == info.getPreferredOffsetForX() && none == info.getPreferredOffsetForNone()) {
                throw new RuntimeException("Failed to find free offsets for " + info.getBaseType());
            }
        }
    }

    /**
     * Add a member to an already registered conduit type.
     * <p>
     * The given {@link ConduitDefinition} must belong to an already registered {@link ConduitTypeDefinition}, you can
     * access all registered types with {@link #getNetwork(Conduit)}.
     * <p>
     * Please be advised that may or may not work. Especially conduit types where the members need to interact with each
     * other will not magically work.
     **/
    public static void injectMember(ConduitDefinition member) {
        if (!UUID_TO_NETWORK.containsKey(member.getNetwork().getNetworkUUID())) {
            throw new IllegalArgumentException(
                    "Cannot add a ConduitDefinition that is for an unregistered ConduitTypeDefinition");
        }
        UUID_TO_CONDUIT.put(member.getConduitUUID(), member);
        for (UUID uuid : member.getAliases()) {
            UUID_TO_CONDUIT.put(uuid, member);
        }
        CLASS_TO_UUID.put(member.getServerClass(), member.getConduitUUID());
        CLASS_TO_UUID.put(member.getClientClass(), member.getConduitUUID());

        // Pre-classload the instances.
        getServerInstance(member.getConduitUUID());
        if (!EnderIO.proxy.isDedicatedServer()) {
            getClientInstance(member.getConduitUUID());
        }
    }

    /**
     * @return Returns the {@link ConduitDefinition} for the given conduit instance (member).
     */
    public static ConduitDefinition get(Conduit conduit) {
        return UUID_TO_CONDUIT.get(CLASS_TO_UUID.get(conduit.getClass()));
    }

    /**
     * @return Returns the {@link ConduitDefinition} for the given conduit UUID.
     */
    public static ConduitDefinition get(UUID uuid) {
        return UUID_TO_CONDUIT.get(uuid);
    }

    /**
     * @return Returns the {@link ConduitTypeDefinition} for the given conduit instance (member).
     */
    public static ConduitTypeDefinition getNetwork(Conduit conduit) {
        return getNetwork(CLASS_TO_UUID.get(conduit.getClass()));
    }

    /**
     * @return Returns the {@link ConduitTypeDefinition} for the given conduit or network/type UUID.
     */
    public static ConduitTypeDefinition getNetwork(UUID uuid) {
        final ConduitTypeDefinition network = UUID_TO_NETWORK.get(uuid);
        return network != null ? network : get(uuid).getNetwork();
    }

    /**
     * @return Returns all registered {@link ConduitDefinition}s.
     */
    public static Collection<ConduitDefinition> getAll() {
        return UUID_TO_CONDUIT.values();
    }

    /**
     * @return Returns a new conduit instance (member) for the given member UUID (<em>not</em> network/type UUID).
     */
    public static ConduitServer getServerInstance(UUID uuid) {
        try {
            return UUID_TO_CONDUIT.get(uuid).getServerClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create a server instance of the conduit of type " + uuid, e);
        }
    }

    /**
     * @return Returns a new conduit client proxy instance (member) for the given member UUID (<em>not</em> network/type UUID).
     */
    public static ConduitClient getClientInstance(UUID uuid) {
        try {
            return UUID_TO_CONDUIT.get(uuid).getClientClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create a client instance of the conduit of type " + uuid, e);
        }
    }

    public static void sort(List<Conduit> conduits) {
        if (sortingSupported) {
            try {
                conduits.sort(CONDUIT_COMPARATOR);
            } catch (UnsupportedOperationException exception) {
                // On older versions of Java this is not supported. We don't care, the list is only sorted to optimize
                // our model cache.
                sortingSupported = false;
            }
        }
    }

    @Nullable
    public static Block getConduitBlock() {
        return conduitBlock == null ? null : conduitBlock.getBlock();
    }

    @Nullable
    public static IModObject getConduitModObject() {
        return conduitBlock;
    }

    @NotNull
    public static IModObject getConduitModObjectNN() {
        return NullHelper.notnull(conduitBlock, "Cannot use conduits unless conduits submod is installed");
    }

    @Internal // Only used in conduit module, do not used this method in external modules.
    public static void registerConduitBlock(@NotNull IModObject block) {
        conduitBlock = block;
    }

}
