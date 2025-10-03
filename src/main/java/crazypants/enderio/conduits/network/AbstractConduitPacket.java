package crazypants.enderio.conduits.network;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import crazypants.enderio.conduits.gui.ExternalConnectionContainer;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;

public abstract class AbstractConduitPacket<T extends Conduit> extends AbstractConduitBundlePacket {

    private UUID uuid;

    public AbstractConduitPacket() {}

    public AbstractConduitPacket(@Nonnull T conduit) {
        super(conduit.getBundle().getTileEntity());
        this.uuid = ConduitRegistry.getNetwork(conduit).getUUID();
    }

    protected Class<? extends Conduit> getConType() {
        return ConduitRegistry.getNetwork(uuid).getBaseType();
    }

    @Override
    public void write(@SuppressWarnings("null") @Nonnull ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public void read(@SuppressWarnings("null") @Nonnull ByteBuf buf) {
        uuid = new UUID(buf.readLong(), buf.readLong());
    }

    @SuppressWarnings("unchecked")
    public @Nullable T getConduit(MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            if (ctx.getServerHandler().player.openContainer instanceof ExternalConnectionContainer) {
                final TileConduitBundle tileEntity = ((ExternalConnectionContainer) ctx
                        .getServerHandler().player.openContainer).getTileEntity();
                if (tileEntity == null || !tileEntity.getPos().equals(getPos())) {
                    Log.warn("Player " + ctx.getServerHandler().player.getName() +
                            " tried to manipulate conduit while having another conduit's GUI open!");
                    return null;
                }
            } else {
                if (BlockCoord.get(ctx.getServerHandler().player).distanceSq(getPos()) >
                        EnderIO.proxy.getReachDistanceForPlayer(ctx.getServerHandler().player)) {
                    Log.warn("Player " + ctx.getServerHandler().player.getName() +
                            " tried to manipulate conduit without having its GUI open or being near it!");
                    return null;
                }
            }
        }
        World world = getWorld(ctx);
        TileEntity tileEntity = getTileEntity(world);
        if (tileEntity instanceof ConduitBundle) {
            return (T) ((ConduitBundle) tileEntity).getConduit(getConType());
        }
        return null;
    }

    public static abstract class Sided<T extends Conduit> extends AbstractConduitPacket<T> {

        protected @Nonnull EnumFacing dir = EnumFacing.DOWN;

        public Sided() {}

        public Sided(@Nonnull T con, @Nonnull EnumFacing dir) {
            super(con);
            this.dir = dir;
        }

        @Override
        public void write(@Nonnull ByteBuf buf) {
            super.write(buf);
            buf.writeShort(dir.ordinal());
        }

        @Override
        public void read(@Nonnull ByteBuf buf) {
            super.read(buf);
            dir = EnumReader.get(EnumFacing.class, buf.readShort());
        }
    }
}
