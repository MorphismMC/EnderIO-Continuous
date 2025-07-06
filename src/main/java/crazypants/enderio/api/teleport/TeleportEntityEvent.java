package crazypants.enderio.api.teleport;

import crazypants.enderio.base.teleport.TravelSourceRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * An event that can be used to respond to, edit, and prevent entity teleports.
 * <p>
 * This event will fire under all circumstances:
 * <ul>
 * <li>Travel Anchors</li>
 * <li>Staff of travelling</li>
 * <li>Telepad</li>
 * </ul>
 * <p>
 * As well as any externally added teleports, assuming they fire this event manually.
 */
@Cancelable
public class TeleportEntityEvent extends EntityEvent {

    /**
     * The target coords. These can be edited by event handlers.
     */
    private BlockPos targetPos;

    private int dimension;

    private final ITravelSource source;

    @Deprecated
    public TeleportEntityEvent(Entity entity, TravelSource source, BlockPos pos,
                               int dimension) {
        super(entity);
        this.targetPos = pos;
        this.source = source;
        this.setDimension(dimension);
    }

    /**
     * Fired before an entity teleports to the given location.
     *
     * @param entity The entity teleporting
     * @param pos    The target coord
     */
    public TeleportEntityEvent(Entity entity, ITravelSource source, BlockPos pos,
                               int dimension) {
        super(entity);
        this.targetPos = pos;
        this.source = source;
        this.setDimension(dimension);
    }

    public BlockPos getTarget() {
        return targetPos;
    }

    public void setTargetPos(BlockPos target) {
        this.targetPos = target;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Deprecated
    public TravelSource getSource() {
        return TravelSourceRegistry.getEnum(source);
    }

    public ITravelSource getTravelSource() {
        return source;
    }
}
