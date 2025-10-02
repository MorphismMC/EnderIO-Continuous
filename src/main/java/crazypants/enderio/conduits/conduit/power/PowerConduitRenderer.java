package crazypants.enderio.conduits.conduit.power;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.ConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.conduits.render.ConduitInOutRenderer;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;

public class PowerConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(@Nonnull Conduit conduit) {
        return conduit instanceof PowerConduit;
    }

    @Override
    protected void addConduitQuads(@Nonnull ConduitBundle bundle, @Nonnull ConduitClient conduit,
                                   @Nonnull ConduitTexture tex,
                                   @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer,
                                   @Nonnull List<BakedQuad> quads) {
        super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
        ConduitInOutRenderer.renderIO(bundle, conduit, component, layer, quads, DyeColor.RED, DyeColor.RED);
    }
}
