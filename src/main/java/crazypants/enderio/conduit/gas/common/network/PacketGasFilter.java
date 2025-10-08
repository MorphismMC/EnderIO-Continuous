package crazypants.enderio.conduit.gas.common.network;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import crazypants.enderio.conduits.network.AbstractConduitPacket;
import crazypants.enderio.conduit.gas.common.conduit.GasConduit;
import crazypants.enderio.conduit.gas.common.conduit.ender.EnderGasConduitImpl;
import crazypants.enderio.conduit.gas.common.filter.GasFilterImpl;
import crazypants.enderio.conduit.gas.common.filter.GasFilter;
import io.netty.buffer.ByteBuf;

public class PacketGasFilter extends AbstractConduitPacket.Sided<GasConduit> {

    private boolean isInput;
    @Nonnull
    private GasFilter filter = new GasFilterImpl();

    public PacketGasFilter() {}

    public PacketGasFilter(EnderGasConduitImpl eConduit, @Nonnull EnumFacing dir, @Nonnull GasFilter filter,
                           boolean isInput) {
        super(eConduit, dir);
        this.filter = filter;
        this.isInput = isInput;
    }

    @Override
    public void write(@Nonnull ByteBuf buf) {
        super.write(buf);
        buf.writeBoolean(isInput);
        NBTTagCompound tag = new NBTTagCompound();
        filter.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public void read(@Nonnull ByteBuf buf) {
        super.read(buf);
        isInput = buf.readBoolean();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if (tag != null) {
            filter.readFromNBT(tag);
        }
    }

    public static class Handler implements IMessageHandler<PacketGasFilter, IMessage> {

        @Override
        public IMessage onMessage(PacketGasFilter message, MessageContext ctx) {
            GasConduit conduit = message.getConduit(ctx);
            if (conduit instanceof EnderGasConduitImpl) {
                ((EnderGasConduitImpl) conduit).setFilter(message.dir, message.filter, message.isInput);
                World world = message.getWorld(ctx);
                IBlockState bs = world.getBlockState(message.getPos());
                world.notifyBlockUpdate(message.getPos(), bs, bs, 3);
            }
            return null;
        }
    }
}
