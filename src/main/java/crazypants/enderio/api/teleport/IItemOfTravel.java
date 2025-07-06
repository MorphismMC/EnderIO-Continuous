package crazypants.enderio.api.teleport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import info.loenwind.autoconfig.factory.IValue;

public interface IItemOfTravel {

    boolean isActive(EntityPlayer ep, ItemStack equipped);

    void extractInternal(ItemStack item, int power);

    void extractInternal(ItemStack item, IValue<Integer> power);

    int getEnergyStored(ItemStack item);
}
