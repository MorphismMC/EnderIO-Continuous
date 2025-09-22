package com.morphismmc.eioadditions.common;

import com.enderio.core.common.util.NullHelper;
import com.morphismmc.eioadditions.AdditionsConstants;
import com.morphismmc.eioadditions.common.item.ItemTeleportStaff;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObjectBase;
import crazypants.enderio.base.init.ModObjectRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum AdditionsObject implements IModObjectBase {
    
    itemTeleportStaff(ItemTeleportStaff::create);
    
    private final String unlocalisedName;

    @Nullable
    private final Function<IModObject, Block> blockMaker;

    @Nullable
    private final BiFunction<IModObject, Block, Item> itemMaker;

    @Nullable
    private final IModTileEntity modTileEntity;

    AdditionsObject(BiFunction<IModObject, Block, Item> itemMaker) {
        this(null, itemMaker, null);
    }

    AdditionsObject(Function<IModObject, Block> blockMaker) {
        this(blockMaker, null, null);
    }

    AdditionsObject(Function<IModObject, Block> blockMaker,
                    BiFunction<IModObject, Block, Item> itemMaker) {
        this(blockMaker, itemMaker, null);
    }

    AdditionsObject(Function<IModObject, Block> blockMaker, IModTileEntity modTileEntity) {
        this(blockMaker, null, modTileEntity);
    }

    AdditionsObject(@Nullable Function<IModObject, Block> blockMaker,
                    @Nullable BiFunction<IModObject, Block, Item> itemMaker, @Nullable IModTileEntity modTileEntity) {
        this.unlocalisedName = ModObjectRegistry.sanitizeName(NullHelper.notnullJ(name(), "Enum.name()"));
        this.blockMaker = blockMaker;
        this.itemMaker = itemMaker;
        if (blockMaker == null && itemMaker == null) {
            throw new RuntimeException(this + " unexpectedly is neither a Block nor an Item.");
        }
        this.modTileEntity = null;
    }
    
    @Override
    public final String getUnlocalisedName() {
        return unlocalisedName;
    }
    
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(AdditionsConstants.MOD_ID, getUnlocalisedName());
    }

    @Override
    @Nullable
    public IModTileEntity getTileEntity() {
        return modTileEntity;
    }
    
    @Override
    public final <B extends Block> B apply(B blockIn) {
        blockIn.setCreativeTab(EnderIOTab.tabEnderIOConduits);
        return IModObjectBase.super.apply(blockIn);
    }
    
    @Override
    public Function<IModObject, Block> getBlockCreator() {
        return blockMaker != null ? blockMaker : mo -> null;
    }
    
    @Override
    public BiFunction<IModObject, Block, Item> getItemCreator() {
        return NullHelper.first(itemMaker, WithBlockItem.itemCreator);
    }
}
