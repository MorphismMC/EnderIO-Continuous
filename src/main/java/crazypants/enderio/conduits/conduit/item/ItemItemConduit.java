package crazypants.enderio.conduits.conduit.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemItemConduit extends AbstractConduitItem {

    public static ItemItemConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemItemConduit(modObject);
    }

    protected ItemItemConduit(@NotNull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()));

        ConduitRegistry.register(ConduitBuilder.start()
                .setUUID(EnderIO.id("items"))
                .setClass(getBaseConduitType())
                .setOffsets(Offset.EAST, Offset.SOUTH, Offset.EAST, Offset.EAST)
                .build()
                .setUUID(EnderIO.id("item_conduit"))
                .setClass(ItemConduitImpl.class)
                .build()
                .finish());

        ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_ITEM,
                IconEIO.WRENCH_OVERLAY_ITEM_OFF));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderers(@NotNull IModObject modObject) {
        super.registerRenderers(modObject);
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new ItemConduitRenderer());
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return ItemConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack item, @NotNull EntityPlayer player) {
        return new ItemConduitImpl(item.getItemDamage());
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
