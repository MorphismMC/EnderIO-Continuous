package crazypants.enderio.conduits.conduit;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.client.model.ModelLoader;

import com.google.common.collect.Maps;

import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import org.jetbrains.annotations.NotNull;

public class ConduitBundleStateMapper extends StateMapperBase {

    public static void create() {
        ConduitBundleStateMapper mapper = new ConduitBundleStateMapper();
        ModelLoader.setCustomStateMapper(ConduitRegistry.getConduitModObjectNN().getBlockNN(), mapper);
    }

    @NotNull
    @Override
    protected ModelResourceLocation getModelResourceLocation(@NotNull IBlockState state) {
        Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
        map.remove(BlockConduitBundle.OPAQUE);
        return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()), getPropertyString(map));
    }

}
