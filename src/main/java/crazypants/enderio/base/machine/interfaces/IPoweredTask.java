package crazypants.enderio.base.machine.interfaces;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.RecipeBonusType;

public interface IPoweredTask {

    void update(float availableEnergy);

    boolean isComplete();

    float getProgress();

    @Nonnull
    ResultStack[] getCompletedResult();

    float getRequiredEnergy();

    @Nonnull
    RecipeBonusType getBonusType();

    void writeToNBT(@Nonnull NBTTagCompound nbtRoot);

    @Nullable
    IMachineRecipe getRecipe();

    public abstract @Nonnull NNList<MachineRecipeInput> getInputs();
}
