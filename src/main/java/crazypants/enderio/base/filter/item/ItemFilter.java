package crazypants.enderio.base.filter.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.filter.IFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemFilter extends IFilter {

    /**
     * Checks if the given item passes the filter or not.
     * 
     * @param inv  The attached inventory - or null when used without an inventory (e.g. for a GUI).
     * @param item The item to check.
     * @return     Returns {@code true} if the item is allowed to pass, otherwise returns {@code false}.
     */
    default boolean doesItemPassFilter(@Nullable IItemHandler inv, @NotNull ItemStack item) {
        return getMaxCountThatPassesFilter(inv, item) > 0;
    }

    /**
     * Checks if the given item passes the filter or not, giving a limit to the amount that can pass.
     * 
     * @param inv  The attached inventory - or null when used without an inventory (eg for a GUI)
     * @param item The item to check
     * @return     Returns <tt>-1</tt> if the item is not allowed to pass, {@code Integer.MAX_VALUE} if the item can
     *             pass and there is no limit, otherwise the maximum number of items that pass.
     */
    default int getMaxCountThatPassesFilter(@Nullable IItemHandler inv, @NotNull ItemStack item) {
        return doesItemPassFilter(inv, item) ? Integer.MAX_VALUE : -1;
    }

    boolean isValid();

    default boolean isSticky() {
        return false;
    };

    /**
     * @return Returns {@code true} if {@link #getMaxCountThatPassesFilter} is implemented.
     */
    default boolean isLimited() {
        return false;
    };

    int getSlotCount();

    interface WithGhostSlots extends ItemFilter {

        void createGhostSlots(@NotNull NNList<GhostSlot> slots, int x, int y, @Nullable Runnable cb);
    }

}
