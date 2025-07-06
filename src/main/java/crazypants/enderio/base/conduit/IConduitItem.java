package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import crazypants.enderio.api.tool.IHideFacades;

public interface IConduitItem extends IHideFacades {

    @Nonnull
    Class<? extends IConduit> getBaseConduitType();

    IServerConduit createConduit(@Nonnull ItemStack item, @Nonnull EntityPlayer player);
}
