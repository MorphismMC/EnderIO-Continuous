package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class CombustionGenConfig {

    public static final IValueFactory F = Config.F.section("generator.combustion");

    public static final IValue<Integer> combGenTankSize = F
            .make("combGenTankSize", 5000,
                    "How large should the fuel and coolant tanks of the combustion generator be?")
            .setRange(500, 50000).sync();
}
