package crazypants.enderio.base.conduit.geom;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.util.EnumFacing;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.Conduit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Desugar
public record CollidableComponent(@Nullable Class<? extends Conduit> conduitType,
                                  @NotNull BoundingBox bound,
                                  @Nullable EnumFacing direction,
                                  @Nullable Object data) {

    @NotNull
    @Override
    public EnumFacing direction() {
        return NullHelper.notnull(direction, "core element is not directional");
    }

    public boolean isDirectional() {
        return direction != null;
    }

    public boolean isCore() {
        return direction == null;
    }

    @NotNull
    @Override
    public String toString() {
        return "CollidableComponent [conduitType=" + conduitType + ", bound=" + bound + ", id=" + direction + "]";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof CollidableComponent other) {
            return conduitType == other.conduitType
                    && bound.equals(other.bound)
                    && direction == other.direction;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bound.hashCode();
        final Class<?> cls = conduitType;
        result = prime * result + (cls == null ? 0 : cls.getName().hashCode());
        final EnumFacing f = direction;
        result = prime * result + (f == null ? 0 : f.hashCode());
        return result;
    }

}
