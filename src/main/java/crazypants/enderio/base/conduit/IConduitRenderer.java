package crazypants.enderio.base.conduit;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.BlockRenderLayer;

import crazypants.enderio.base.conduit.ConduitClient.WithDefaultRendering;

public interface IConduitRenderer {

    boolean isRendererForConduit(@Nonnull Conduit conduit);

    void addBakedQuads(@Nonnull TileEntitySpecialRenderer<?> conduitBundleRenderer, @Nonnull ConduitBundle bundle,
                       @Nonnull ConduitClient.WithDefaultRendering conduit, float brightness,
                       @Nonnull BlockRenderLayer layer, @Nonnull List<BakedQuad> quads);

    // -----------------------
    // DYNAMIC
    // -----------------------

    void renderDynamicEntity(@Nonnull TileEntitySpecialRenderer<?> conduitBundleRenderer, @Nonnull ConduitBundle te,
                             @Nonnull ConduitClient.WithDefaultRendering conduit, double x, double y, double z,
                             float partialTick, float worldLight);

    default boolean isDynamic() {
        return false;
    }

    default boolean canRenderInLayer(@Nonnull WithDefaultRendering con, @Nonnull BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT;
    }

    default @Nonnull BlockRenderLayer getCoreLayer() {
        return BlockRenderLayer.SOLID;
    }
}
