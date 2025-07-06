package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.render.util.LimitedTextureAtlasSprite;

public interface IConduitTexture {

    @Nonnull
    TextureSupplier getTexture();

    @Nonnull
    Vector4f getUv();

    @SideOnly(Side.CLIENT)
    default @Nonnull TextureAtlasSprite getSprite() {
        return getTexture().get(TextureAtlasSprite.class);
    }

    @SideOnly(Side.CLIENT)
    default @Nonnull TextureAtlasSprite getCroppedSprite() {
        return new LimitedTextureAtlasSprite(getSprite(), getUv());
    }
}
