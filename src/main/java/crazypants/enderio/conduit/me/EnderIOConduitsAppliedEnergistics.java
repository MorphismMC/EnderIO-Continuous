package crazypants.enderio.conduit.me;

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
import crazypants.enderio.conduit.me.init.ConduitAppliedEnergisticsObject;

@Mod(modid = EnderIOConduitsAppliedEnergistics.MODID,
     name = EnderIOConduitsAppliedEnergistics.MOD_NAME,
     version = EnderIOConduitsAppliedEnergistics.VERSION,
     dependencies = EnderIOConduitsAppliedEnergistics.DEPENDENCIES)
@EventBusSubscriber
public class EnderIOConduitsAppliedEnergistics implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderioconduitsappliedenergistics";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Conduits Applied Energistics";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    public EnderIOConduitsAppliedEnergistics() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @EventHandler
    public static void init(@Nonnull FMLPreInitializationEvent event) {
        if (MEUtil.isMEEnabled()) {
            Log.warn("Applied Energistics conduits loaded. Let your networks connect!");
        } else {
            Log.warn("Applied Energistics conduits NOT loaded. Applied Energistics is not installed");
        }
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        if (MEUtil.isMEEnabled()) {}
    }

    @EventHandler
    public static void init(FMLPostInitializationEvent event) {
        if (MEUtil.isMEEnabled()) {}
    }

    @SubscribeEvent
    public static void registerConduits(@Nonnull RegisterModObject event) {
        if (MEUtil.isMEEnabled()) {
            ConduitAppliedEnergisticsObject.registerBlocksEarly(event);
        }
    }

    @Override
    @Nonnull
    public NNList<Triple<Integer, RecipeFactory, String>> getRecipeFiles() {
        if (MEUtil.isMEEnabled()) {
            if (MeUtil2.isFluixEnabled()) {
                return new NNList<>(Triple.of(2, null, "conduits-applied-energistics"));
            } else {
                Log.error("[" + MOD_NAME +
                        "]: AE2 Fluix and Pure Fluix or Quartz Fibres are disabled. There will be no way to craft ME conduits unless YOU add a custom recipe.");
            }
        }
        return NNList.emptyList();
    }
}
