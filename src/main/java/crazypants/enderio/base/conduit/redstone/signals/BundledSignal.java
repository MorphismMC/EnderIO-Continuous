package crazypants.enderio.base.conduit.redstone.signals;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;
import org.jetbrains.annotations.NotNull;

public class BundledSignal {

    @NotNull
    private final Map<DyeColor, CombinedSignal> bundle;
    @NotNull
    private final Map<DyeColor, Map<Integer, Signal>> bundleSignals;

    public BundledSignal() {
        bundle = new EnumMap<>(DyeColor.class);
        bundleSignals = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            bundle.put(color, new CombinedSignal(0));
            bundleSignals.put(color, new HashMap<>());
        }
    }

    @NotNull
    public CombinedSignal getSignal(@NotNull DyeColor color) {
        return bundle.get(color);
    }

    public void addSignal(@NotNull DyeColor color, @NotNull Signal signal) {
        Map<Integer, Signal> signalMap = bundleSignals.get(color);
        if (!signalMap.containsKey(signal.id())) {
            signalMap.put(signal.id(), signal);
        } else if (signalMap.get(signal.id()).strength() != signal.strength()) {
            signalMap.put(signal.id(), signal);
        }

        int str = 0;
        for (Signal sig : signalMap.values()) {
            str += sig.strength();
            if (str >= 15) {
                str = 15;
                break;
            }
        }

        bundle.get(color).setStrength(str);
    }

    public void set(@NotNull DyeColor color, @NotNull CombinedSignal signal) {
        bundle.put(color, signal);
    }

    public void reset(@NotNull DyeColor color) {
        bundle.remove(color);
    }

    @NotNull
    public CombinedSignal getFilteredSignal(@NotNull DyeColor color, @NotNull IOutputSignalFilter filter) {
        return filter.apply(color, this);
    }

    @NotNull
    public Collection<CombinedSignal> getSignals() {
        return bundle.values();
    }

    public void clear() {
        for (CombinedSignal sig : bundle.values()) {
            sig.setStrength(0);
        }
    }

}
