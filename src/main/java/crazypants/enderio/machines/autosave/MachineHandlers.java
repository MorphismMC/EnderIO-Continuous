package crazypants.enderio.machines.autosave;

import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.transceiver.HandleChannelList;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class MachineHandlers extends BaseHandlers {

    @SubscribeEvent
    public static void register(EnderIOLifecycleEvent.PreInit event) {
        REGISTRY.register(new HandleChannelList());
    }
}
