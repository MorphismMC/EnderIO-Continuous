package com.morphismmc.enderio.api.register.builder;

import com.morphismmc.enderio.api.register.ModRegistration;
import com.morphismmc.enderio.api.register.entry.RegistryEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class DefaultRegistryEntryBuilder<R extends IForgeRegistryEntry<R>,
        T extends R,
        P,
        S extends DefaultRegistryEntryBuilder<R, T, P, S>> implements RegistryEntryBuilder<R, T, P, S> {

    protected final ModRegistration owner;
    protected final String name;
    protected final IForgeRegistry<R> registry;
    protected final Supplier<? extends T> factory;

    protected final @Nullable P parent;

    private final ObjectList<Consumer<? super T>> onRegisterCallbacks = new ObjectArrayList<>();

    @SuppressWarnings("unchecked")
    private S self() {
        return (S) this;
    }

    protected DefaultRegistryEntryBuilder(final ModRegistration owner,
                                          final String name,
                                          final IForgeRegistry<R> registry,
                                          final Supplier<? extends T> factory,
                                          @Nullable final P parent) {
        this.owner = owner;
        this.name = name;
        this.registry = registry;
        this.factory = factory;
        this.parent = parent;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public IForgeRegistry<R> registry() {
        return registry;
    }

    @Override
    public @Nullable P parent() {
        return parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public P build() {
        register();
        if (parent == null) {
            return (P) this;
        }
        return parent;
    }

    @Override
    public RegistryEntry<R, T> register() {
        return owner.accept(name, registry, this, this::createEntry, this::createEntryWrapper);
    }

    public S onRegister(final Consumer<? super T> callback) {
        onRegisterCallbacks.add(callback);
        return self();
    }

    public void fireOnRegisterCallbacks(final T entry) {
        onRegisterCallbacks.forEach(c -> c.accept(entry));
    }

    protected RegistryEntry<R, T> createEntryWrapper(final RegistryEntry<R, T> entry) {
        return entry;
    }

    protected T createEntry() { // TODO: More extendable?
        final T entry = factory.get();
        switch (entry) {
            case Block blockEntry -> {
                blockEntry.setRegistryName(new ResourceLocation(owner.modId(), name()));
                blockEntry.setTranslationKey(owner.modId() + "." + name());
            }
            case Item  itemEntry  -> {
                itemEntry.setRegistryName(new ResourceLocation(owner.modId(), name()));
                itemEntry.setTranslationKey(owner.modId() + "." + name());
            }
            default -> {}
        }
        return entry;
    }
}
