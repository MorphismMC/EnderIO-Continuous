package crazypants.enderio.api.teleport;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.sound.IModSound;
import crazypants.enderio.base.sound.SoundRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public enum TravelSource implements ITravelSource {

    BLOCK(SoundRegistry.TRAVEL_SOURCE_BLOCK) {

        @Override
        public int getMaxDistanceTravelled() {
            return TeleportConfig.rangeBlocks.get();
        }
    },
    STAFF(SoundRegistry.TRAVEL_SOURCE_ITEM) {

        @Override
        public int getMaxDistanceTravelled() {
            return TeleportConfig.rangeItem2Block.get();
        }

        @Override
        public float getPowerCostPerBlockTraveledRF() {
            return TeleportConfig.costItem2Block.get();
        }
    },
    STAFF_BLINK(SoundRegistry.TRAVEL_SOURCE_ITEM) {

        @Override
        public int getMaxDistanceTravelled() {
            return TeleportConfig.rangeItem2Blink.get();
        }

        @Override
        public float getPowerCostPerBlockTraveledRF() {
            return TeleportConfig.costItem2Blink.get();
        }

        @Override
        public boolean getConserveMomentum() {
            return true;
        }
    },
    TELEPAD(SoundRegistry.TELEPAD);


    public static int getMaxDistanceSq() {
        int result = 0;
        for (TravelSource source : values()) {
            if (source.getMaxDistanceTravelled() > result) {
                result = source.getMaxDistanceTravelled();
            }
        }
        return result * result;
    }

    private final ResourceLocation registryName;
    public final IModSound sound;

    // region GTLite Patch
    @Deprecated
    private final IntSupplier maxDistance;
    @Deprecated
    private final DoubleSupplier powerCost;

    @Deprecated
    TravelSource(IModSound sound, IntSupplier maxDistance, DoubleSupplier powerCost) {
        this.registryName = new ResourceLocation(EnderIO.DOMAIN, name().toLowerCase());
        this.sound = sound;
        this.maxDistance = maxDistance;
        this.powerCost = powerCost;
    }
    // endregion

    TravelSource(IModSound sound) {
        this(sound, () -> 0, () -> 0);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public IModSound getSound() {
        return sound;
    }

    @Override
    public int getMaxDistanceTravelled() {
        return maxDistance.getAsInt();
    }

    @Override
    public float getPowerCostPerBlockTraveledRF() {
        return (float) powerCost.getAsDouble();
    }
}
