package com.morphismmc.eioadditions.integrations.crafttweaker.machines;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;
import com.morphismmc.eioadditions.integrations.crafttweaker.CTIntegration;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import net.minecraft.enchantment.Enchantment;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Map;

@ZenClass("mods.enderio.Enchanter")
@ZenRegister
public class Enchanter {

    @ZenMethod
    public static void addRecipe(IEnchantmentDefinition output, IIngredient input, int amountPerLevel, double costMultiplier) {
        if (output == null) {
            CraftTweakerAPI.logError("Cannot add recipe for null to enchanter.");
            return;
        }
        if (input == null) {
            CraftTweakerAPI.logError("Cannot add recipe for " + output.getTranslatedName(1) + " with null input to enchanter.");
            return;
        }
        CTIntegration.ADDITIONS.add(() -> {
            Things thing = new Things();
            thing.add(new NNList<>(CraftTweakerMC.getIngredient(input).getMatchingStacks()));
            Enchantment enchantment = (Enchantment) output.getInternal();
            if (!thing.isEmpty() && enchantment != null) {
                EnchanterRecipe recipe = new EnchanterRecipe(RecipeLevel.IGNORE, thing, amountPerLevel, enchantment, costMultiplier);
                MachineRecipeRegistry.instance.registerRecipe(recipe);
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(IEnchantmentDefinition output) {
        if (output == null) {
            CraftTweakerAPI.logError("Cannot remove recipe for null from enchanter.");
            return;
        }
        CTIntegration.REMOVALS.add(() -> {
            Enchantment enchantment = (Enchantment) output.getInternal();
            String id = null;
            IMachineRecipe recipe = null;
            for (Map.Entry<String, ? extends IMachineRecipe> ent :
                    MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).entrySet()) {
                if (((EnchanterRecipe) ent.getValue()).getEnchantment() == enchantment) {
                    id = ent.getKey();
                    recipe = ent.getValue();
                    break;
                }
            }
            if (id != null) {
                MachineRecipeRegistry.instance.removeRecipe(recipe);
            } else CraftTweakerAPI.logError("No Enchanter recipe found for " + output.getName());
        });
    }

}
