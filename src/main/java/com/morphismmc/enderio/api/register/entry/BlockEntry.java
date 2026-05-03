package com.morphismmc.enderio.api.register.entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import org.jetbrains.annotations.Nullable;

public class BlockEntry<T extends Block> extends RegistryEntry<Block, T> {

    public BlockEntry(final ResourceLocation name, final IForgeRegistry<Block> registry) {
        super(name, registry);
    }

    public @Nullable IBlockState getDefaultState() {
        final T block = get();
        return block != null ? block.getDefaultState() : null;
    }

    public boolean hasBlockState(final IBlockState state) {
        final T block = get();
        return block != null && state.getBlock() == block;
    }

    // TODO: Shortcut block -> item methods like gtlitecore kt extension?
}
