package crazypants.enderio.base.teleport;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.teleport.packet.PacketTravelEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Random;

public class TravelUtil {

    private static final Random RAND = new Random();

    /**
     * Client only method.
     * Note: This is restricted to the current player
     *
     * @param player           The player to be traveling
     * @param hand             The hand holding the travel item e.g. travel staff
     * @param dest             The destination
     * @param source           The travel source
     * @param consumePower     Whether to consume power
     * @param conserveMomentum Whether to conserve momentum
     * @return true if successful
     */
    public static boolean travelTo(EntityPlayer player, EnumHand hand, BlockPos dest,
                                   ITravelSource source, boolean consumePower, boolean conserveMomentum) {
        // check player
        if (player instanceof FakePlayer || player.isDead) return false;

        // check authorization
        var tileEntity = player.world.getTileEntity(dest);
        if (tileEntity instanceof ITravelAccessable accessable && !accessable.canBlockBeAccessed(player)) {
            player.sendMessage(Lang.GUI_TRAVEL_UNAUTHORIZED.toChatServer());
            return false;
        }

        // check range
        if (!isInRangeTarget(player, dest, source.getMaxDistanceTravelledSq())) {
            player.sendStatusMessage(Lang.GUI_TRAVEL_OUT_OF_RANGE.toChatServer(), true);
            return false;
        }

        // check target
        if (!isValidTarget(player, dest)) {
            player.sendStatusMessage(Lang.GUI_TRAVEL_INVALID_TARGET.toChatServer(), true);
            return false;
        }

        // check power
        int requiredPower = 0;
        if (consumePower) {
            var equipped = player.getHeldItem(hand);
            if (!isTravelItemActive(player, equipped)) return false;
            requiredPower = getRequiredPower(player, dest, source);
            if (requiredPower > getEnergyInTravelItem(equipped)) {
                player.sendStatusMessage(Lang.STAFF_NO_POWER.toChat(), true);
                return false;
            }
        }
        return doTravel(player, hand, dest, source, requiredPower, conserveMomentum);
    }

    public static boolean isValidTarget(EntityPlayer player, BlockPos targetPos) {
        // Do not teleport into the Void
        if (targetPos.getY() < 1) return false;
        var world = player.world;
        for (var pos : getAllPosInPlayer(player, targetPos)) {
            if (world.getBlockState(pos).causesSuffocation()) return false;
        }
        return true;
    }

    private static Iterable<BlockPos.MutableBlockPos> getAllPosInPlayer(EntityPlayer player, BlockPos targetPos) {
        double targetX = targetPos.getX() + 0.5;
        double targetY = targetPos.getY();
        double targetZ = targetPos.getZ() + 0.5;

        int x1 = MathHelper.floor(targetX - player.width / 2);
        int y1 = MathHelper.floor(targetY);
        int z1 = MathHelper.floor(targetZ - player.width / 2);

        int x2 = MathHelper.floor(targetX + player.width / 2);
        int y2 = MathHelper.floor(targetY + player.height);
        int z2 = MathHelper.floor(targetZ + player.width / 2);

        return BlockPos.getAllInBoxMutable(x1, y1, z1, x2, y2, z2);
    }

    private static boolean doTravel(EntityPlayer player, EnumHand hand, BlockPos dest,
                                    ITravelSource source, int powerUsed, boolean conserveMomentum) {
        // post travel event
        var event = new TeleportEntityEvent(player, source, dest, player.dimension);
        if (MinecraftForge.EVENT_BUS.post(event)) return false;
        // send travel packet
        var packet = new PacketTravelEvent(event.getTarget(), powerUsed, conserveMomentum, source, hand);
        PacketHandler.INSTANCE.sendToServer(packet);
        // spawn particles
        if (PersonalConfig.machineParticlesEnabled.get()) {
            for (int i = 0; i < 6; ++i) {
                player.world.spawnParticle(EnumParticleTypes.PORTAL,
                        player.posX + (RAND.nextDouble() - 0.5),
                        player.posY + RAND.nextDouble() * player.height - 0.25,
                        player.posZ + (RAND.nextDouble() - 0.5),
                        (RAND.nextDouble() - 0.5) * 2.0,
                        -RAND.nextDouble(),
                        (RAND.nextDouble() - 0.5) * 2.0);
            }
        }
        return true;
    }

