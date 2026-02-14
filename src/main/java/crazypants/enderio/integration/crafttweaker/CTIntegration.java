package crazypants.enderio.integration.crafttweaker;

import java.util.ArrayList;
import java.util.List;

public class CTIntegration {

    public static final List<Runnable> ADDITIONS = new ArrayList<>();
    public static final List<Runnable> REMOVALS = new ArrayList<>();

    public static void applyChanges() {
        REMOVALS.forEach(Runnable::run);
        ADDITIONS.forEach(Runnable::run);
        REMOVALS.clear();
        ADDITIONS.clear();
    }

}
