package crazypants.enderio.conduits.conduit.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandler;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitTexture;
import crazypants.enderio.base.conduit.ConduitExtractor;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.filter.item.ItemFilter;
import crazypants.enderio.conduits.conduit.ConduitEnder;
import org.jetbrains.annotations.NotNull;

public interface ItemConduit extends ConduitExtractor, ConduitServer, ConduitClient, ConduitEnder {

    ConduitTexture getEnderIcon();

    IItemHandler getExternalInventory(@NotNull EnumFacing direction);

    int getMaximumExtracted(@NotNull EnumFacing direction);

    float getTickTimePerItem(@NotNull EnumFacing direction);

    void itemsExtracted(int numInserted, int slot);

    void setInputFilterUpgrade(@NotNull EnumFacing direction, @NotNull ItemStack stack);

    void setOutputFilterUpgrade(@NotNull EnumFacing direction, @NotNull ItemStack stack);

    @NotNull
    ItemStack getInputFilterUpgrade(@NotNull EnumFacing direction);

    @NotNull
    ItemStack getOutputFilterUpgrade(@NotNull EnumFacing direction);

    void setInputFilter(@NotNull EnumFacing direction, @NotNull ItemFilter filter);

    void setOutputFilter(@NotNull EnumFacing dir, @NotNull ItemFilter filter);

    ItemFilter getInputFilter(@NotNull EnumFacing direction);

    ItemFilter getOutputFilter(@NotNull EnumFacing direction);

    void setFunctionUpgrade(@NotNull EnumFacing direction, @NotNull ItemStack upgrade);

    @NotNull
    ItemStack getFunctionUpgrade(@NotNull EnumFacing direction);

    int getMetaData();

    boolean isExtractionRedstoneConditionMet(@NotNull EnumFacing direction);

}
