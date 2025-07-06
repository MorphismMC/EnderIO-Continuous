package crazypants.enderio.base.recipe;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.NNList;

public interface IManyToOneRecipe extends IRecipe {

    boolean isValidRecipeComponents(@Nonnull NNList<ItemStack> input);

    @Nonnull
    ItemStack getOutput();

    boolean isDedupeInput();
}
