package crazypants.enderio.base.conduit.item;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.base.EnderIO;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum FunctionUpgrade {

    INVENTORY_PANEL("inventory_panel_upgrade", "item.item_inventory_panel_upgrade", 1),
    EXTRACT_SPEED_UPGRADE("extract_speed_upgrade", "item.item_extract_speed_upgrade", 15) {

        @Override
        public int getMaximumExtracted(int stackSize) {
            return BASE_MAX_EXTRACTED + Math.min(stackSize, getMaxStackSize()) * 4;
        }

        @Override
        public float getFluidSpeedMultiplier(int stackSize) {
            return 1 + Math.min(getMaxStackSize(), stackSize);
        }
    },
    EXTRACT_SPEED_DOWNGRADE("extract_speed_downgrade", "item.item_extract_speed_downgrade", 3) {

        @Override
        public int getMaximumExtracted(int stackSize) {
            return stackSize;
        }

        @Override
        public float getFluidSpeedMultiplier(int stackSize) {
            return .25f * stackSize;
        }
    },

    RS_CRAFTING_UPGRADE("rs_crafting_upgrade", "item.item_rs_crafting_upgrade", 1),
    RS_CRAFTING_SPEED_UPGRADE("rs_crafting_upgrade", "item.item_rs_crafting_upgrade", 15) {

        @Override
        public int getMaximumExtracted(int stackSize) {
            return BASE_MAX_EXTRACTED + Math.min(stackSize, getMaxStackSize()) * 4;
        }
    },
    RS_CRAFTING_SPEED_DOWNGRADE("rs_crafting_speed_downgrade", "item.item_rs_crafting_speed_downgrade", 1) {

        @Override
        public int getMaximumExtracted(int stackSize) {
            return 1;
        }
    },

    ;

    public static final int BASE_MAX_EXTRACTED = 4;

    @NotNull
    public final String baseName;
    @NotNull
    public final String iconName;
    @NotNull
    public final String unlocalizedName;

    /**
     * -- GETTER --
     *
     * @return Maximum stack size allowed in the upgrade slot. Has no effect on the stack size in normal inventories.
     */
    @SuppressWarnings("JavadocDeclaration")
    @Getter
    private final int maxStackSize;

    FunctionUpgrade(@NotNull String name, @NotNull String unlocalizedName, int maxStackSize) {
        this.baseName = name;
        this.iconName = EnderIO.MODID + ":" + name;
        this.unlocalizedName = unlocalizedName;
        this.maxStackSize = maxStackSize;
    }

    public static List<ResourceLocation> resources() {
        List<ResourceLocation> res = new ArrayList<>(values().length);
        for (FunctionUpgrade c : values()) {
            res.add(new ResourceLocation(c.iconName));
        }
        return res;
    }

    public int getMaximumExtracted(int stackSize) {
        return BASE_MAX_EXTRACTED;
    }

    public static int getMaximumExtracted(@Nullable FunctionUpgrade upgrade, int stackSize) {
        return upgrade == null ? BASE_MAX_EXTRACTED : upgrade.getMaximumExtracted(stackSize);
    }

    public static int getMaximumExtracted(@NotNull ItemStack upgradeStack) {
        FunctionUpgrade upgrade = ItemFunctionUpgrade.getFunctionUpgrade(upgradeStack);
        return upgrade == null ? BASE_MAX_EXTRACTED : upgrade.getMaximumExtracted(upgradeStack.getCount());
    }

    /**
     * @param stackSize
     *                  size of the stack of upgrades
     * @return Multiplier of the base speed for fluid conduits.
     */
    public float getFluidSpeedMultiplier(int stackSize) {
        return 0;
    }

}
