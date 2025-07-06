package crazypants.enderio.machines;

import com.enderio.core.common.Lang;
import com.enderio.core.common.mixin.SimpleMixinLoader;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.api.EIOTags;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.machines.config.Config;
import crazypants.enderio.machines.machine.transceiver.TransceiverRegistry;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.autoconfig.ConfigHandler;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = EnderIOMachines.MODID,
     name = EnderIOMachines.MOD_NAME,
     version = EnderIOMachines.VERSION,
     dependencies = EnderIOMachines.DEPENDENCIES)
public class EnderIOMachines implements IEnderIOAddon {

    public static final @Nonnull String MODID = "enderiomachines";
    public static final @Nonnull String DOMAIN = "enderio";
    public static final @Nonnull String MOD_NAME = "Ender IO Machines";
    public static final @Nonnull String VERSION = EIOTags.VERSION;

    private static final @Nonnull String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final @Nonnull String DEPENDENCIES = DEFAULT_DEPENDENCIES;
    @SuppressWarnings("unused")
    private static ConfigHandler configHandler;

    public EnderIOMachines() {
        SimpleMixinLoader.loadMixinSources(this);
    }

    @EventHandler
    public static void init(@Nonnull FMLPreInitializationEvent event) {
        configHandler = new ConfigHandlerEIO(event, Config.F);
    }

    @EventHandler
    public static void init(FMLInitializationEvent event) {
        PacketHandler.init(event);
    }

    public static final @Nonnull Lang lang = new Lang(DOMAIN);

    @Override
    @Nullable
    public Configuration getConfiguration() {
        return Config.F.getConfig();
    }

    @EventHandler
    public void serverStopped(@Nonnull FMLServerStoppedEvent event) {
        TransceiverRegistry.INSTANCE.reset();
    }

    @EventHandler
    public static void onServerStart(FMLServerAboutToStartEvent event) {
        TransceiverRegistry.INSTANCE.reset();
    }

    @Override
    public NNList<RecipeFile> getRecipeFileList() {
        return new NNList<>(
                new RecipeFile(2, "machines"),
                new RecipeFile(2, "sagmill"),
                new RecipeFile(3, "sagmill_modded"),
                new RecipeFile(3, "sagmill_ores"),
                new RecipeFile(3, "sagmill_metals"), 
                new RecipeFile(3, "sagmill_vanilla"),
                new RecipeFile(3, "sagmill_vanilla2modded"),
                new RecipeFile(3, "sagmill_silentgems"),
                new RecipeFile(3, "vat"),
                new RecipeFile(3, "enchanter"),
                new RecipeFile(3, "spawner"),
                new RecipeFile(9, "capacitor_machines"),
                new RecipeFile(3, "integration_railcraft_recipes"),
                new RecipeFile(3, "soulbinder"),
                new RecipeFile(3, "tank"),
                new RecipeFile(3, "hiding_machines"),
                new RecipeFile(3, "darksteel_upgrades_machines"),
                new RecipeFile(3, "alloying"),
                new RecipeFile(3, "alloying_modded"));
    }

    @Override
    @Nonnull
    public NNList<String> getExampleFiles() {
        return new NNList<>("machines_easy_recipes", "machines_easy_recipes", "infinity", "cheap_machines",
                "cheaty_spawner", "sagmill_dupe_recipe_patches");
    }
}
