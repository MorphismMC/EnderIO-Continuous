package crazypants.enderio.base.sound;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import org.jetbrains.annotations.NotNull;

public interface ModSound {

    /**
     * Checks if the sound is valid and can be played.
     * 
     * @return true if the sound can be played
     */
    boolean isValid();

    /**
     * 
     * @return the soundEvent to play. Will throw an exception if isValid() is false.
     */
    @NotNull
    SoundEvent getSoundEvent();

    /**
     * 
     * @return the SoundCategory to use for playing the sound. Will throw an exception if isValid() is false.
     */
    @NotNull
    SoundCategory getSoundCategory();

}
