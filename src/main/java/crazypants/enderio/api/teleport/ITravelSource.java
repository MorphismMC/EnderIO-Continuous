package crazypants.enderio.api.teleport;

import crazypants.enderio.base.sound.ModSound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

/**
 * GTLite Patch
 *
 * @author GateGuardian
 * @date : 2025/7/2
 */
public interface ITravelSource extends IForgeRegistryEntry<ITravelSource> {

    ModSound getSound();

    default boolean getConserveMomentum() {
        return false;
    }

    int getMaxDistanceTravelled();

    default int getMaxDistanceTravelledSq() {
        return getMaxDistanceTravelled() * getMaxDistanceTravelled();
    }

    default float getPowerCostPerBlockTraveledRF() {
        return 0f;
    }

    /*  -------------------------------------------------- Registry Entry -------------------------------------------------- */

    @Override
    @NotNull ResourceLocation getRegistryName();

    @Override
    default ITravelSource setRegistryName(ResourceLocation name) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Class<ITravelSource> getRegistryType() {
        return ITravelSource.class;
    }
}
