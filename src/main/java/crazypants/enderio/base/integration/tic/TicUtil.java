package crazypants.enderio.base.integration.tic;

import javax.annotation.Nonnull;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.farming.FarmersRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class TicUtil {

    private TicUtil() {}

    @SubscribeEvent
    public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
        FarmersRegistry.registerLogs("blockSlimeCongealed"); // oreDict
    }
}
