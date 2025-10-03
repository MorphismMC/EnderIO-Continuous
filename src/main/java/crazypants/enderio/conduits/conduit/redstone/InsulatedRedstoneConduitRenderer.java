package crazypants.enderio.conduits.conduit.redstone;

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

public class InsulatedRedstoneConduitRenderer extends DefaultConduitRenderer {

    @Override
    public boolean isRendererForConduit(@Nonnull Conduit conduit) {
        return conduit instanceof IRedstoneConduit;
    }

    @Override
    protected void addConduitQuads(@Nonnull ConduitBundle bundle, @Nonnull ConduitClient conduit,
                                   @Nonnull ConduitTexture texture,
                                   @Nonnull CollidableComponent component, float brightness, BlockRenderLayer layer,
                                   @Nonnull List<BakedQuad> quads) {
        super.addConduitQuads(bundle, conduit, texture, component, brightness, layer, quads);

        IRedstoneConduit pc = (IRedstoneConduit) conduit;
        EnumFacing dir = component.direction();
        ConduitInOutRenderer.renderIO(bundle, conduit, component, layer, quads, pc.getInputSignalColor(dir),
                pc.getOutputSignalColor(dir));
    }
}
