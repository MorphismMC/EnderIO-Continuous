package crazypants.enderio.conduit.oc.conduit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import crazypants.enderio.conduit.oc.OCUtil;
import crazypants.enderio.conduit.oc.init.ConduitOpenComputersObject;
import crazypants.enderio.conduits.conduit.AbstractConduitItem;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemOCConduit extends AbstractConduitItem {

    public static ItemOCConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        if (OCUtil.isOCEnabled()) {
            return new ItemOCConduit(modObject);
        }
        return null;
    }

    protected ItemOCConduit(@NotNull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(ConduitOpenComputersObject.item_opencomputers_conduit.getUnlocalisedName(),
                "enderio:item_oc_conduit"));

        var definition = ConduitBuilder.builder()
                .id(EnderIO.id("opencomputers"))
                .baseType(getBaseConduitType())
                .offsets(Offset.WEST_DOWN, Offset.NORTH_DOWN, Offset.SOUTH_WEST, Offset.WEST_DOWN)
                .build();

        ConduitRegistry.register(definition
                .id(EnderIO.id("opencomputers_conduit"))
                .baseType(OCConduitImpl.class)
                .build().finish());

        ConduitDisplayMode.registerDisplayMode(
                new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_OC, IconEIO.WRENCH_OVERLAY_OC_OFF));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderers(@NotNull IModObject modObject) {
        super.registerRenderers(modObject);
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new OCConduitRenderer());
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return OCConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack item, @NotNull EntityPlayer player) {
        return new OCConduitImpl(item.getItemDamage());
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
