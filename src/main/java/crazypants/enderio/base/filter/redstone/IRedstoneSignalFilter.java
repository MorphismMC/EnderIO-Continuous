package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;

import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.filter.Filter;
import io.netty.buffer.ByteBuf;

public interface IRedstoneSignalFilter extends Filter {

    @Override
    default void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {}

    @Override
    default void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {}

    @Override
    default void writeToByteBuf(@Nonnull ByteBuf buf) {
        NBTTagCompound root = new NBTTagCompound();
        writeToNBT(root);
        NetworkUtil.writeNBTTagCompound(root, buf);
    }

    @Override
    default void readFromByteBuf(@Nonnull ByteBuf buf) {
        NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
        readFromNBT(tag);
    }

    @Override
    default boolean isEmpty() {
        return true;
    }
}
