package crazypants.enderio.conduit.me.conduit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import appeng.api.AEApi;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.conduits.conduit.AbstractConduitItem;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemMEConduit extends AbstractConduitItem {

    public static ItemMEConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        if (MEUtil.isMEEnabled()) {
            return new ItemMEConduit(modObject);
        }
        return null;
    }

    protected ItemMEConduit(@NotNull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
                new ItemConduitSubtype(modObject.getUnlocalisedName() + "_dense",
                        modObject.getRegistryName() + "_dense"));

        var definition = ConduitBuilder.builder()
                .id(EnderIO.id("appliedenergistics"))
                .baseType(getBaseConduitType())
                .offsets(Offset.EAST_UP, Offset.SOUTH_UP, Offset.NORTH_EAST, Offset.EAST_UP)
                .build();

        ConduitRegistry.register(definition
                .id(EnderIO.id("appliedenergistics_conduit"))
                .baseType(MEConduitImpl.class)
                .build()
                .finish());

        ConduitDisplayMode.registerDisplayMode(
                new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_ME, IconEIO.WRENCH_OVERLAY_ME_OFF));
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return MEConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack item, @NotNull EntityPlayer player) {
        MEConduitImpl conduit = new MEConduitImpl(item.getItemDamage());
        conduit.setPlayerID(AEApi.instance().registries().players().getID(player));
        return conduit;
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
