package com.morphismmc.enderio.api.register.entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemEntry<T extends Item> extends RegistryEntry<Item, T> {

    public ItemEntry(final ResourceLocation name, final IForgeRegistry<Item> registry) {
        super(name, registry);
    }

    public ItemStack getStack() {
        final T item = get();
        return item != null ? new ItemStack(item) : ItemStack.EMPTY;
    }

    public ItemStack getStack(final int count) {
        final T item = get();
        return item != null ? new ItemStack(item, count) : ItemStack.EMPTY;
    }

    public ItemStack getStack(final int count, final int meta) {
        final T item = get();
        return item != null ? new ItemStack(item, count, meta) : ItemStack.EMPTY;
    }

    public boolean equalsTo(final ItemStack stack) { // TODO: Change to morphism lib ItemUtil call.
        final T item = get();
        return item != null && stack.getItem() == item;
    }
}
