package crazypants.enderio.conduits.conduit.redstone;

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
import crazypants.enderio.conduits.conduit.AbstractConduitItem;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRedstoneConduit extends AbstractConduitItem {

    public static ItemRedstoneConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemRedstoneConduit(modObject);
    }

    protected ItemRedstoneConduit(@NotNull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName() + "_insulated",
                modObject.getRegistryName() + "_insulated"));

        var definition = ConduitBuilder.builder()
                .id(EnderIO.id("redstone"))
                .baseType(getBaseConduitType())
                .offsets(Offset.UP, Offset.UP, Offset.NORTH, Offset.UP)
                .canConnectToAnything()
                .build();

        ConduitRegistry.register(definition
                .id(EnderIO.id("redstone_conduit"))
                .baseType(InsulatedRedstoneConduit.class)
                .build()
                .finish());

        ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(),
                IconEIO.WRENCH_OVERLAY_REDSTONE, IconEIO.WRENCH_OVERLAY_REDSTONE_OFF));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderers(@NotNull IModObject modObject) {
        super.registerRenderers(modObject);
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new InsulatedRedstoneConduitRenderer());
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return RedstoneConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return new InsulatedRedstoneConduit();
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
