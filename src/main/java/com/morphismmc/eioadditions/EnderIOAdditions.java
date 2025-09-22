package com.morphismmc.eioadditions;

import com.enderio.core.common.util.NNList;
import com.morphismmc.eioadditions.common.AdditionsObject;
import com.morphismmc.eioadditions.common.config.AdditionsConfig;
import com.morphismmc.eioadditions.common.teleport.AdditionsTravelSource;
import crazypants.enderio.api.addon.IEnderIOAddon;
import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.ConfigHandlerEIO;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.init.RegisterModObject;
import info.loenwind.autoconfig.ConfigHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AdditionsConstants.MOD_ID,
        name = AdditionsConstants.MOD_NAME,
        version = AdditionsConstants.MOD_VERSION,
        dependencies = EnderIOAdditions.DEPENDENCIES,
        acceptedMinecraftVersions = AdditionsConstants.MC_VERSION)
@Mod.EventBusSubscriber(modid = AdditionsConstants.MOD_ID)
public class EnderIOAdditions implements IEnderIOAddon {

    private static final String DEFAULT_DEPENDENCIES = "after:" + crazypants.enderio.base.EnderIO.MODID;
    public static final String DEPENDENCIES = DEFAULT_DEPENDENCIES;

    private static ConfigHandler configHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configHandler = new ConfigHandlerEIO(event, AdditionsConfig.F);
    }

    @SubscribeEvent
    public static void registerTravelSource(RegistryEvent.Register<ITravelSource> event) {
        for (AdditionsTravelSource value : AdditionsTravelSource.values()) {
            event.getRegistry().register(value);
        }
    }

    @SubscribeEvent
    public static void registerModObjects(RegisterModObject event) {
        event.register(AdditionsObject.class);
    }

    @Override
    public NNList<RecipeFile> getRecipeFileList() {
        return new NNList<>(new RecipeFile(
                2,
                new RecipeFactory(
                        EnderIO.getConfigHandler().getConfigDirectory(),
                        AdditionsConstants.MOD_ID),
                "addition_items"));
    }
}
