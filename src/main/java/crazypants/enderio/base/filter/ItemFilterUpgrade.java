package crazypants.enderio.base.filter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.machine.interfaces.IClearableConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemFilterUpgrade<T extends Filter> extends IClearableConfiguration, IEioGuiHandler.WithPos {

    T createFilterFromStack(@NotNull ItemStack stack);

    @Nullable
    @Override
    default Container getServerGuiElement(@NotNull EntityPlayer player, @NotNull World world, @NotNull BlockPos pos,
                                          @Nullable EnumFacing facing, int param1) {
        return new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1);
    }

}
