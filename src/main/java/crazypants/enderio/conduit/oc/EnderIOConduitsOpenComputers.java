package crazypants.enderio.conduit.oc;

import javax.annotation.Nonnull;

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
import crazypants.enderio.conduit.oc.init.ConduitOpenComputersObject;
import crazypants.enderio.conduit.oc.network.PacketHandler;
import crazypants.enderio.conduits.conduit.TileConduitBundle;
import li.cil.oc.api.network.Environment;

@Mod(modid = EnderIOConduitsOpenComputers.MODID,
     name = EnderIOConduitsOpenComputers.MOD_NAME,
     version = EnderIOConduitsOpenComputers.VERSION,
     dependencies = EnderIOConduitsOpenComputers.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOConduitsOpenComputers implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderioconduitsopencomputers";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Conduits OpenComputers";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    public EnderIOConduitsOpenComputers() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @EventHandler
    public static void init(@Nonnull FMLPreInitializationEvent event) {
        if (OCUtil.isOCEnabled()) {
            Log.warn("OpenComputers conduits loaded. Let your networks connect!");
        } else {
            Log.warn("OpenComputers conduits NOT loaded. OpenComputers is not installed");
        }
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        if (OCUtil.isOCEnabled()) {
            // Sanity checking
            System.out.println("Mixin successful? " + Environment.class.isAssignableFrom(TileConduitBundle.class));
            PacketHandler.init(event);
        }
    }

    @EventHandler
    public static void init(FMLPostInitializationEvent event) {
        if (OCUtil.isOCEnabled()) {}
    }

    @SubscribeEvent
    public static void registerConduits(@Nonnull RegisterModObject event) {
        if (OCUtil.isOCEnabled()) {
            ConduitOpenComputersObject.registerBlocksEarly(event);
        }
    }

    @Override
    @Nonnull
    public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
        if (OCUtil.isOCEnabled()) {
            return new NNList<>(Triple.of(2, null, "conduits-opencomputers"));
        }
        return NNList.emptyList();
    }
}
