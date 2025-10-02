package crazypants.enderio.conduits.conduit;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.conduits.render.BlockStateWrapperConduitBundle;

public interface ConduitComponent {

    @SideOnly(Side.CLIENT)
    void hashCodeForModelCaching(BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes);

    interface ConduitComponentProvider {

        @SideOnly(Side.CLIENT)
        void hashCodeForModelCaching(IBlockStateWrapper wrapper,
                                     BlockStateWrapperConduitBundle.ConduitCacheKey hashCodes);
    }

}
