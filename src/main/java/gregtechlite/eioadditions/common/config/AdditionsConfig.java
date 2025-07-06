package gregtechlite.eioadditions.common.config;

import crazypants.enderio.base.config.factory.ValueFactoryEIO;
import gregtechlite.eioadditions.AdditionsConstants;

public final class AdditionsConfig {

    public static final ValueFactoryEIO F = new ValueFactoryEIO(AdditionsConstants.MOD_ID);

    static {
        // force sub-configs to be classloaded with the main config
        AdditionsTeleportConfig.F.getClass();
    }
}
