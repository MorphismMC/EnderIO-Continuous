package crazypants.enderio.base.filter;

import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface to allow the implementation of filter access for tile entities. Used on conduit bundles and vac chest.
 *
 */
public interface TileFilterContainer {

    /**
     * Get a filter from the tile. This is only used by the existing item filter.
     * 
     * @param filterIndex The id of the filter, specifically used by conduits to identify if the filter is a input or
     *                    output filter.
     * @param param1      An extra parameter for even more specific filter selection, see conduits using it for facing.
     * @return            The {@link Filter} of the type.
     */
    Filter getFilter(int filterIndex, int param1);

    /**
     * Sets the filter of the tile.
     * 
     * @param filterIndex The id of the filter location.
     * @param param1      Useful extra parameter for things that cannot be expressed simply by the filter index.
     * @param filter      The filter to set it to.
     */
    void setFilter(int filterIndex, int param1, @NotNull Filter filter);

    /**
     * Used by the existing item filter to get the inventory it is connected to
     * 
     * @param filterIndex The index of the filter.
     * @param param1      Extra param for the filter (e.g. {@code EnumFacing.ordinal()} for conduits).
     * @return            The inventory for the snapshot.
     */
    @Nullable
    default IItemHandler getInventoryForSnapshot(int filterIndex, int param1) {
        return null;
    }

}
