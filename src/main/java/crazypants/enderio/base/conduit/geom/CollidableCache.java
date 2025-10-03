package crazypants.enderio.base.conduit.geom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.EnumFacing;

import crazypants.enderio.base.conduit.Conduit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CollidableCache {

    public static final CollidableCache INSTANCE = new CollidableCache();

    private final Map<CacheKey, Collection<CollidableComponent>> cache = new HashMap<>();

    @NotNull
    public CacheKey createKey(@NotNull Class<? extends Conduit> baseType,
                              @NotNull Offset offset,
                              @Nullable EnumFacing direction) {
        return new CacheKey(baseType, offset, direction);
    }

    public Collection<CollidableComponent> getCollidables(@NotNull CacheKey key, @NotNull Conduit conduit) {
        Collection<CollidableComponent> result = cache.get(key);
        if (result == null) {
            result = conduit.createCollidables(key);
            cache.put(key, result);
        }
        return result;
    }

    public static class CacheKey {

        @NotNull
        public final Class<? extends Conduit> baseType;
        @NotNull
        public final String className; // Used to generate reliable equals / hashcode.
        @NotNull
        public final Offset offset;
        @Nullable
        public final EnumFacing direction;

        public CacheKey(@NotNull Class<? extends Conduit> baseType,
                        @NotNull Offset offset,
                        @Nullable EnumFacing direction) {
            this.baseType = baseType;
            this.className = baseType.getCanonicalName();
            this.offset = offset;
            this.direction = direction;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + className.hashCode();
            result = prime * result + ((direction != null) ? direction.hashCode() : 0);
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
            CacheKey other = (CacheKey) o;
            if (!className.equals(other.className)) {
                return false;
            }
            if (direction != other.direction) {
                return false;
            }
            return offset == other.offset;
        }

    }

}
