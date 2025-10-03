package crazypants.enderio.base.filter.capability;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class CapabilityFilterHolder {

    @SuppressWarnings({ "null", "rawtypes" })
    @CapabilityInject(FilterHolder.class)
    @Nonnull
    public static Capability<FilterHolder> FILTER_HOLDER_CAPABILITY = null;

    @SubscribeEvent
    public static void create(EnderIOLifecycleEvent.PreInit event) {
        CapabilityManager.INSTANCE.register(FilterHolder.class, new Storage(), new Factory());

        NullHelper.notnullJ(FILTER_HOLDER_CAPABILITY, "Filter Holder Capability is not registered");
    }

    @SuppressWarnings("rawtypes")
    private static class Storage implements Capability.IStorage<FilterHolder> {

        @Override
        @Nullable
        public NBTBase writeNBT(Capability<FilterHolder> capability, FilterHolder instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<FilterHolder> capability, FilterHolder instance, EnumFacing side,
                            NBTBase nbt) {}
    }

    @SuppressWarnings("rawtypes")
    private static class Factory implements Callable<FilterHolder> {

        @Override
        public FilterHolder call() throws Exception {
            return null;
        }
    }
}
