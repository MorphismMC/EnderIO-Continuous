package com.morphismmc.eioadditions.integrations.crafttweaker.recipe;

import net.minecraft.item.crafting.Ingredient;

public class VatRecipeInput extends RecipeInput {

    protected final int slot;
    protected final float multiple;

    public VatRecipeInput(Ingredient ing, int slot, float multiple) {
        super(ing);
        this.slot = slot;
        this.multiple = multiple;
    }

    @Override
    public int getSlotNumber() {
        return slot;
    }

    @Override
    public float getMulitplier() {
        return multiple;
    }

}