    public static int getRequiredPower(EntityPlayer player, BlockPos dest, ITravelSource source) {
        return (int) (getDistance(player, dest) * source.getPowerCostPerBlockTraveledRF());
    }

    private static boolean isInRangeTarget(EntityPlayer player, BlockPos bc, float maxSq) {
        return getDistanceSquared(player, bc) <= maxSq;
    }

    private static double getDistanceSquared(EntityPlayer player, BlockPos bc) {
        Vector3d eye = Util.getEyePositionEio(player);
        Vector3d target = new Vector3d(bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5);
        return eye.distanceSquared(target);
    }

    private static double getDistance(EntityPlayer player, BlockPos coord) {
        return Math.sqrt(getDistanceSquared(player, coord));
    }

    /*  -------------------------------------------------- Blink -------------------------------------------------- */

    public static boolean blink(EntityPlayer player, EnumHand hand, ITravelSource travelSource) {
        var equipped = player.getHeldItem(hand);
        // check offhand
        if (!TeleportConfig.enableOffHandBlink.get() && hand == EnumHand.OFF_HAND) return false;
        // check travel item
        if (!TravelUtil.isTravelItemActive(player, equipped)) return false;

        double maxDistance = travelSource.getMaxDistanceTravelled();
        boolean canBlinkSolidBlocks = TeleportConfig.enableBlinkSolidBlocks.get();
        var eyePos = Util.getEyePosition(player);
        var feetPos = Util.getEyePosition(player).subtract(0, player.getEyeHeight(), 0);
        var lookAt = player.getLookVec(); // normalize vector
        var endPos = lookAt.scale(maxDistance * 8).add(eyePos); // maximum distance * 8, like EIOu(GTNH)

        var hitResult = player.world.rayTraceBlocks(eyePos, endPos, !canBlinkSolidBlocks);
        if (hitResult == null) {
            return tryBlinkFarest(player, hand, equipped, feetPos, lookAt, maxDistance, travelSource, true);
        } else {
            var minDistance = eyePos.distanceTo(hitResult.hitVec) + lookAt.y * player.getEyeHeight() + 1.5;
            maxDistance = minDistance + maxDistance / 4; // limit distance through the wall to 1/4 maximum distance
            endPos = lookAt.scale(maxDistance).add(eyePos);
            var results = Util.raytraceAll(player.world, eyePos, endPos, !canBlinkSolidBlocks);
            for (RayTraceResult result : results) {
                if (result == null) continue;
                var hitPos = result.getBlockPos();
                var hitBlock = player.world.getBlockState(hitPos);
                if (!canBlinkThrough(player.world, hitPos, hitBlock)) {
                    var targetPos = new Vec3d(
                            hitPos.getX() + 0.5,
                            hitPos.getY() + 0.5,
                            hitPos.getZ() + 0.5
                    );
                    maxDistance = eyePos.distanceTo(targetPos) - 1.5;
                    break;
                }
            }
            if (tryBlinkNearest(player, hand, equipped, feetPos, lookAt, minDistance, maxDistance, travelSource, false)) {
                return true;
            }
            return tryBlinkFarest(player, hand, equipped, feetPos, lookAt, minDistance, travelSource, false);
        }
    }

    private static boolean tryBlinkFarest(EntityPlayer player, EnumHand hand, ItemStack equipped,
                                          Vec3d feetPos, Vec3d lookAt, double maxDistance,
                                          ITravelSource source, boolean conserveMomentum) {
        // use mutable vector for performance
        var sample = new Vector3d();
        // go as far as possible
        while (maxDistance > 1) {
            sample.set(feetPos.x, feetPos.y, feetPos.z);
            sample.add(
                    lookAt.x * maxDistance,
                    lookAt.y * maxDistance,
                    lookAt.z * maxDistance);
            if (blinkAround(player, hand, equipped, sample, source, conserveMomentum)) {
                return true;
            }
            maxDistance--;
        }
        return false;
    }

