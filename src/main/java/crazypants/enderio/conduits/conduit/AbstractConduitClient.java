package crazypants.enderio.conduits.conduit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.RaytraceResult;
import crazypants.enderio.base.conduit.geom.CollidableCache;
import crazypants.enderio.base.conduit.geom.CollidableCache.CacheKey;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: Does we need this class? If not needed just deleted it.
public abstract class AbstractConduitClient implements ConduitClient.WithDefaultRendering {

    @NotNull
    protected final Set<EnumFacing> conduitConnections = EnumSet.noneOf(EnumFacing.class);
    @NotNull
    protected final Set<EnumFacing> externalConnections = EnumSet.noneOf(EnumFacing.class);
    @NotNull
    protected final List<CollidableComponent> collidables = new ArrayList<>();
    @NotNull
    protected final EnumMap<EnumFacing, ConnectionMode> connectionModes = new EnumMap<>(EnumFacing.class);

    @Nullable
    protected ConduitBundle bundle;

    public static final float TRANSMISSION_SCALE = 0.3f;

    protected AbstractConduitClient() {}

    @NotNull
    @Override
    public ConnectionMode getConnectionMode(@NotNull EnumFacing direction) {
        ConnectionMode mode = connectionModes.get(direction);
        if (mode == null) {
            return getDefaultConnectionMode();
        }
        return mode;
    }

    @NotNull
    protected ConnectionMode getDefaultConnectionMode() {
        return ConnectionMode.IN_OUT;
    }

    @Override
    public boolean haveCollidablesChangedSinceLastCall() {
        return false;
    }

    @Override
    public void setBundle(@Nullable ConduitBundle bundle) {
        this.bundle = bundle;
    }

    @NotNull
    @Override
    public ConduitBundle getBundle() {
        return NullHelper.notnull(bundle, "Logic error in conduit---no bundle set");
    }

    @NotNull
    @Override
    public Set<EnumFacing> getConduitConnections() {
        return conduitConnections;
    }

    @Override
    public boolean containsConduitConnection(@NotNull EnumFacing direction) {
        return conduitConnections.contains(direction);
    }

    @Override
    public boolean canConnectToExternal(@NotNull EnumFacing direction, boolean ignoreConnectionMode) {
        return false;
    }

    @NotNull
    @Override
    public Set<EnumFacing> getExternalConnections() {
        return externalConnections;
    }

    @Override
    public boolean hasExternalConnections() {
        return !externalConnections.isEmpty();
    }

    @Override
    public boolean hasConduitConnections() {
        return !conduitConnections.isEmpty();
    }

    @Override
    public boolean containsExternalConnection(@NotNull EnumFacing direction) {
        return externalConnections.contains(direction);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound data) {
        conduitConnections.clear();
        int[] dirs = data.getIntArray("connections");
        for (int dir : dirs) {
            conduitConnections.add(EnumFacing.values()[dir]);
        }

        externalConnections.clear();
        dirs = data.getIntArray("externalConnections");
        for (int dir : dirs) {
            externalConnections.add(EnumFacing.values()[dir]);
        }

        connectionModes.clear();
        byte[] modes = data.getByteArray("conModes");
        if (modes.length == 6) {
            int i = 0;
            for (EnumFacing dir : EnumFacing.VALUES) {
                connectionModes.put(dir, ConnectionMode.values()[modes[i]]);
                i++;
            }
        }

        CollidableCache cc = CollidableCache.INSTANCE;
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (dir != null && isConnectedTo(dir) && getConnectionMode(dir) != ConnectionMode.DISABLED) {
                collidables.addAll(cc.getCollidables(
                        cc.createKey(getCollidableType(), getBundle().getOffset(getBaseConduitType(), dir), dir),
                        this));
            }
        }
    }

    @Override
    public int getLightValue() {
        return 0;
    }

    @Override
    public boolean onBlockActivated(@NotNull EntityPlayer player, @NotNull EnumHand hand, @NotNull RaytraceResult res,
                                    @NotNull List<RaytraceResult> all) {
        return false;
    }

    @Override
    public float getSelfIlluminationForState(@NotNull CollidableComponent component) {
        return isActive() ? 1 : 0;
    }

    @Override
    public float getTransmitionGeometryScale() {
        return TRANSMISSION_SCALE;
    }

    @NotNull
    @Override
    public Collection<CollidableComponent> createCollidables(@NotNull CacheKey key) {
        return NullHelper.notnullJ(Collections.singletonList(
                new CollidableComponent(getCollidableType(),
                        ConduitGeometryUtil.getINSTANCE().getBoundingBox(getBaseConduitType(), key.direction, key.offset),
                        key.direction, null)),
                "Collections#singletonList");
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getCollidableType() {
        return getBaseConduitType();
    }

    @NotNull
    @Override
    public List<CollidableComponent> getCollidableComponents() {
        return collidables;
    }

    @Override
    public boolean shouldMirrorTexture() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes) {
        hashCodes.add(this.getClass());
        hashCodes.add(conduitConnections, externalConnections, connectionModes);
    }

}
