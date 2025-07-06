package crazypants.enderio.base.integration.draconic;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.base.farming.FarmersRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DraconicUtil {

    @SubscribeEvent
    public static void registerHoes(@Nonnull EnderIOLifecycleEvent.Init.Pre event) {
        FarmersRegistry.registerHoes("draconicevolution", "draconic_hoe");
    }
}
