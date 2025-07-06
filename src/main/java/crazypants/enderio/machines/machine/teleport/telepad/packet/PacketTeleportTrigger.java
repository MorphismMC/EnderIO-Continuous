package crazypants.enderio.machines.machine.teleport.telepad.packet;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;

public class PacketTeleportTrigger extends MessageTileEntity<TileEntity> {

    public PacketTeleportTrigger() {
        super();
    }

    public PacketTeleportTrigger(TileTelePad te) {
        super(te.getTileEntity());
    }

    public static class Handler implements IMessageHandler<PacketTeleportTrigger, IMessage> {

        @Override
        public IMessage onMessage(PacketTeleportTrigger message, MessageContext ctx) {
            World world = message.getWorld(ctx);
            TileEntity te = message.getTileEntity(world);
            if (te instanceof TileTelePad) {
                ((TileTelePad) te).teleportAll();
            }
            return null;
        }
    }
}
