package crazypants.enderio.conduit.gas.common.filter;

import net.minecraft.item.ItemStack;

import crazypants.enderio.base.filter.IFilter;
import mekanism.api.gas.GasStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GasFilter extends IFilter {

    /**
     * Check if the filter has no {@link GasStack}s.
     *
     * @return Returns {@code true} if the filter has no gases, otherwise returns {@code false}.
     */
    @Override
    boolean isEmpty();

    /**
     * Get the number of gases in the filter.
     *
     * @return The number of gases in the filter.
     */
    int size();

    /**
     * Get the gas stack at the given index.
     *
     * @param index The index of the gas.
     * @return      The {@link GasStack} at the given index, {@code null} if there is none.
     */
    @Nullable
    GasStack getGasStackAt(int index);

    /**
     * Set the gas in the given slot.
     *
     * @param index The index of the slot.
     * @param gas   The {@link GasStack} to insert, gas can be null to make the slot empty.
     * @return      Returns {@code true} if the gas was successfully set, otherwise returns {@code false}.
     */
    boolean setGas(int index, @Nullable GasStack gas);

    /**
     * Set the gas from the {@link ItemStack}.
     *
     * @param index The index of the gas filter.
     * @param stack The {@link ItemStack} to get the gas from.
     * @return      Returns {@code true} if the gas is successfully set, otherwise returns {@code false}.
     */
    boolean setGas(int index, @NotNull ItemStack stack);

    /**
     * Check the whitelist/blacklist setting of the filter.
     *
     * @return Returns {@code true} if the blacklist is active.
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
     * Check if the gas matches the filter
     *
     * @param drained The {@link GasStack} to check.
     * @return        Returns {@code true} if it matches the filter settings.
     */
    boolean matchesFilter(GasStack drained);

    /**
     * Get the number of slots in the gas filter.
     *
     * @return The number of slots for the filter.
     */
    int getSlotCount();

}
