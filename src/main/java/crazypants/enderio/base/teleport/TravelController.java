package crazypants.enderio.base.teleport;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.teleport.packet.PacketOpenAuthGui;
import crazypants.enderio.util.Mods;
import journeymap.client.model.Waypoint;
import journeymap.client.waypoint.WaypointStore;
import journeymap.common.Journeymap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = EnderIO.MODID, value = Side.CLIENT)
public class TravelController {

    // CAREFUL: missAngle MUST BE >= hitAngle
    // Miss scale for blocks with an angle above this threshold
    public static final double CANDIDATE_MISS_ANGLE = Math.PI / 5;

    // CAREFUL: missAngle MUST BE >= hitAngle
    // Hit scale for blocks with an angle below this threshold
    public static final double CANDIDATE_HIT_ANGLE = 0.087; // == ~5 degree

    private static boolean wasJumping = false;

    private static boolean wasSneaking = false;

    private static boolean tempJump;

    private static boolean tempSneak;

    private static boolean showTargets = false;

    /**
     * The position of ITravelAccessable block the player is standing on
     * Cached per tick for rendering use
     */
    @Nullable
    private static BlockPos onBlockCoord;

    /**
     * The position of the selected ITravelAccessable block
     */
    @Nullable
    private static BlockPos selectedCoord;

    private static double fovRad;

    private static boolean selectionEnabled = true;

    private static final HashSet<BlockPos> candidates = new HashSet<>();

    public static boolean activateTravelAccessable(ItemStack equipped, EnumHand hand, World world, EntityPlayer player, ITravelSource source) {
        BlockPos target = selectedCoord;
        if (target == null) {
            return false;
        }
        TileEntity te = world.getTileEntity(target);
        if (te instanceof ITravelAccessable ta) {
            if (ta.getRequiresPassword(player)) {
                PacketOpenAuthGui p = new PacketOpenAuthGui(target);
                PacketHandler.INSTANCE.sendToServer(p);
                return true;
            }
        }

        if (TravelUtil.doesAllowTravel(player, hand) || selectedCoord != null) {
            TravelUtil.travelTo(player, hand, selectedCoord.up(), source, true, false);
            return true;
        }
        return true;
    }

    public static boolean showTargets() {
        return showTargets && selectionEnabled;
    }

    public static void setSelectionEnabled(boolean b) {
        selectionEnabled = b;
        if (!selectionEnabled) {
            candidates.clear();
        }
    }

    public static boolean isBlockSelected(BlockPos coord) {
        return coord.equals(selectedCoord);
    }

    public static void addCandidate(BlockPos coord) {
        candidates.add(coord);
    }

    @Nullable
    public static BlockPos getPosPlayerOn() {
        return onBlockCoord;
    }

