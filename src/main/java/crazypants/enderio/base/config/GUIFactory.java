package crazypants.enderio.base.config;

import static crazypants.enderio.base.lang.Lang.CONFIG_TITLE;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraftforge.common.config.Configuration;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.registry.Registry;
import info.loenwind.autoconfig.gui.ConfigFactory;

public class GUIFactory extends ConfigFactory {

    @Override
    protected @Nonnull String getModID() {
        return EnderIO.MODID;
    }

    @Override
    protected @Nonnull String getTitle() {
        return CONFIG_TITLE.get();
    }

    @Override
    protected @Nonnull String getTitle2() {
        return "";
    }

    @Override
    protected @Nonnull Map<String, Configuration> getConfigurations() {
        return Registry.getConfigurations();
    }
}
