package crazypants.enderio.conduit.oc.conduit;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.model.ModelLoader.White;

import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.ConduitTexture;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.conduits.render.BakedQuadBuilder;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;

public class OCConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(@Nonnull Conduit conduit) {
        return conduit instanceof IOCConduit;
    }

    /*
     * TODO: HL 2020-03-08: I just re-enabled this render because it seems its registration got lost. I have no setup to
     * test it, so if there's a weird rendering
     * issue...
     */

    @Override
    protected void addConduitQuads(@Nonnull ConduitBundle bundle, @Nonnull ConduitClient conduit,
                                   @Nonnull ConduitTexture tex,
                                   @Nonnull CollidableComponent component, float selfIllum, BlockRenderLayer layer,
                                   @Nonnull List<BakedQuad> quads) {
        if (IOCConduit.COLOR_CONTROLLER_ID.equals(component.data)) {
            if (conduit.containsExternalConnection(component.getDirection())) {
                int c = ((IOCConduit) conduit).getSignalColor(component.getDirection()).getColor();
                BakedQuadBuilder.addBakedQuads(quads, component.bound, White.INSTANCE, ColorUtil.toFloat4(c));
            }
        } else {
            super.addConduitQuads(bundle, conduit, tex, component, selfIllum, layer, quads);
        }
    }
}
