package crazypants.enderio.base.filter.network;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.Filter;
import crazypants.enderio.base.filter.TileFilterContainer;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import io.netty.buffer.ByteBuf;

public class PacketFilterUpdate extends MessageTileEntity<TileEntity> {

    protected int filterId;
    protected int param1;
    protected Filter filter;

    public PacketFilterUpdate() {}

    public PacketFilterUpdate(@Nonnull TileEntity te, @Nonnull Filter filter, int filterId, int param1) {
        super(te);
        this.filter = filter;
        this.filterId = filterId;
        this.param1 = param1;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(filterId);
        buf.writeInt(param1);
        FilterRegistry.writeFilter(buf, filter);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        filterId = buf.readInt();
        param1 = buf.readInt();
        filter = FilterRegistry.readFilter(buf);
    }

    public TileFilterContainer getFilterContainer(MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            if (ctx.getServerHandler().player.openContainer instanceof ContainerFilter) {
                final TileEntity tileEntity = ((ContainerFilter) ctx.getServerHandler().player.openContainer)
                        .getTileEntity();
                if (tileEntity == null || !tileEntity.getPos().equals(getPos())) {
                    Log.warn("Player " + ctx.getServerHandler().player.getName() +
                            " tried to manipulate a filter while another gui was open!");
                    return null;
                } else {
                    if (tileEntity instanceof TileFilterContainer) {
                        return (TileFilterContainer) tileEntity;
                    }
                }
            }
        }
        return null;
    }

    public static class Handler implements IMessageHandler<PacketFilterUpdate, IMessage> {

        @Override
        public IMessage onMessage(PacketFilterUpdate message, MessageContext ctx) {
            TileFilterContainer filterContainer = message.getFilterContainer(ctx);
            if (filterContainer != null && message.filter != null) {
                filterContainer.setFilter(message.filterId, message.param1, message.filter);
            }
            return null;
        }
    }
}
