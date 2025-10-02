package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import crazypants.enderio.base.conduit.geom.CollidableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Desugar
public record RaytraceResult(@NotNull CollidableComponent component, @NotNull RayTraceResult movingObjectPosition) {

    @Nullable
    public static RaytraceResult getClosestHit(@NotNull Vec3d origin,
                                               @NotNull Collection<RaytraceResult> candidates) {
        double minLengthSquared = Double.POSITIVE_INFINITY;
        RaytraceResult closest = null;

        for (RaytraceResult candidate : candidates) {
            double lengthSquared = candidate.movingObjectPosition.hitVec.squareDistanceTo(origin);
            if (lengthSquared < minLengthSquared) {
                minLengthSquared = lengthSquared;
                closest = candidate;
            }
        }
        return closest;
    }

    @NotNull
    public static List<RaytraceResult> sort(final @NotNull Vec3d origin,
                                            @NotNull List<RaytraceResult> candidatesToSort) {
        candidatesToSort.sort(Comparator.comparingDouble(o -> o.getDistanceTo(origin)));
        return candidatesToSort;
    }

    public double getDistanceTo(@NotNull Vec3d origin) {
        return movingObjectPosition.hitVec.squareDistanceTo(origin);
    }

}
