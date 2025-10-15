package crazypants.enderio.base.conduit.redstone.signals;

import com.github.bsideup.jabel.Desugar;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable redstone signal wrapper.
 * <p>
 * Please see {@link BundledSignal} and {@link CombinedSignal} for several redstone signal variants.
 *
 * @param id       The id of redstone signal.
 * @param strength The strength of redstone signal.
 */
@Desugar
public record Signal(int id, int strength) {

    public Signal(@NotNull CombinedSignal signal, int id) {
        this(signal.getStrength(), id);
    }

}
