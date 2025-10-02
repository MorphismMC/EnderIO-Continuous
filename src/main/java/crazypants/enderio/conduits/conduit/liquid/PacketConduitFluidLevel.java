package crazypants.enderio.conduits.conduit.liquid;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import crazypants.enderio.conduits.network.AbstractConduitPacket;
import io.netty.buffer.ByteBuf;

public class PacketConduitFluidLevel extends AbstractConduitPacket<LiquidConduit> {

    public NBTTagCompound tc;

    public PacketConduitFluidLevel() {}

    public PacketConduitFluidLevel(@Nonnull LiquidConduit conduit) {
        super(conduit);
        tc = new NBTTagCompound();
        conduit.writeToNBT(tc);
    }

    @Override
    public void write(@Nonnull ByteBuf buf) {
        super.write(buf);
        ByteBufUtils.writeTag(buf, tc);
    }

    @Override
    public void read(@Nonnull ByteBuf buf) {
        super.read(buf);
        tc = ByteBufUtils.readTag(buf);
    }

    public static class Handler implements IMessageHandler<PacketConduitFluidLevel, IMessage> {

        @Override
        public IMessage onMessage(PacketConduitFluidLevel message, MessageContext ctx) {
            final NBTTagCompound nbt = message.tc;
            final LiquidConduit conduit = message.getConduit(ctx);
            if (nbt != null && conduit != null) {
                conduit.readFromNBT(nbt);
            }
            return null;
        }
    }
}
