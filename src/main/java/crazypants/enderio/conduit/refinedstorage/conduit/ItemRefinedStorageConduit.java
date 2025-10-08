package crazypants.enderio.conduit.refinedstorage.conduit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.conduit.AbstractConduitItem;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRefinedStorageConduit extends AbstractConduitItem {

    public static ItemRefinedStorageConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemRefinedStorageConduit(modObject);
    }

    protected ItemRefinedStorageConduit(@NotNull IModObject modObject) {
        super(modObject,
                new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()));

        var definition = ConduitBuilder.builder()
                .id(EnderIO.id("refinedstorage"))
                .baseType(getBaseConduitType())
                .offsets(Offset.WEST_UP, Offset.NORTH_UP, Offset.NORTH_WEST, Offset.WEST_UP)
                .build();

        ConduitRegistry.register(definition
                .id(EnderIO.id("refinedstorage_conduit"))
                .baseType(RefinedStorageConduitImpl.class)
                .build()
                .finish());

        ConduitDisplayMode.registerDisplayMode(
                new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_RS, IconEIO.WRENCH_OVERLAY_RS_OFF));
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return RefinedStorageConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return new RefinedStorageConduitImpl();
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
