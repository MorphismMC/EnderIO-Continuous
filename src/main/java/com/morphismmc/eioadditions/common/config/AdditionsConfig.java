package com.morphismmc.eioadditions.common.config;

import com.morphismmc.eioadditions.AdditionsConstants;
import crazypants.enderio.base.config.factory.ValueFactoryEIO;

public final class AdditionsConfig {

    public static final ValueFactoryEIO F = new ValueFactoryEIO(AdditionsConstants.MOD_ID);

    static {
        // force sub-configs to be classloaded with the main config
        AdditionsTeleportConfig.F.getClass();
    }
}
