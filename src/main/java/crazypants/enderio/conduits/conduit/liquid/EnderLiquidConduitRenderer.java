package crazypants.enderio.conduits.conduit.liquid;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.ConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.conduits.render.ConduitInOutRenderer;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;

public class EnderLiquidConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(@Nonnull Conduit conduit) {
        if (conduit instanceof EnderLiquidConduit) {
            return true;
        }
        return false;
    }

    @Override
    public @Nonnull BlockRenderLayer getCoreLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    protected void addConduitQuads(@Nonnull ConduitBundle bundle, @Nonnull ConduitClient conduit,
                                   @Nonnull ConduitTexture tex,
                                   @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer,
                                   @Nonnull List<BakedQuad> quads) {
        super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);

        EnderLiquidConduit pc = (EnderLiquidConduit) conduit;
        EnumFacing dir = component.getDirection();
        ConduitInOutRenderer.renderIO(bundle, conduit, component, layer, quads, pc.getInputColor(dir),
                pc.getOutputColor(dir));
    }
}
