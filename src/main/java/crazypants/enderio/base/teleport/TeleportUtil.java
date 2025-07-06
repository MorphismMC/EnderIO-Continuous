package crazypants.enderio.base.teleport;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.base.sound.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

public class TeleportUtil {

    public static boolean teleport(Entity entityLiving, BlockPos pos, int targetDim,
                                     boolean conserveMotion, ITravelSource source) {
        if (entityLiving instanceof FakePlayer) return false;
        if (entityLiving.world.isRemote) {
            return !MinecraftForge.EVENT_BUS.post(new TeleportEntityEvent(entityLiving, source, pos, targetDim));
        }
        return teleportServer(entityLiving, pos, targetDim, conserveMotion, source);
    }

    public static boolean teleportServer(Entity entity, BlockPos pos, int targetDim,
                                         boolean conserveMotion, ITravelSource source) {
        if (entity instanceof FakePlayer || entity.isDead) return false;
        
        if (MinecraftForge.EVENT_BUS.post(new TeleportEntityEvent(entity, source, pos, targetDim))) {
            return false;
        }

        if (entity instanceof EntityPlayerMP player) {
            if (entity.dimension == targetDim) {
                teleportPlayerLocal(player, pos, conserveMotion, source);
            } else {
                teleportPlayerDimension(player, pos, targetDim, conserveMotion, source);
            }
        } else {
            if (entity.dimension == targetDim) {
                teleportEntityLocal(entity, pos, source);
            } else {
                teleportEntityDimension(entity, pos, targetDim, source);
            }
        }
        return true;
    }

    private static void teleportEntityLocal(Entity entity, BlockPos pos,
                                            ITravelSource source) {
        SoundHelper.playSound(entity.world, entity, source.getSound(), 1.0F, 1.0F);
        entity.world.getChunk(pos);
        entity.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5);
        entity.fallDistance = 0;
        SoundHelper.playSound(entity.world, entity, source.getSound(), 1.0F, 1.0F);
    }

    private static void teleportPlayerLocal(EntityPlayerMP player, BlockPos pos,
                                            boolean conserveMotion, ITravelSource source) {
        ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));
        ChunkTicket.loadChunk(player, player.world, pos);

        SoundHelper.playSound(player.world, player, source.getSound(), 1.0F, 1.0F);
        player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, player.rotationYaw,
                player.rotationPitch);
        player.fallDistance = 0;
        SoundHelper.playSound(player.world, player, source.getSound(), 1.0F, 1.0F);

        if (conserveMotion) {
            var velocityVex = Util.getLookVecEio(player);
            SPacketEntityVelocity p = new SPacketEntityVelocity(player.getEntityId(), velocityVex.x, velocityVex.y,
                    velocityVex.z);
            player.connection.sendPacket(p);
        }
    }

    private static void teleportEntityDimension(Entity entity, BlockPos pos, int targetDim,
                                                ITravelSource source) {
        SoundHelper.playSound(entity.world, entity, source.getSound(), 1.0F, 1.0F);
        entity.changeDimension(targetDim, (world, toTp, yaw) -> {
            toTp.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, toTp.rotationYaw,
                    toTp.rotationPitch);
            toTp.motionX = 0;
            toTp.motionY = 0;
            toTp.motionZ = 0;
            toTp.fallDistance = 0;
        });
        SoundHelper.playSound(entity.world, entity, source.getSound(), 1.0F, 1.0F);
    }

    private static void teleportPlayerDimension(EntityPlayerMP player, BlockPos pos,
                                                int targetDim,
                                                boolean conserveMotion,
                                                ITravelSource source) {
        ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));
        SoundHelper.playSound(player.world, player, source.getSound(), 1.0F, 1.0F);

        player.server.getPlayerList().transferPlayerToDimension(player, targetDim, (world, entity, yaw) -> {
            // like Forge's teleport command:
            entity.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, entity.rotationYaw,
                    entity.rotationPitch);
            // like vanilla's nether teleporter:
            ((EntityPlayerMP) entity).connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 1.1,
                    pos.getZ() + 0.5, entity.rotationYaw, entity.rotationPitch);
            // Note: Each one of the above should be enough, but there have been issues with setting the player
            // position after a dimension change, so we're doing
            // both to be on the safe side...
            entity.motionX = 0;
            entity.motionY = 0;
            entity.motionZ = 0;
            entity.fallDistance = 0;
        });

        SoundHelper.playSound(player.world, player, source.getSound(), 1.0F, 1.0F);
        ChunkTicket.loadChunk(player, player.world, BlockCoord.get(player));

        if (conserveMotion) {
            Vector3d velocityVex = Util.getLookVecEio(player);
            player.connection.sendPacket(
                    new SPacketEntityVelocity(player.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z));
        }
    }
}
