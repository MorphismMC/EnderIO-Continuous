package crazypants.enderio.machines.machine.obelisk.weather;

import static crazypants.enderio.machines.init.MachineObject.block_weather_obelisk;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import crazypants.enderio.machines.machine.obelisk.render.ObeliskSpecialRenderer;

public class WeatherObeliskSpecialRenderer extends ObeliskSpecialRenderer<TileWeatherObelisk> {

    public WeatherObeliskSpecialRenderer(@Nonnull ItemStack itemStack) {
        super(itemStack, block_weather_obelisk.getBlock());
    }

    @Override
    protected void renderItemStack(TileWeatherObelisk te, @Nonnull World world, double x, double y, double z,
                                   float tick) {
        if (te == null || te.isActive()) {
            super.renderItemStack(te, world, x, y, z, tick);
        }
    }
}
