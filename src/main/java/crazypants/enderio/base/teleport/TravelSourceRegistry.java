package crazypants.enderio.base.teleport;

import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.sound.IModSound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class TravelSourceRegistry {

    private static final ResourceLocation NAME = new ResourceLocation(EnderIO.DOMAIN, "travel_source");
    public static ForgeRegistry<ITravelSource> REGISTRY;

    private static final Map<ITravelSource, TravelSource> ENUM_LOOKUP = new IdentityHashMap<>();

    private TravelSourceRegistry() {
        // prevent instantiation
    }

    @SubscribeEvent
    public static void registerRegistry(RegistryEvent.NewRegistry event) {
        REGISTRY = (ForgeRegistry<ITravelSource>) new RegistryBuilder<ITravelSource>()
                .setName(NAME)
                .setType(ITravelSource.class)
                .setMaxID(16)
                .disableSaving()
                .disableOverrides()
                .add(Callbacks.INSTANCE)
                .create();
    }

    @SubscribeEvent
    public static void registerBuiltinSources(RegistryEvent.Register<ITravelSource> event) {
        for (TravelSource value : TravelSource.values()) {
            event.getRegistry().register(value);
        }
    }

    public static TravelSource getEnum(ITravelSource source) {
        return ENUM_LOOKUP.get(source);
    }

    private enum Callbacks implements IForgeRegistry.AddCallback<ITravelSource> {
        INSTANCE;

        @Override
        public void onAdd(IForgeRegistryInternal<ITravelSource> owner, RegistryManager stage, int id, ITravelSource obj, @Nullable ITravelSource oldObj) {
            if (obj instanceof TravelSource travelSource) {
                ENUM_LOOKUP.put(obj, travelSource);
            } else {
                ENUM_LOOKUP.put(obj, EnumHelper.addEnum(TravelSource.class, Objects.requireNonNull(obj.getRegistryName()).toString(),
                        new Class[]{IModSound.class, IntSupplier.class, DoubleSupplier.class},
                        obj.getSound(),
                        (IntSupplier) obj::getMaxDistanceTravelled,
                        (DoubleSupplier) obj::getPowerCostPerBlockTraveledRF));
            }
        }
    }
}
