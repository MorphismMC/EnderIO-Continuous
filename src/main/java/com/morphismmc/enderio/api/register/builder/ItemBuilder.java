package com.morphismmc.enderio.api.register.builder;

import com.morphismmc.enderio.api.register.ModRegistration;
import com.morphismmc.enderio.api.register.entry.ItemEntry;
import com.morphismmc.enderio.api.register.entry.RegistryEntry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ItemBuilder<T extends Item, P> extends DefaultRegistryEntryBuilder<Item, T, P, ItemBuilder<T, P>> {

    private @Nullable CreativeTabs creativeTab;

    public ItemBuilder(final ModRegistration owner,
                       final String name,
                       final IForgeRegistry<Item> registry,
                       final Supplier<? extends T> factory,
                       @Nullable final P parent) {
        super(owner, name, registry, factory, parent);
    }

    public ItemBuilder<T, P> tab(final CreativeTabs tab) {
        this.creativeTab = tab;
        return this;
    }

    @Override
    protected ItemEntry<T> createEntryWrapper(final RegistryEntry<Item, T> entry) {
        return new ItemEntry<>(entry.name(), entry.registry());
    }

    @Override
    protected T createEntry() {
        final T entry = factory.get();
        entry.setRegistryName(new ResourceLocation(owner.modId(), name));
        entry.setTranslationKey(owner.modId() + "." + name);
        if (creativeTab != null) {
            entry.setCreativeTab(creativeTab);
        }
        return entry;
    }

    @Override
    public ItemEntry<T> register() {
        final ItemEntry<T> entry = (ItemEntry<T>) super.register();
        if (parent instanceof BlockBuilder<?, ?> parentBlock) {
            parentBlock.setItemEntry(entry);
        }
        return entry;
    }

    public @Nullable CreativeTabs creativeTab() {
        return creativeTab;
    }
}
