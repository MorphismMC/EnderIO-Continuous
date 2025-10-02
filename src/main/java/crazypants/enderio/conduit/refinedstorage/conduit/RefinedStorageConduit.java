package crazypants.enderio.conduit.refinedstorage.conduit;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.filter.Filter;
import crazypants.enderio.base.filter.capability.FilterHolder;
import crazypants.enderio.conduits.capability.UpgradeHolder;

public interface RefinedStorageConduit extends ConduitClient, ConduitServer,
                                               INetworkNodeProxy<ConduitRefinedStorageNode>, UpgradeHolder,
        FilterHolder<Filter> {

    public static final int INDEX_INPUT_REFINED_STORAGE = 7;
    public static final int INDEX_OUTPUT_REFINED_STROAGE = 8;

    public static final @Nonnull String ICON_KEY = "blocks/refined_storage_conduit";
    public static final @Nonnull String ICON_CORE_KEY = "blocks/refined_storage_conduit_core";

    void setInputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack);

    void setOutputFilterUpgrade(@Nonnull EnumFacing dir, @Nonnull ItemStack stack);

    @Nonnull
    ItemStack getInputFilterUpgrade(@Nonnull EnumFacing dir);

    @Nonnull
    ItemStack getOutputFilterUpgrade(@Nonnull EnumFacing dir);

    void setInputFilter(@Nonnull EnumFacing dir, @Nonnull Filter filter);

    void setOutputFilter(@Nonnull EnumFacing dir, @Nonnull Filter filter);

    Filter getInputFilter(@Nonnull EnumFacing dir);

    Filter getOutputFilter(@Nonnull EnumFacing dir);
}
