package crazypants.enderio.base.conduit;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.render.util.LimitedTextureAtlasSprite;
import org.jetbrains.annotations.NotNull;

public interface ConduitTexture {

    @NotNull
    TextureSupplier getTexture();

    @NotNull
    Vector4f getUv();

    @NotNull
    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getSprite() {
        return getTexture().get(TextureAtlasSprite.class);
    }

    @NotNull
    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getCroppedSprite() {
        return new LimitedTextureAtlasSprite(getSprite(), getUv());
    }

}
