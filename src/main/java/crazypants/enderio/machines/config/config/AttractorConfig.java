package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class AttractorConfig {

    public static final IValueFactory F = Config.F.section("attractor");

    public static final IValue<Integer> maxMobsAttracted = F.make("maxMobsAttracted", 20, //
            "Maximum number of mobs any Attraction Obelisk can attract at any time.").setRange(1, 64).sync();
}
