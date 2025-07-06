package gg.galaxygaming.gasconduits;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Logger;

import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import gg.galaxygaming.gasconduits.common.conduit.GasConduitObject;
import gg.galaxygaming.gasconduits.common.config.Config;
import gg.galaxygaming.gasconduits.common.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;

@Mod(modid = GasConduitsConstants.MOD_ID,
     name = GasConduitsConstants.MOD_NAME,
     version = GasConduitsConstants.VERSION,
     dependencies = GasConduits.DEPENDENCIES,
     acceptedMinecraftVersions = GasConduitsConstants.MC_VERSION)
@Mod.EventBusSubscriber(modid = GasConduitsConstants.MOD_ID)
public class GasConduits implements IEnderIOAddon {

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    public static Logger logger;

    private static ConfigHandler configHandler;

    public GasConduits() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (isLoaded()) {
            logger = event.getModLog();
            configHandler = new ConfigHandlerEIO(event, Config.F);
            Log.warn("Mekanism Gas conduits loaded. Let your networks connect!");
        } else {
            Log.warn("Mekanism Gas conduits NOT loaded. Mekanism is not installed");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PacketHandler.init(event);
    }

    @SubscribeEvent
    public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
        if (isLoaded()) {
            GasConduitObject.registerBlocksEarly(event);
        }
    }

    @Override
    @Nullable
    public Configuration getConfiguration() {
        return Config.F.getConfig();
    }

    @Override
    public NNList<RecipeFile> getRecipeFileList() {
        if (isLoaded()) {
            return new NNList<>(new RecipeFile(2, "conduits-gas"));
        }
        return NNList.emptyList();
    }

    public static boolean isLoaded() {
        return Loader.isModLoaded("mekanism");
    }
}
