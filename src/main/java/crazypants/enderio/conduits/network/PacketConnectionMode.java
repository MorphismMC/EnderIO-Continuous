package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;

public class PacketConnectionMode extends AbstractConduitPacket.Sided<Conduit> {

    private @Nonnull ConnectionMode mode = ConnectionMode.NOT_SET;

    public PacketConnectionMode() {}

    public PacketConnectionMode(@Nonnull Conduit con, @Nonnull EnumFacing dir, @Nonnull ConnectionMode mode) {
        super(con, dir);
        this.mode = mode;
    }

    @Override
    public void write(@Nonnull ByteBuf buf) {
        super.write(buf);
        buf.writeShort(mode.ordinal());
    }

    @Override
    public void read(@Nonnull ByteBuf buf) {
        super.read(buf);
        mode = EnumReader.get(ConnectionMode.class, buf.readShort());
    }

    public static class Handler implements IMessageHandler<PacketConnectionMode, IMessage> {

        @Override
        public IMessage onMessage(PacketConnectionMode message, MessageContext ctx) {
            Conduit conduit = message.getConduit(ctx);
            if (conduit instanceof ConduitServer) {
                if (conduit instanceof IRedstoneConduit) {
                    ((IRedstoneConduit) conduit).forceConnectionMode(message.dir, message.mode);
                } else if (conduit instanceof ConduitServer) {
                    ((ConduitServer) conduit).setConnectionMode(message.dir, message.mode);
                }
            }
            return null;
        }
    }
}
