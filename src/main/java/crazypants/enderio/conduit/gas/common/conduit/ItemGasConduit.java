package crazypants.enderio.conduit.gas.common.conduit;

import java.util.List;

import crazypants.enderio.conduit.gas.EnderIOConduitsMekanism;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.api.IModObject;
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
import crazypants.enderio.conduit.gas.common.conduit.advanced.AdvancedGasConduitImpl;
import crazypants.enderio.conduit.gas.common.conduit.advanced.AdvancedGasConduitRenderer;
import crazypants.enderio.conduit.gas.common.conduit.basic.GasConduitImpl;
import crazypants.enderio.conduit.gas.common.conduit.basic.GasConduitRenderer;
import crazypants.enderio.conduit.gas.common.conduit.ender.EnderGasConduitImpl;
import crazypants.enderio.conduit.gas.common.conduit.ender.EnderGasConduitRenderer;
import crazypants.enderio.conduit.gas.common.config.GasConduitConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemGasConduit extends AbstractConduitItem implements IAdvancedTooltipProvider {

    public static ItemGasConduit create(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemGasConduit(modObject);
    }

    protected ItemGasConduit(@NotNull IModObject modObject) {
        super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
                new ItemConduitSubtype(modObject.getUnlocalisedName() + "_advanced",
                        modObject.getRegistryName() + "_advanced"),
                new ItemConduitSubtype(modObject.getUnlocalisedName() + "_ender",
                        modObject.getRegistryName() + "_ender"));

        var definition = ConduitBuilder.builder()
                .id(EnderIOConduitsMekanism.id("gas"))
                .baseType(getBaseConduitType())
                .offsets(Offset.EAST_DOWN, Offset.SOUTH_DOWN, Offset.SOUTH_EAST, Offset.EAST_DOWN)
                .build();

        ConduitRegistry.register(definition
                .id(EnderIOConduitsMekanism.id("gas_conduit"))
                .baseType(GasConduitImpl.class)
                .build()
                .id(EnderIOConduitsMekanism.id("advanced_gas_conduit"))
                .baseType(AdvancedGasConduitImpl.class)
                .build()
                .id(EnderIOConduitsMekanism.id("ender_gas_conduit"))
                .baseType(EnderGasConduitImpl.class)
                .build()
                .finish());

        ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_GAS,
                IconEIO.WRENCH_OVERLAY_GAS_OFF));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderers(@NotNull IModObject modObject) {
        super.registerRenderers(modObject);
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(GasConduitRenderer.create());
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new AdvancedGasConduitRenderer());
        ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new EnderGasConduitRenderer());
    }

    @NotNull
    @Override
    public Class<? extends Conduit> getBaseConduitType() {
        return GasConduit.class;
    }

    @Override
    public ConduitServer createConduit(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        if (stack.getItemDamage() == 1) {
            return new AdvancedGasConduitImpl();
        } else if (stack.getItemDamage() == 2) {
            return new EnderGasConduitImpl();
        }
        return new GasConduitImpl();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addCommonEntries(@NotNull ItemStack stack, @Nullable EntityPlayer player,
                                 @NotNull List<String> list, boolean flag) {}

    @SideOnly(Side.CLIENT)
    @Override
    public void addBasicEntries(@NotNull ItemStack stack, @Nullable EntityPlayer player,
                                @NotNull List<String> list, boolean flag) {}

    @SideOnly(Side.CLIENT)
    @Override
    public void addDetailedEntries(@NotNull ItemStack stack, @Nullable EntityPlayer player,
                                   @NotNull List<String> list, boolean flag) {
        int extractRate;
        int maxIo;

        if (stack.getItemDamage() == 0) {
            extractRate = GasConduitConfig.tier1_extractRate.get();
            maxIo = GasConduitConfig.tier1_maxIO.get();
        } else if (stack.getItemDamage() == 1) {
            extractRate = GasConduitConfig.tier2_extractRate.get();
            maxIo = GasConduitConfig.tier2_maxIO.get();
        } else {
            extractRate = GasConduitConfig.tier3_extractRate.get();
            maxIo = GasConduitConfig.tier3_maxIO.get();
        }

        String mbt = new TextComponentTranslation("gasconduits.gas.millibuckets_tick").getUnformattedComponentText();
        list.add(new TextComponentTranslation("gasconduits.item_gas_conduit.tooltip.max_extract")
                .getUnformattedComponentText() + " " + extractRate + mbt);
        list.add(new TextComponentTranslation("gasconduits.item_gas_conduit.tooltip.max_io")
                .getUnformattedComponentText() + " " + maxIo + mbt);

        if (stack.getItemDamage() == 0) {
            SpecialTooltipHandler.addDetailedTooltipFromResources(list, "gasconduits.item_gas_conduit");
        }
    }

    @Override
    public boolean shouldHideFacades(@NotNull ItemStack stack, @NotNull EntityPlayer player) {
        return true;
    }

}
