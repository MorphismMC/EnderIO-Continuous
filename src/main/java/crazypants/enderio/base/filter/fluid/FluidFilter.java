package crazypants.enderio.base.filter.fluid;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import crazypants.enderio.base.filter.Filter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FluidFilter extends Filter {

    /**
     * Checks if the filter has no {@link FluidStack}s.
     *
     * @return Returns {@code true} if the filter has no fluids.
     */
    @Override
    boolean isEmpty();

    /**
     * Get the number of fluids in the filter.
     *
     * @return The number of fluids in the filter.
     */
    int size();

    /**
     * Get the fluid stack at the given index.
     *
     * @param index The index of the fluid.
     * @return      The {@link FluidStack} at the given index, returns {@code null} if there is none.
     */
    @Nullable
    FluidStack getFluidStackAt(int index);

    /**
     * Set the fluid in the given slot.
     *
     * @param index The index of the slot.
     * @param fluid The {@link FluidStack} to insert, fluid can be null to make the slot empty.
     * @return      Returns {@code true} if the fluid was successfully set, otherwise returns {@code false}.
     */
    boolean setFluid(int index, @Nullable FluidStack fluid);

    /**
     * Set the fluid from the {@link ItemStack}
     *
     * @param index The index of the fluid filter.
     * @param stack The {@link ItemStack} to get the fluid from.
     * @return      Returns {@code true} if the fluid is successfully set, otherwise returns {@code false}.
     */
    boolean setFluid(int index, @NotNull ItemStack stack);

    /**
     * Remove a fluid at the given index.
     *
     * @param index The index of the fluid to remove.
     * @return      Returns {@code true} if the fluid is successfully removed, otherwise returns {@code false}.
     */
    boolean removeFluid(int index);

    /**
     * Check the whitelist/blacklist setting of the filter.
     *
     * @return Returns {@code true} if the blacklist is active, otherwise returns {@code false}.
     */
    boolean isBlacklist();

    /**
     * Set the blacklist/whitelist button.
     *
     * @param isBlacklist Used {@code true} if it should be a blacklist, {@code false} for whitelist.
     */
    void setBlacklist(boolean isBlacklist);

    /**
     * Check if the filter matches the default filter setting.
     *
     * @return Returns {@code true} if the filter has no different settings to a freshly made one.
     */
    boolean isDefault();

    /**
     * Check if the fluid matches the filter.
     *
     * @param drained The {@link FluidStack} to check.
     * @return        Returns {@code true} if it matches the filter settings.
     */
    boolean matchesFilter(FluidStack drained);

    /**
     * Get the number of slots in the fluid filter.
     *
     * @return The number of slots for the filter.
     */
    int getSlotCount();

}
