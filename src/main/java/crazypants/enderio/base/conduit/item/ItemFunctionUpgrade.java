package crazypants.enderio.base.conduit.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemFunctionUpgrade extends Item implements IResourceTooltipProvider {

    @NotNull
    private final FunctionUpgrade upgradeType;

    protected ItemFunctionUpgrade(@NotNull IModObject modObject, @NotNull FunctionUpgrade upgradeType) {
        setCreativeTab(EnderIOTab.tabEnderIOItems);
        modObject.apply(this);
        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(64);
        this.upgradeType = upgradeType;
    }

    public static ItemFunctionUpgrade createUpgrade(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemFunctionUpgrade(modObject, FunctionUpgrade.EXTRACT_SPEED_UPGRADE);
    }

    public static ItemFunctionUpgrade createDowngrade(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemFunctionUpgrade(modObject, FunctionUpgrade.EXTRACT_SPEED_DOWNGRADE);
    }

    public static ItemFunctionUpgrade createRSCraftingUpgrade(@NotNull IModObject modObject, @Nullable Block block) {
        return new ItemFunctionUpgrade(modObject, FunctionUpgrade.RS_CRAFTING_UPGRADE);
    }

    public static ItemFunctionUpgrade createRSCraftingSpeedUpgrade(@NotNull IModObject modObject,
                                                                   @Nullable Block block) {
        return new ItemFunctionUpgrade(modObject, FunctionUpgrade.RS_CRAFTING_SPEED_UPGRADE);
    }

    public static ItemFunctionUpgrade createRSCraftingSpeedDowngrade(@NotNull IModObject modObject,
                                                                     @Nullable Block block) {
        return new ItemFunctionUpgrade(modObject, FunctionUpgrade.RS_CRAFTING_SPEED_DOWNGRADE);
    }

    @Nullable
    public static FunctionUpgrade getFunctionUpgrade(@NotNull ItemStack stack) {
        var upgradeItem = stack.getItem();
        if (upgradeItem instanceof ItemFunctionUpgrade upgradeStack) {
            return upgradeStack.getFunctionUpgrade();
        }
        return null;
    }

    @NotNull
    public FunctionUpgrade getFunctionUpgrade() {
        return upgradeType;
    }

    @NotNull
    @Override
    public String getUnlocalizedNameForTooltip(@NotNull ItemStack stack) {
        return getTranslationKey(stack);
    }

    public int getUpgradeSlotLimit() {
        return upgradeType.getMaxStackSize();
    }

}
