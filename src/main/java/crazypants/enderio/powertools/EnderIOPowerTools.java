package crazypants.enderio.powertools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.Lang;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.EIOTags;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.conduits.EnderIOConduits;
import crazypants.enderio.powertools.config.Config;
import crazypants.enderio.powertools.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;

@Mod(modid = EnderIOPowerTools.MODID,
     name = EnderIOPowerTools.MOD_NAME,
     version = EnderIOPowerTools.VERSION,
     dependencies = "after:" + EnderIOConduits.MODID)
public class EnderIOPowerTools implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderiopowertools";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Powertools";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    public static final @Nonnull Lang lang = new Lang(DOMAIN);

    @SuppressWarnings("unused")
    private static ConfigHandler configHandler;

    @Override
    @Nullable
    public Configuration getConfiguration() {
        return Config.F.getConfig();
    }

    @Override
    public NNList<RecipeFile> getRecipeFileList() {
        return new NNList<>(new RecipeFile(2, "powertools"));
    }

    @Override
    @Nonnull
    public NNList<String> getExampleFiles() {
        return new NNList<>("powertools_easy_recipes", "powertools_easy_recipes");
    }

    @EventHandler
    public static void preinit(@Nonnull FMLPreInitializationEvent event) {
        configHandler = new ConfigHandlerEIO(event, Config.F);
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        PacketHandler.init(event);
    }

    @EventHandler
    public static void postinit(FMLPostInitializationEvent event) {}
}
