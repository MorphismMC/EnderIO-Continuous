package crazypants.enderio.conduits.conduit.power;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
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
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.conduits.conduit.AbstractConduitItem;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemPowerConduit extends AbstractConduitItem {

    public static ItemPowerConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemPowerConduit(modObject);
    }

    protected ItemPowerConduit(@NotNull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
                new ItemConduitSubtype(modObject.getUnlocalisedName() + "_enhanced",
                        modObject.getRegistryName() + "_enhanced"),
                new ItemConduitSubtype(modObject.getUnlocalisedName() + "_ender",
                        modObject.getRegistryName() + "_ender"));

        var definition = ConduitBuilder.builder()
                .id(EnderIO.id("power"))
                .baseType(getBaseConduitType())
                .offsets(Offset.DOWN, Offset.DOWN, Offset.SOUTH, Offset.DOWN)
                .build();

        ConduitRegistry.register(definition
                .id(EnderIO.id("power_conduit"))
                .baseType(PowerConduitImpl.class)
                .build()
                .finish());

        ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(),
                IconEIO.WRENCH_OVERLAY_POWER, IconEIO.WRENCH_OVERLAY_POWER_OFF));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderers(@NotNull IModObject modObject) {
        super.registerRenderers(modObject);
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new PowerConduitRenderer());
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return PowerConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return new PowerConduitImpl(PowerConduitData.Registry.fromID(stack.getItemDamage()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@NotNull ItemStack stack,
                               @Nullable World world,
                               @NotNull List<String> tooltip,
                               @NotNull ITooltipFlag flag) {
        String prefix = EnderIO.lang.localize("power.max_output") + " ";
        super.addInformation(stack, world, tooltip, flag);
        int cap = PowerConduitImpl.getMaxEnergyIO(PowerConduitData.Registry.fromID(stack.getItemDamage()));
        tooltip.add(prefix + LangPower.RFt(cap));
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
