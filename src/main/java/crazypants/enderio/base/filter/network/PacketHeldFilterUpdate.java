package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.Filter;
import crazypants.enderio.base.filter.ItemFilterUpgrade;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;

public class PacketHeldFilterUpdate implements IMessage {

    private Filter filter;
    private int param;

    public PacketHeldFilterUpdate() {}

    public PacketHeldFilterUpdate(@Nonnull Filter filter, int param) {
        this.filter = filter;
        this.param = param;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf != null) {
            filter = FilterRegistry.readFilter(buf);
            param = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (buf != null) {
            FilterRegistry.writeFilter(buf, filter);
            buf.writeInt(param);
        }
    }

    public static class Handler implements IMessageHandler<PacketHeldFilterUpdate, IMessage> {

        @Override
        public IMessage onMessage(PacketHeldFilterUpdate message, MessageContext ctx) {
            ItemStack filterStack = ctx.getServerHandler().player
                    .getHeldItem(EnumReader.get(EnumHand.class, message.param));
            if (!filterStack.isEmpty() && filterStack.getItem() instanceof ItemFilterUpgrade) {
                FilterRegistry.writeFilterToStack(message.filter, filterStack);
            }
            return null;
        }
    }
}
