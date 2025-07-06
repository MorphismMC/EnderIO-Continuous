package crazypants.enderio.base.render.ranged;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.render.BoundingBox;

public interface IRanged {

    @SideOnly(Side.CLIENT)
    boolean isShowingRange();

    @Nonnull
    BoundingBox getBounds();
}
