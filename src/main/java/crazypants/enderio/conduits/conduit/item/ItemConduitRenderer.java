package crazypants.enderio.conduits.conduit.item;

import java.util.List;

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
import org.jetbrains.annotations.NotNull;

public class ItemConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(@NotNull Conduit conduit) {
        return conduit instanceof ItemConduit;
    }

    @Override
    protected void addConduitQuads(@NotNull ConduitBundle bundle,
                                   @NotNull ConduitClient conduit,
                                   @NotNull ConduitTexture texture,
                                   @NotNull CollidableComponent component,
                                   float selfIllum,
                                   BlockRenderLayer layer,
                                   @NotNull List<BakedQuad> quads) {
        super.addConduitQuads(bundle, conduit, texture, component, selfIllum, layer, quads);
        ItemConduit pc = (ItemConduit) conduit;
        EnumFacing dir = component.direction();
        ConduitInOutRenderer.renderIO(bundle, conduit, component, layer, quads, pc.getInputColor(dir),
                pc.getOutputColor(dir));
    }

}
