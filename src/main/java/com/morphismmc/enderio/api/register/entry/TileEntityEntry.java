package com.morphismmc.enderio.api.register.entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityEntry<T extends TileEntity> {

    private final ResourceLocation name;
    private final Class<T> teClass;

    public TileEntityEntry(final ResourceLocation name, final Class<T> teClass) {
        this.name = name;
        this.teClass = teClass;
    }

    public ResourceLocation getName() {
        return name;
    }

    public Class<T> getTileEntity() {
        return teClass;
    }
}
