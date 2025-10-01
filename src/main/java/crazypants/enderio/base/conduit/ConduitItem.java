package crazypants.enderio.base.conduit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import crazypants.enderio.api.tool.IHideFacades;
import org.jetbrains.annotations.NotNull;

public interface ConduitItem extends IHideFacades {

    /**
     * @see Conduit#getBaseConduitType()
     */
    @NotNull
    Class<? extends Conduit> getBaseConduitType();

    ConduitServer createConduit(@NotNull ItemStack item, @NotNull EntityPlayer player);

}
