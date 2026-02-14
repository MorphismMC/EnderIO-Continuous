package crazypants.enderio.base.integration.crafttweaker;

import com.enderio.core.common.util.NNList;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crazypants.enderio.base.integration.crafttweaker.recipe.RecipeInput;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.RecipeOutput;

public class RecipeUtils {
    public static IRecipeInput[] toEIOInputs(IIngredient[] inputs) {
        IRecipeInput[] ret = new IRecipeInput[inputs.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = toInput(inputs[i]);
        }
        return ret;
    }

    public static NNList<IRecipeInput> toEIOInputsNN(IIngredient[] inputs) {
        NNList<IRecipeInput> ret = new NNList<>();
        for (IIngredient input : inputs) {
            ret.add(toInput(input));
        }
        return ret;
    }

    public static RecipeOutput[] toEIOOutputs(IItemStack[] inputs, float[] chances, float[] xp) {
        RecipeOutput[] ret = new RecipeOutput[inputs.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new RecipeOutput(CraftTweakerMC.getItemStack(inputs[i]), chances[i], xp[i]);
        }
        return ret;
    }

    public static RecipeOutput[] toEIOOutputs(WeightedItemStack[] inputs, float[] xp) {
        RecipeOutput[] ret = new RecipeOutput[inputs.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new RecipeOutput(CraftTweakerMC.getItemStack(inputs[i].getStack()), inputs[i].getChance(), xp[i]);
        }
        return ret;
    }

    public static RecipeInput toInput(IIngredient ingredient) {
        return new RecipeInput(CraftTweakerMC.getIngredient(ingredient));
    }

    public static String getDisplayString(IIngredient... ingredients) {
        StringBuilder sb = new StringBuilder("[");
        for (IIngredient i : ingredients)
            sb.append(i == null ? "null" : i.toCommandString() + ",");
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.append("]").toString();
    }

    public static String getDisplayString(WeightedItemStack... weightedItemStacks) {
        StringBuilder sb = new StringBuilder("[");
        for (WeightedItemStack i : weightedItemStacks)
            sb.append(i == null ? "null" : i.getStack().toCommandString() + " % " + i.getPercent() + ",");
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.append("]").toString();
    }
}
