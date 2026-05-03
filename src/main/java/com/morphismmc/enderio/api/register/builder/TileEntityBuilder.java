package com.morphismmc.enderio.api.register.builder;

import com.morphismmc.enderio.api.register.ModRegistration;
import com.morphismmc.enderio.api.register.entry.TileEntityEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TileEntityBuilder<T extends TileEntity, P> {

    private final ModRegistration owner;
    private final String name;
    private final Class<T> teClass;

    private final @Nullable P parent;

    private final ObjectList<Consumer<? super T>> onCreateCallbacks = new ObjectArrayList<>();

    public TileEntityBuilder(final ModRegistration owner,
                             final String name,
                             final Class<T> teClass,
                             @Nullable final P parent) {
        this.owner = owner;
        this.name = name;
        this.teClass = teClass;
        this.parent = parent;
    }

    public String name() {
        return name;
    }

    public Class<T> tileEntityClass() {
        return teClass;
    }

    public @Nullable P parent() {
        return parent;
    }

    public TileEntityBuilder<T, P> onRegister(Consumer<? super T> callback) {
        onCreateCallbacks.add(callback);
        return this;
    }

    public P build() {
        if (parent == null) {
            throw new IllegalStateException("Cannot build a root BlockEntityBuilder — use register() instead");
        }
        register();
        return parent;
    }

    public TileEntityEntry<T> register() {
        final TileEntityEntry<T> entry = new TileEntityEntry<>(new ResourceLocation(owner.modId(), name), teClass);

        if (parent instanceof BlockBuilder<?, ?> parentBlock) {
            parentBlock.setTileEntityEntry(entry);
        }

        owner.tileEntityPre(new ResourceLocation(owner.modId(), name), teClass);
        owner.tileEntity(entry);

        return entry;
    }
}
