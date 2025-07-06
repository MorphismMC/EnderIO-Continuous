package crazypants.enderio.base.invpanel.database;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import crazypants.enderio.util.IMapKey;

public interface IItemEntry extends IMapKey {

    Item getItem();

    int getDbID();

    int getHash();

    int getItemID();

    int getMeta();

    NBTTagCompound getNbt();

    boolean equals(int itemID, int meta, NBTTagCompound nbt);
}
