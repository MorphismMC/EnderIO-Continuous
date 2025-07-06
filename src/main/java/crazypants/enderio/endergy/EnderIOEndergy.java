package crazypants.enderio.endergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.EIOTags;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.endergy.config.Config;
import crazypants.enderio.endergy.init.CommonProxy;
import info.loenwind.autoconfig.ConfigHandler;

@Mod(modid = EnderIOEndergy.MODID,
     name = EnderIOEndergy.MOD_NAME,
     version = EnderIOEndergy.VERSION,
     dependencies = EnderIOEndergy.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOEndergy implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderioendergy";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Endergy";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    @SidedProxy(clientSide = "crazypants.enderio.endergy.init.ClientProxy",
                serverSide = "crazypants.enderio.endergy.init.CommonProxy")
    public static CommonProxy proxy;
    @SuppressWarnings("unused")
    private static ConfigHandler configHandler;

    public EnderIOEndergy() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @EventHandler
    public static void init(@Nonnull FMLPreInitializationEvent event) {
        configHandler = new ConfigHandlerEIO(event, Config.F);
        proxy.preInit();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {}

    @EventHandler
    public static void init(FMLPostInitializationEvent event) {}

    @Override
    @Nullable
    public Configuration getConfiguration() {
        return Config.F.getConfig();
    }

    @Override
    @Nonnull
    public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
        return new NNList<>(Triple.of(2, null, "endergy"), Triple.of(2, null, "endergy_balls"),
                Triple.of(2, null, "hiding_endergy"));
    }
}
