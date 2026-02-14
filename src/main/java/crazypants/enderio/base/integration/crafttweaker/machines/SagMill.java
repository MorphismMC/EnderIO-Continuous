package crazypants.enderio.base.integration.crafttweaker.machines;

import java.util.Arrays;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.WeightedItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crazypants.enderio.base.integration.crafttweaker.CTIntegration;
import crazypants.enderio.base.integration.crafttweaker.RecipeUtils;
import crazypants.enderio.base.integration.crafttweaker.recipe.RecipeInput;
import crazypants.enderio.base.integration.crafttweaker.recipe.SagRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.enderio.SagMill")
@ZenRegister
public class SagMill {

    @ZenMethod
    public static void addRecipe(IItemStack[] output, float[] chances, IIngredient input, @Optional String bonusType, @Optional int energyCost, @Optional float[] xp) {
        if (xp == null) {
            xp = new float[output.length];
            Arrays.fill(xp, 0);
        }
        final float[] xpa = xp;
        if (hasErrors(output, chances, input, xpa, bonusType)) return;
        CTIntegration.ADDITIONS.add(() -> {
            SagRecipe recipe = new SagRecipe(new RecipeInput(CraftTweakerMC.getIngredient(input)), energyCost <= 0 ? 5000 : energyCost, RecipeBonusType.fromString(bonusType), RecipeLevel.IGNORE, RecipeUtils.toEIOOutputs(output, chances, xpa));
            SagMillRecipeManager.getInstance().addRecipe(recipe);
        });
    }

    @ZenMethod
    public static void addRecipe(WeightedItemStack[] output, IIngredient input, @Optional String bonusType, @Optional int energyCost, @Optional float[] xp) {
        if (xp == null) {
            xp = new float[output.length];
            Arrays.fill(xp, 0);
        }
        final float[] xpa = xp;
        if (hasErrors(output, input, xpa, bonusType)) return;
        CTIntegration.ADDITIONS.add(() -> {
            SagRecipe recipe = new SagRecipe(new RecipeInput(CraftTweakerMC.getIngredient(input)), energyCost <= 0 ? 5000 : energyCost, RecipeBonusType.fromString(bonusType), RecipeLevel.IGNORE, RecipeUtils.toEIOOutputs(output, xpa));
            SagMillRecipeManager.getInstance().addRecipe(recipe);
        });
    }

    @ZenMethod
    public static void removeRecipe(IItemStack input) {
        if (input == null) {
            CraftTweakerAPI.logError("Cannot remove recipe for null from sag mill.");
            return;
        }
        CTIntegration.REMOVALS.add(() -> {
            ItemStack stack = CraftTweakerMC.getItemStack(input);
            IRecipe rec = SagMillRecipeManager.getInstance().getRecipeForInput(RecipeLevel.IGNORE, stack);
            if (rec != null) {
                SagMillRecipeManager.getInstance().getRecipes().remove(rec);
            } else CraftTweakerAPI.logError("No Sag Mill recipe found for " + stack.getDisplayName());
        });
    }

    private static boolean hasErrors(IItemStack[] output, float[] chances, IIngredient input, float[] xp, String type) {
        if (output == null || output.length == 0) {
            CraftTweakerAPI.logError("Invalid output (empty or null) in Sag Mill recipe: " + Arrays.toString(output));
            return true;
        }
        if (output.length != chances.length) {
            CraftTweakerAPI.logError("Invalid output chances (chances do not match outputs) in Sag Mill recipe: " + RecipeUtils.getDisplayString(output) + " | " + chances);
            return true;
        }
        return checkError(output.length, input, xp, type, RecipeUtils.getDisplayString(output));
    }

    private static boolean hasErrors(WeightedItemStack[] output, IIngredient input, float[] xp, String type) {
        if (output == null || output.length == 0) {
            CraftTweakerAPI.logError("Invalid output (empty or null) in Sag Mill recipe: " + Arrays.toString(output));
            return true;
        }
        return checkError(output.length, input, xp, type, RecipeUtils.getDisplayString(output));
    }

    private static boolean checkError(int outputLength, IIngredient input, float[] xp, String type, String displayString) {
        if (outputLength > 4) {
            CraftTweakerAPI.logError("Invalid output (more than four entries) in Sag Mill recipe: " + displayString);
            return true;
        }
        if (outputLength != xp.length) {
            CraftTweakerAPI.logError("Invalid output xp (xp does not match outputs) in Sag Mill recipe: " + displayString + " | " + xp);
            return true;
        }
        if (input == null) {
            CraftTweakerAPI.logError("Invalid null Sag Mill input.");
            return true;
        }
        return false;
    }

}
