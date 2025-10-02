package crazypants.enderio.base.conduit.geom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.EnumFacing;

import crazypants.enderio.base.conduit.Conduit;
import org.jetbrains.annotations.NotNull;

public class Offsets {

    private static final Map<OffsetKey, Offset> OFFSETS = new HashMap<>();

    /**
     * Registers a set of offsets for a new conduit. (API method)
     * 
     * @param type The class of the conduit.
     * @param none The offset for the node.
     * @param x    The offset for conduit arms on the X axis.
     * @param y    The offset for conduit arms on the Y axis.
     * @param z    The offset for conduit arms on the Z axis.
     * @return     Returns {@code true} if the offset was registered, returns {@code false} if the conduit already is
     *             registered or if one of the axis is already in use.
     */
    public static boolean registerOffsets(Class<? extends Conduit> type, Offset none, Offset x, Offset y, Offset z) {
        OffsetKey keyNone = key(type, Axis.NONE);
        OffsetKey keyX = key(type, Axis.X);
        OffsetKey keyY = key(type, Axis.Y);
        OffsetKey keyZ = key(type, Axis.Z);
        if (OFFSETS.containsKey(keyNone) || OFFSETS.containsKey(keyX) || OFFSETS.containsKey(keyY) ||
                OFFSETS.containsKey(keyZ)) {
            return false;
        }
        for (Entry<OffsetKey, Offset> elem : OFFSETS.entrySet()) {
            if (elem.getKey().axis == Axis.NONE && elem.getValue() == none) {
                return false;
            }
            if (elem.getKey().axis == Axis.X && elem.getValue() == x) {
                return false;
            }
            if (elem.getKey().axis == Axis.Y && elem.getValue() == y) {
                return false;
            }
            if (elem.getKey().axis == Axis.Z && elem.getValue() == z) {
                return false;
            }
        }
        OFFSETS.put(keyNone, none);
        OFFSETS.put(keyX, x);
        OFFSETS.put(keyY, y);
        OFFSETS.put(keyZ, z);
        return true;
    }

    @NotNull
    public static Offset get(Class<? extends Conduit> type, EnumFacing direction) {
        Offset res = OFFSETS.get(key(type, getAxisForDir(direction)));
        if (res == null) {
            res = Offset.NONE;
        }
        return res;
    }

    public static OffsetKey key(Class<? extends Conduit> type, Axis axis) {
        return new OffsetKey(type, axis);
    }

    public static Axis getAxisForDir(EnumFacing direction) {
        if (direction == null) {
            return Axis.NONE;
        }
        if (direction == EnumFacing.EAST || direction == EnumFacing.WEST) {
            return Axis.X;
        }
        if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
            return Axis.Y;
        }
        if (direction == EnumFacing.NORTH || direction == EnumFacing.SOUTH) {
            return Axis.Z;
        }
        return Axis.NONE;
    }

    public enum Axis {
        NONE,
        X,
        Y,
        Z
    }

    public static class OffsetKey {

        String typeName;
        Axis axis;

        private OffsetKey(Class<? extends Conduit> type, Axis axis) {
            this.typeName = type.getCanonicalName();
            this.axis = axis;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((axis == null) ? 0 : axis.hashCode());
            result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
            OffsetKey other = (OffsetKey) o;
            if (axis != other.axis) {
                return false;
            }
            if (typeName == null) {
                return other.typeName == null;
            } else {
                return typeName.equals(other.typeName);
            }
        }

        @Override
        public String toString() {
            return "OffsetKey [typeName=" + typeName + ", axis=" + axis + "]";
        }

    }

}
