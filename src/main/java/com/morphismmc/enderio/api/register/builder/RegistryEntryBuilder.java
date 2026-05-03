package com.morphismmc.enderio.api.register.builder;

import com.morphismmc.enderio.api.register.entry.RegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistryEntryBuilder<R extends IForgeRegistryEntry<R>,
                                      T extends R,
                                      P,
                                      S extends RegistryEntryBuilder<R, T, P, S>> extends Supplier<RegistryEntry<R, T>> {

    String name();

    IForgeRegistry<R> registry();

    P parent();

    P build();

    RegistryEntry<R, T> register();

    @SuppressWarnings("unchecked")
    default <S2> S2 map(final Function<? super S, S2> f) {
        return f.apply((S) this);
    }
    
    @Override
    default RegistryEntry<R, T> get() {
        return register();
    }
}
