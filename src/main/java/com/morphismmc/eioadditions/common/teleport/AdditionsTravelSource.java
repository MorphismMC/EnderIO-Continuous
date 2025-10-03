package com.morphismmc.eioadditions.common.teleport;

import crazypants.enderio.api.teleport.ITravelSource;
import crazypants.enderio.base.sound.ModSound;
import crazypants.enderio.base.sound.SoundRegistry;
import com.morphismmc.eioadditions.AdditionsConstants;
import com.morphismmc.eioadditions.common.config.AdditionsTeleportConfig;
import net.minecraft.util.ResourceLocation;

public enum AdditionsTravelSource implements ITravelSource {

    TELEPORT_STAFF(SoundRegistry.TRAVEL_SOURCE_ITEM) {

        @Override
        public int getMaxDistanceTravelled() {
            return AdditionsTeleportConfig.rangeTeleportStaff2Block.get();
        }
    },
    TELEPORT_STAFF_BLINK(SoundRegistry.TRAVEL_SOURCE_ITEM) {

        @Override
        public int getMaxDistanceTravelled() {
            return AdditionsTeleportConfig.rangeTeleportStaff2Blink.get();
        }
    };

    private final ResourceLocation registryName;
    public final ModSound sound;

    AdditionsTravelSource(ModSound sound) {
        this.registryName = new ResourceLocation(AdditionsConstants.MOD_ID, name().toLowerCase());
        this.sound = sound;
    }

    @Override
    public ModSound getSound() {
        return sound;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }
}
