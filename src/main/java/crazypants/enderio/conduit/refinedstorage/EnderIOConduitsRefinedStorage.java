package crazypants.enderio.conduit.refinedstorage;

import static crazypants.enderio.conduit.refinedstorage.EnderIOConduitsRefinedStorage.MODID;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.EIOTags;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import crazypants.enderio.conduit.refinedstorage.init.ConduitRefinedStorageObject;

@Mod(modid = MODID,
     name = EnderIOConduitsRefinedStorage.MOD_NAME,
     version = EnderIOConduitsRefinedStorage.VERSION,
     dependencies = EnderIOConduitsRefinedStorage.DEPENDENCIES)
@EventBusSubscriber(modid = MODID)
public class EnderIOConduitsRefinedStorage implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderioconduitsrefinedstorage";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Conduits Refined Storage";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    public EnderIOConduitsRefinedStorage() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @EventHandler
    public static void init(@Nonnull FMLPreInitializationEvent event) {
        if (isLoaded()) {
            Log.warn("Refined Storage conduits loaded. Let your networks connect!");
        } else {
            Log.warn("Refined Storage conduits NOT loaded. Refined Storage is not installed");
        }
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        if (isLoaded()) {}
    }

    @EventHandler
    public static void init(FMLPostInitializationEvent event) {
        if (isLoaded()) {}
    }

    @SubscribeEvent
    public static void registerBlocksEarly(@Nonnull RegisterModObject event) {
        if (isLoaded()) {
            ConduitRefinedStorageObject.registerBlocksEarly(event);
        }
    }

    @Override
    public NNList<RecipeFile> getRecipeFileList() {
        if (isLoaded()) {
            return new NNList<>(new RecipeFile(2, "conduits-refined-storage"));
        }
        return NNList.emptyList();
    }

    public static boolean isLoaded() {
        return Loader.isModLoaded("refinedstorage");
    }
}
