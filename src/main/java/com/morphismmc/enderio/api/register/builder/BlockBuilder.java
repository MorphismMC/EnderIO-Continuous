package com.morphismmc.enderio.api.register.builder;

import com.morphismmc.enderio.api.register.ModRegistration;
import com.morphismmc.enderio.api.register.entry.TileEntityEntry;
import com.morphismmc.enderio.api.register.entry.BlockEntry;
import com.morphismmc.enderio.api.register.entry.ItemEntry;
import com.morphismmc.enderio.api.register.entry.RegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockBuilder<T extends Block, P> extends DefaultRegistryEntryBuilder<Block, T, P, BlockBuilder<T, P>> {

    private @Nullable ItemEntry<? extends Item> itemEntry;
    private @Nullable TileEntityEntry<? extends TileEntity> tileEntityEntry;

    public BlockBuilder(final ModRegistration owner,
                        final String name,
                        final IForgeRegistry<Block> registry,
                        final Supplier<? extends T> factory,
                        @Nullable final P parent) {
        super(owner, name, registry, factory, parent);
    }

    @SuppressWarnings("unchecked")
    public <I extends Item> ItemBuilder<I, BlockBuilder<T, P>> item(final Function<? super T, I> itemFactory) {
        return new ItemBuilder<>(owner, name, ForgeRegistries.ITEMS, () -> {
            final T block = (T) ForgeRegistries.BLOCKS.getValue(new ResourceLocation(owner.modId(), name));
            return itemFactory.apply(block);
            }, this);
    }

    public ItemBuilder<ItemBlock, BlockBuilder<T, P>> simpleItem() {
        return item(ItemBlock::new);
    }

    public @Nullable ItemEntry<? extends Item> getItemEntry() {
        return itemEntry;
    }

    public void setItemEntry(final ItemEntry<? extends Item> itemEntry) {
        this.itemEntry = itemEntry;
    }

    public <E extends TileEntity> TileEntityBuilder<E, BlockBuilder<T, P>> tileEntity(final Class<E> teClass) {
        return new TileEntityBuilder<>(owner, name, teClass, this);
    }

    public @Nullable TileEntityEntry<? extends TileEntity> getTileEntityEntry() {
        return tileEntityEntry;
    }

    public void setTileEntityEntry(final TileEntityEntry<? extends TileEntity> tileEntityEntry) {
        this.tileEntityEntry = tileEntityEntry;
    }

    @Override
    protected BlockEntry<T> createEntryWrapper(final RegistryEntry<Block, T> entry) {
        return new BlockEntry<>(entry.name(), entry.registry());
    }

    @Override
    protected T createEntry() {
        final T entry = factory.get();
        entry.setRegistryName(new ResourceLocation(owner.modId(), name));
        entry.setTranslationKey(owner.modId() + "." + name);
        return entry;
    }

    @Override
    public BlockEntry<T> register() {
        return (BlockEntry<T>) super.register();
    }
}
