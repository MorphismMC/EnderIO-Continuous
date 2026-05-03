package com.morphismmc.enderio.api.register.entry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class RegistryEntry<R extends IForgeRegistryEntry<R>, T extends R> {

    protected final ResourceLocation name;
    protected final IForgeRegistry<R> registry;

    private @Nullable T value;
    private boolean lenient;

    public RegistryEntry(ResourceLocation name, IForgeRegistry<R> registry) {
        this.name = name;
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public @Nullable T get() {
        if (!lenient) {
            value = (T) registry.getValue(name);
            lenient = true;
        }
        return value;
    }

    public boolean isPresent() {
        return registry.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public static @Nullable <R extends IForgeRegistryEntry<R>, T extends R> RegistryEntry<R, T> cast(@Nullable RegistryEntry<?, ?> entry) {
        return (RegistryEntry<R, T>) entry;
    }

    public ResourceLocation name() {
        return name;
    }

    public IForgeRegistry<R> registry() {
        return registry;
    }
}
