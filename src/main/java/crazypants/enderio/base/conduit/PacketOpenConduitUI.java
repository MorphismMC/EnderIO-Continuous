package crazypants.enderio.base.conduit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

public class PacketOpenConduitUI extends MessageTileEntity<TileEntity> {

    @NotNull
    private EnumFacing direction = EnumFacing.DOWN;

    public PacketOpenConduitUI() {}

    public PacketOpenConduitUI(@NotNull TileEntity tileEntity, @NotNull EnumFacing direction) {
        super(tileEntity);
        this.direction = direction;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort(direction.ordinal());
    }

    @SuppressWarnings("null")
    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        short ord = buf.readShort();
        if (ord >= 0 && ord < EnumFacing.values().length) {
            direction = EnumFacing.values()[ord];
        }
    }

    public static class Handler implements IMessageHandler<PacketOpenConduitUI, IMessage> {

        @Override
        public IMessage onMessage(PacketOpenConduitUI message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            ConduitRegistry.getConduitModObjectNN().openGui(player.world, message.getPos(), player, message.direction,
                    message.direction.ordinal());
            return null;
        }

    }

}
