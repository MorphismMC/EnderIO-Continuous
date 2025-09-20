package crazypants.enderio.conduit.gas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.EIOTags;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.conduit.gas.common.conduit.GasConduitObject;
import crazypants.enderio.conduit.gas.common.config.Config;
import crazypants.enderio.conduit.gas.common.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;

@Mod(modid = EnderIOConduitsMekanism.MOD_ID,
     name = EnderIOConduitsMekanism.MOD_NAME,
     version = EnderIOConduitsMekanism.VERSION,
     dependencies = EnderIOConduitsMekanism.DEPENDENCIES,
     acceptedMinecraftVersions = EnderIOConduitsMekanism.MC_VERSION)
@Mod.EventBusSubscriber(modid = EnderIOConduitsMekanism.MOD_ID)
public class EnderIOConduitsMekanism implements IEnderIOAddon {

    public static final String MOD_ID = "gasconduits";
    public static final String MOD_NAME = "GasConduits";
    public static final String VERSION = EIOTags.VERSION;
    public static final String MC_VERSION = "1.12.2,";
    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    public static Logger logger;

    private static ConfigHandler configHandler;

    public EnderIOConduitsMekanism() {
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
        return isLoaded() ? Config.F.getConfig() : null;
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