    /*  -------------------------------------------------- Event Handler -------------------------------------------------- */

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event) {
        fovRad = Math.toRadians(Minecraft.getMinecraft().gameSettings.fovSetting);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            var player = Minecraft.getMinecraft().player;
            if (player == null) return;
            if (player.isSpectator()) {
                showTargets = false;
                candidates.clear();
                return;
            }

            var pair = getActiveTravelBlock(player);
            onBlockCoord = pair != null ? pair.getLeft() : null;
            boolean onBlock = onBlockCoord != null;
            showTargets = onBlock || TravelUtil.isTravelItemActive(player);
            if (showTargets) {
                updateSelectedTarget(player);
            } else {
                selectedCoord = null;
            }
            MovementInput input = player.movementInput;
            if (input == null) {
                return;
            }
            tempJump = input.jump;
            tempSneak = input.sneak;

            // Handles teleportation if a target is selected
            if ((input.jump && !wasJumping && onBlock && selectedCoord != null && TeleportConfig.activateJump.get()) ||
                    (input.sneak && !wasSneaking && onBlock && selectedCoord != null &&
                            TeleportConfig.activateSneak.get())) {

                onInput(player);
            }

            // Elevator: If there is no selected coordinate and the input is jump, go up
            if (input.jump && !wasJumping && onBlock && selectedCoord == null) {
                updateVerticalTarget(player, 1);
                onInput(player);
            }

            // Elevator: If there is no selected coordinate and the input is sneak, go down
            if (input.sneak && !wasSneaking && onBlock && selectedCoord == null) {
                updateVerticalTarget(player, -1);
                onInput(player);
            }

            wasJumping = tempJump;
            wasSneaking = tempSneak;
            candidates.clear();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void updateVerticalTarget(EntityPlayerSP player, int direction) {
        Pair<BlockPos, ITravelAccessable> pair = getActiveTravelBlock(player);
        BlockPos currentBlock = pair.getKey();
        World world = Minecraft.getMinecraft().world;
        for (int i = 0, y = currentBlock.getY() + direction; i < pair.getValue().getTravelRangeDeparting() && y >= 0 &&
                y <= 255; i++, y += direction) {

            // Circumvents the raytracing used to find candidates on the y axis
            TileEntity selectedBlock = world.getTileEntity(new BlockPos(currentBlock.getX(), y, currentBlock.getZ()));

            if (selectedBlock instanceof ITravelAccessable travelBlock) {
                BlockPos targetBlock = new BlockPos(currentBlock.getX(), y, currentBlock.getZ());

                if (travelBlock.canBlockBeAccessed(player) && TravelUtil.isValidTarget(player, targetBlock)) {
                    selectedCoord = targetBlock;
                    return;
                } else if (travelBlock.getRequiresPassword(player)) {
                    player.sendStatusMessage(Lang.GUI_TRAVEL_SKIP_LOCKED.toChatServer(), true);
                } else if (travelBlock.getAccessMode() == ITravelAccessable.AccessMode.PRIVATE &&
                        !travelBlock.canUiBeAccessed(player)) {
                    player.sendStatusMessage(Lang.GUI_TRAVEL_SKIP_PRIVATE.toChatServer(), true);
                } else if (!TravelUtil.isValidTarget(player, targetBlock)) {
                    player.sendStatusMessage(Lang.GUI_TRAVEL_SKIP_OBSTRUCTED.toChatServer(), true);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void onInput(EntityPlayerSP player) {
        MovementInput input = player.movementInput;
        BlockPos target = selectedCoord;
        if (target == null) {
            return;
        }

        TileEntity te = player.world.getTileEntity(target);
        if (te instanceof ITravelAccessable) {
            ITravelAccessable ta = (ITravelAccessable) te;
            if (ta.getRequiresPassword(player)) {
                PacketOpenAuthGui p = new PacketOpenAuthGui(target);
                PacketHandler.INSTANCE.sendToServer(p);
                return;
            }
        }


        if (selectedCoord != null
                && TravelUtil.travelTo(player, EnumHand.MAIN_HAND, selectedCoord, TravelSource.BLOCK, false, false)) {
            input.jump = false;
            try {
                ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, (EntityPlayer) player, 0,
                        "flyToggleTimer", "field_71101_bC");
            } catch (Exception ignore) {
                // ignore
            }
        }
    }

    @Nullable
    private static Pair<BlockPos, ITravelAccessable> getActiveTravelBlock(EntityPlayerSP player) {
        var world = Minecraft.getMinecraft().world;
        if (world == null) {
            // Log.warn("(in TickEvent.ClientTickEvent) net.minecraft.client.Minecraft.world is marked but it
            // is null.");
            return null;
        }
        int x = MathHelper.floor(player.posX);
        int y = MathHelper.floor(player.getEntityBoundingBox().minY) - 1;
        int z = MathHelper.floor(player.posZ);
        final BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof ITravelAccessable travelAccessable
                && travelAccessable.isTravelSource()) {
            return Pair.of(pos, travelAccessable);
        }
        return null;
    }

    private static void updateSelectedTarget(EntityPlayerSP player) {
        selectedCoord = null;

        double closestDistance = Double.MAX_VALUE;
        for (BlockPos bc : candidates) {
            // Exclude the block the player is standing on
            if (!bc.equals(onBlockCoord)) {
                Vector3d loc = new Vector3d(bc.getX() + 0.5, bc.getY() + 0.5, bc.getZ() + 0.5);
                double[] distanceAndAngle = getCandidateDistanceAndAngle(player, loc);
                double distance = distanceAndAngle[0];
                double angle = distanceAndAngle[1];

                if (distance < closestDistance && angle < CANDIDATE_HIT_ANGLE) {
                    // Valid hit, sorted by distance.
                    selectedCoord = bc;
                    closestDistance = distance;
                }
            }
        }

        if (Mods.JourneyMap.isModLoaded()) {
            var waypoints = WaypointStore.INSTANCE.getAll();
            int playerDim = player.dimension;
            var eyePos = Util.getEyePosition(player);
            for (Waypoint waypoint : waypoints) {
                if (waypoint.isEnable() && waypoint.getDimensions().contains(playerDim)) {
                    var target = waypoint.getBlockPos();
                    var targetVec = new Vec3d(target.getX(), target.getY(), target.getZ());
                    double distance = targetVec.distanceTo(eyePos);
                    if (distance < closestDistance && isSelectedWaypoint(waypoint)) {
                        // Valid hit, sorted by distance.
                        selectedCoord = target;
                        closestDistance = distance;
                    }
                }
            }
        }
    }

    private static boolean isSelectedWaypoint(Object waypointObj) {
        if (!(waypointObj instanceof Waypoint waypoint)) return false;
        var mc = Minecraft.getMinecraft();
        var player = mc.player;
        var properties = Journeymap.getClient().getWaypointProperties();
        var playerPos = new Vec3d(player.posX, player.posY, player.posZ);
        var waypointPos = waypoint.getPosition().add(0.0F, 0.118, 0.0F);

        double actualDistance = playerPos.distanceTo(waypointPos);
        int maxDistance = properties.maxDistance.get();
        int minDistance = properties.minDistance.get();
        if (maxDistance > 0 && actualDistance > maxDistance) return false;
        if (minDistance > 0 && actualDistance < minDistance) return false;

        double viewDistance = actualDistance;
        double maxRenderDistance = mc.gameSettings.renderDistanceChunks * 16;
        if (actualDistance > maxRenderDistance) {
            var delta = waypointPos.subtract(playerPos).normalize();
            waypointPos = playerPos.add(
                    delta.x * maxRenderDistance,
                    delta.y * maxRenderDistance,
                    delta.z * maxRenderDistance);
            viewDistance = maxRenderDistance;
        }

        if (viewDistance > 0.5F) {
            int angle = 5;
            double yaw = MathHelper.wrapDegrees(Math.toDegrees(
                    Math.atan2(player.posZ - waypointPos.z, player.posX - waypointPos.x)) + 90.0F);
            double playerYaw = MathHelper.wrapDegrees(player.getRotationYawHead());
            return Math.abs(yaw - playerYaw) <= angle;
        }
        return false;
    }

    public static double[] getCandidateDistanceAndAngle(EntityPlayerSP player, Vector3d loc) {
        Vector3d eye = Util.getEyePositionEio(player);
        Vector3d look = Util.getLookVecEio(player);
        Vector3d relativeBlock = new Vector3d(loc);

        relativeBlock.sub(eye);
        double distance = relativeBlock.length();
        relativeBlock.normalize();

        // Angle in [0,pi]
        double angle = Math.acos(look.dot(relativeBlock));
        return new double[]{distance, angle};
    }

    public static double getCandidateHitScale(double fullScreenScaling, double distance) {
        // Take 10% of the screen width per default as the maximum scale for hits (perfectly looking at block)
        return 0.10 * fullScreenScaling;
    }

    public static double getCandidateMissScale(double fullScreenScaling, double distance) {
        // At least 1.5 times the normal block size if the angle is not close to the block
        return 1.5;
    }

    public static double getScaleForCandidate(EntityPlayerSP player, Vector3d loc, int maxDistanceSq) {
        // Retrieve the candidate distance and angle
        double[] distanceAndAngle = getCandidateDistanceAndAngle(player, loc);
        double distance = distanceAndAngle[0];
        double angle = distanceAndAngle[1];

        // To get screen relative scaling, normalize based on fov and
        // distance (this scaling factor would cause the block to be displayed
        // horizontally fitted to the screen)
        double fullScreenScaling = Math.tan(fovRad / 2) * 2 * distance;

        double scaleHit = getCandidateHitScale(fullScreenScaling, distance);
        double scaleMiss = getCandidateMissScale(fullScreenScaling, distance);

        double hitAngle = CANDIDATE_HIT_ANGLE;
        double missAngle = CANDIDATE_MISS_ANGLE;

        // Always apply configuration scaling factor
        // FIXME: The (1/.2) is there because currently .2 is the default value for this config
        // and this new algorithm needs 1 to be the base value. Maybe it is best to change
        // the default config value and remove this factor. The then the config represents
        // intuitive scaling (1.0 = 100%)
        double scale = (1 / .2) * TeleportConfig.visualScale.get();

        // Now we will scale according to the angle:
        // scaleHit for [0,hitAngle)
        // interpolate(scaleHit, scaleMiss) for [hitAngle,missAngle)
        // scaleMiss for [missAngle,pi]
        if (angle < hitAngle) {
            scale *= scaleHit;
        } else if (angle >= hitAngle && angle < missAngle) {
            double lerp = (angle - hitAngle) / (missAngle - hitAngle);
            scale *= scaleHit + lerp * (scaleMiss - scaleHit);
        } else {
            scale *= scaleMiss;
        }

        return scale;
    }
}
