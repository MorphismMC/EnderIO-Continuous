package crazypants.enderio.base.teleport.packet;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.Util;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.sound.SoundHelper;
import crazypants.enderio.base.teleport.ChunkTicket;
import crazypants.enderio.base.teleport.TravelSourceRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTravelEvent implements IMessage {

    long pos;
    int powerUse;
    boolean conserveMotion;
    int source;
    int hand;

    public PacketTravelEvent() {
    }

    public PacketTravelEvent(BlockPos pos, int powerUse, boolean conserveMotion, ITravelSource source, EnumHand hand) {
        this.pos = pos.toLong();
        this.powerUse = powerUse;
        this.conserveMotion = conserveMotion;
        this.source = TravelSourceRegistry.REGISTRY.getID(source);
        this.hand = (hand == null ? EnumHand.MAIN_HAND : hand).ordinal();
    }

    @Deprecated
    public PacketTravelEvent(BlockPos pos, int powerUse, boolean conserveMotion, TravelSource source, EnumHand hand) {
        this.pos = pos.toLong();
        this.powerUse = powerUse;
        this.conserveMotion = conserveMotion;
        this.source = TravelSourceRegistry.REGISTRY.getID(source);
        this.hand = (hand == null ? EnumHand.MAIN_HAND : hand).ordinal();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos);
        buf.writeInt(powerUse);
        buf.writeBoolean(conserveMotion);
        buf.writeInt(source);
        buf.writeInt(hand);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = buf.readLong();
        powerUse = buf.readInt();
        conserveMotion = buf.readBoolean();
        source = buf.readInt();
        hand = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketTravelEvent, IMessage> {

        @Override
        public IMessage onMessage(PacketTravelEvent message, MessageContext ctx) {
            var source = NullHelper.notnullJ(TravelSourceRegistry.REGISTRY.getValue(message.source),
                    "ForgeRegistry.getValue(int)");
            var hand = EnumHand.values()[message.hand];
            var player = ctx.getServerHandler().player;
            var target = BlockPos.fromLong(message.pos);
            int powerUse = message.powerUse;

            // post event
            var event = new TeleportEntityEvent(player, source, target, player.dimension);
            if (MinecraftForge.EVENT_BUS.post(event)) return null;
            target = event.getTarget();

            // load chunks
            ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));
            ChunkTicket.loadChunk(player, player.world, target);

            // play sound
            SoundHelper.playSound(player.world, player, source.getSound(), 1.0F, 1.0F);
            // teleport
            player.setPositionAndUpdate(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
            player.fallDistance = 0;
            // send velocity
            if (message.conserveMotion) {
                var velocityVex = Util.getLookVecEio(player);
                var packetVelocity = new SPacketEntityVelocity(player.getEntityId(),
                        velocityVex.x, velocityVex.y, velocityVex.z);
                player.connection.sendPacket(packetVelocity);
            }
            // consume power
            if (powerUse > 0) {
                ItemStack heldItem = player.getHeldItem(hand);
                if (heldItem.getItem() instanceof IItemOfTravel itemOfTravel) {
                    var copy = heldItem.copy();
                    itemOfTravel.extractInternal(copy, powerUse);
                    player.setHeldItem(hand, copy);
                }
            }
            // play sound
            SoundHelper.playSound(player.world, player, source.getSound(), 1.0F, 1.0F);
            return null;
        }
    }
}
