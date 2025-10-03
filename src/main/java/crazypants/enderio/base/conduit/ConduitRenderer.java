package crazypants.enderio.base.conduit;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.BlockRenderLayer;

import crazypants.enderio.base.conduit.ConduitClient.WithDefaultRendering;
import org.jetbrains.annotations.NotNull;

public interface ConduitRenderer {

    boolean isRendererForConduit(@NotNull Conduit conduit);

    void addBakedQuads(@NotNull TileEntitySpecialRenderer<?> conduitBundleRenderer, @NotNull ConduitBundle bundle,
                       @NotNull ConduitClient.WithDefaultRendering conduit, float brightness,
                       @NotNull BlockRenderLayer layer, @NotNull List<BakedQuad> quads);

    void renderDynamicEntity(@NotNull TileEntitySpecialRenderer<?> conduitBundleRenderer, @NotNull ConduitBundle bundle,
                             @NotNull ConduitClient.WithDefaultRendering conduit, double x, double y, double z,
                             float partialTick, float worldLight);

    default boolean isDynamic() {
        return false;
    }

    default boolean canRenderInLayer(@NotNull WithDefaultRendering conduit, @NotNull BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT;
    }

    @NotNull
    default BlockRenderLayer getCoreLayer() {
        return BlockRenderLayer.SOLID;
    }

}
