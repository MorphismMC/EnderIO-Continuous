package com.morphismmc.enderio.api.register.builder;

import com.morphismmc.enderio.api.register.entry.RegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface RegistryEntryBuilderOp {

    <R extends IForgeRegistryEntry<R>, T extends R> RegistryEntry<R, T> accept(
            final String name,
            final IForgeRegistry<R> registry,
            final RegistryEntryBuilder<R, T, ?, ?> builder,
            final Supplier<? extends T> creator,
            final Function<RegistryEntry<R, T>, ? extends RegistryEntry<R, T>> entryFactory);
}
