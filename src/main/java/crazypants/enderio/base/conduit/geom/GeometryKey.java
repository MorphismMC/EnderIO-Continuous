package crazypants.enderio.base.conduit.geom;

import net.minecraft.util.EnumFacing;

import crazypants.enderio.base.conduit.Conduit;
import org.jetbrains.annotations.NotNull;

public final class GeometryKey {

    @NotNull
    public final Offset offset;
    public final String className;
    public final EnumFacing direction;

    public GeometryKey(EnumFacing direction,
                       @NotNull Offset offset,
                       Class<? extends Conduit> type) {
        this.direction = direction;
        this.offset = offset;
        this.className = type != null ? type.getName() : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((direction == null) ? 0 : direction.hashCode());
        result = prime * result + offset.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        GeometryKey other = (GeometryKey) o;
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        if (direction != other.direction) {
            return false;
        }
        return offset == other.offset;
    }

}