    private static boolean tryBlinkNearest(EntityPlayer player, EnumHand hand, ItemStack equipped,
                                           Vec3d feetPos, Vec3d lookAt, double minDistance, double maxDistance,
                                           ITravelSource source, boolean conserveMomentum) {
        // use mutable vector for performance
        var sample = new Vector3d();
        // go as near as possible
        while (maxDistance > minDistance) {
            sample.set(feetPos.x, feetPos.y, feetPos.z);
            sample.add(
                    lookAt.x * minDistance,
                    lookAt.y * minDistance,
                    lookAt.z * minDistance);
            if (blinkAround(player, hand, equipped, sample, source, conserveMomentum)) {
                return true;
            }
            minDistance++;
        }
        return false;
    }

    private static boolean blinkAround(EntityPlayer player, EnumHand hand, ItemStack equipped, Vector3d dest,
                                       ITravelSource source, boolean conserveMomentum) {
        var destPos = new BlockPos(Math.floor(dest.x), Math.floor(dest.y), Math.floor(dest.z));
        if (blinkTo(player, hand, equipped, destPos, source, conserveMomentum)) {
            return true;
        }
        if (blinkTo(player, hand, equipped, destPos.up(), source, conserveMomentum)) {
            return true;
        }
        return blinkTo(player, hand, equipped, destPos.down(), source, conserveMomentum);
    }

    private static boolean blinkTo(EntityPlayer player, EnumHand hand, ItemStack equipped, BlockPos dest,
                                   ITravelSource source, boolean conserveMomentum) {
        if (!isValidTarget(player, dest)) return false;
        int requiredPower = getRequiredPower(player, dest, source);
        if (requiredPower > getEnergyInTravelItem(equipped)) {
            player.sendStatusMessage(Lang.STAFF_NO_POWER.toChat(), true);
            return false;
        }
        return doTravel(player, hand, dest, source, requiredPower, conserveMomentum);
    }

    private static boolean canBlinkThrough(World world, BlockPos pos, IBlockState hitBlock) {
        boolean isBlackListed = TeleportConfig.blockBlacklist.get().contains(hitBlock.getBlock());
        boolean isUnbreakable = hitBlock.getBlockHardness(world, pos) < 0;
        return !isBlackListed && !(isUnbreakable && !TeleportConfig.enableBlinkUnbreakableBlocks.get());
    }

    /*  -------------------------------------------------- Item Utils -------------------------------------------------- */

    public static boolean isTravelItemActive(EntityPlayer ep, ItemStack equipped) {
        if (equipped.getItem() instanceof IItemOfTravel itemOfTravel) {
            return itemOfTravel.isActive(ep, equipped);
        }
        return false;
    }

    public static boolean isTravelItemActive(EntityPlayer ep, EnumHand hand) {
        return isTravelItemActive(ep, ep.getHeldItem(hand));
    }

    public static boolean isTravelItemActive(EntityPlayer ep) {
        return isTravelItemActive(ep, ep.getHeldItemMainhand()) || isTravelItemActive(ep, ep.getHeldItemOffhand());
    }

    public static int getEnergyInTravelItem(ItemStack equipped) {
        if (equipped.getItem() instanceof IItemOfTravel itemOfTravel) {
            return itemOfTravel.getEnergyStored(equipped);
        }
        return 0;
    }

    public static boolean doesAllowTravel(EntityPlayer ep, EnumHand hand) {
        return (TeleportConfig.enableOffHandTravel.get() || hand == EnumHand.MAIN_HAND) && isTravelItemActive(ep, hand);
    }

    public static boolean doesAllowTravel(EntityPlayer ep) {
        return doesAllowTravel(ep, EnumHand.MAIN_HAND) || doesAllowTravel(ep, EnumHand.OFF_HAND);
    }
}
