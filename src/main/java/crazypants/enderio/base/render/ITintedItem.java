package crazypants.enderio.base.render;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import crazypants.enderio.base.render.registry.PaintTintHandler;

/**
 * Un-sided alternative to {@link net.minecraft.client.renderer.color.IItemColor} to be used together with the
 * {@link PaintTintHandler}
 *
 */
public interface ITintedItem {

    int getItemTint(@Nonnull ItemStack stack, int tintIndex);
}
