package crazypants.enderio.base.conduit.geom;

import static com.enderio.core.common.util.ForgeDirectionOffsets.offsetScaled;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.client.render.VertexRotation;
import com.enderio.core.client.render.VertexTransformComposite;
import com.enderio.core.client.render.VertexTranslation;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.vecmath.VecmathUtil;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = EnderIO.MODID)
public final class ConduitGeometryUtil {

    @NotNull
    private static ConduitGeometryUtil INSTANCE = new ConduitGeometryUtil(3 / 16f);

    @NotNull
    private final Map<EnumFacing, BoundingBox[]> EXTERNAL_CONNECTOR_BOUNDS = new EnumMap<>(EnumFacing.class);
    @NotNull
    private final Map<GeometryKey, BoundingBox> CACHED_BOUNDS = new HashMap<>();
    @NotNull
    private final EnumMap<EnumFacing, List<Vertex>> IO_RING_VERTS = new EnumMap<>(EnumFacing.class);

    private final float width;
    @Getter
    private final float height;

    private final float halfWidth;
    private final float halfHeight;

    // All values are for a single conduit core.
    @NotNull
    private final BoundingBox coreBounds;

    private final float CONNECTOR_DEPTH = 0.05f;

    private ConduitGeometryUtil(float size) {
        this.width = size;
        this.height = size;
        this.halfWidth = width / 2;
        this.halfHeight = height / 2;

        final Vector3d coreMin = new Vector3d(0.5f - halfWidth, 0.5 - halfHeight, 0.5 - halfWidth);
        final Vector3d coreMax = new Vector3d(coreMin.x + width, coreMin.y + height, coreMin.z + width);
        this.coreBounds = new BoundingBox(coreMin, coreMax);

        float connectorWidth = Math.min((2 / 16f) + (size * 3), 1);
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
            EnumFacing dir = itr.next();
            EXTERNAL_CONNECTOR_BOUNDS.put(dir, createExternalConnector(dir, CONNECTOR_DEPTH, connectorWidth));
        }

        createIORingVerts();
    }

    @NotNull
    public static ConduitGeometryUtil getINSTANCE() {
        return INSTANCE;
    }

    @SubscribeEvent
    public static void preInit(EnderIOLifecycleEvent.Config.Post event) {
        INSTANCE = new ConduitGeometryUtil((1 / 16f) * PersonalConfig.conduitPixels.get());
    }

    @NotNull
    private BoundingBox[] createExternalConnector(@NotNull EnumFacing direction,
                                                  float connectorDepth,
                                                  float connectorWidth) {
        BoundingBox[] res = new BoundingBox[2];

        float cMin = 0.5f - connectorWidth / 2;
        float cMax = 0.5f + connectorWidth / 2;
        float dMin = 1 - connectorDepth / 2;
        float dMax = 1;

        res[0] = createConnectorComponent(direction, cMin, cMax, dMin, dMax);

        cMin = 0.5f - connectorWidth / 3;
        cMax = 0.5f + connectorWidth / 3;
        dMin = 1 - connectorDepth;
        dMax = 1 - connectorDepth / 2;

        res[1] = createConnectorComponent(direction, cMin, cMax, dMin, dMax);

        return res;
    }

    @NotNull
    private BoundingBox createConnectorComponent(@NotNull EnumFacing direction,
                                                 float cornerMin,
                                                 float cornerMax,
                                                 float depthMin,
                                                 float depthMax) {
        float minX = (1 - Math.abs(direction.getXOffset())) * cornerMin + direction.getXOffset() * depthMin;
        float minY = (1 - Math.abs(direction.getYOffset())) * cornerMin + direction.getYOffset() * depthMin;
        float minZ = (1 - Math.abs(direction.getZOffset())) * cornerMin + direction.getZOffset() * depthMin;

        float maxX = (1 - Math.abs(direction.getXOffset())) * cornerMax + (direction.getXOffset() * depthMax);
        float maxY = (1 - Math.abs(direction.getYOffset())) * cornerMax + (direction.getYOffset() * depthMax);
        float maxZ = (1 - Math.abs(direction.getZOffset())) * cornerMax + (direction.getZOffset() * depthMax);

        minX = fix(minX);
        minY = fix(minY);
        minZ = fix(minZ);
        maxX = fix(maxX);
        maxY = fix(maxY);
        maxZ = fix(maxZ);

        BoundingBox bb = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        bb = bb.fixMinMax();

        return bb;
    }

    private static float fix(float val) {
        return val < 0 ? 1 + val : val;
    }

    @NotNull
    public BoundingBox getExternalConnectorBoundingBox(@NotNull EnumFacing direction) {
        return getExternalConnectorBoundingBoxes(direction)[0];
    }

    @NotNull
    public BoundingBox[] getExternalConnectorBoundingBoxes(@NotNull EnumFacing direction) {
        return EXTERNAL_CONNECTOR_BOUNDS.get(direction);
    }

    @NotNull
    public BoundingBox getBoundingBox(@NotNull Class<? extends Conduit> type,
                                      EnumFacing direction,
                                      @NotNull Offset offset) {
        GeometryKey key = new GeometryKey(direction, offset, type);
        BoundingBox result = CACHED_BOUNDS.get(key);
        if (result == null) {
            result = createConduitBounds(type, key);
            CACHED_BOUNDS.put(key, result);
        }
        return result;
    }

    @NotNull
    public Vector3d getTranslation(EnumFacing direction, @NotNull Offset offset) {
        Vector3d result = new Vector3d(offset.xOffset, offset.yOffset, offset.zOffset);
        result.scale(width);
        return result;
    }

    @NotNull
    public BoundingBox createBoundsForConnectionController(@NotNull EnumFacing direction, @NotNull Offset offset) {
        Vector3d nonUniformScale = ForgeDirectionOffsets.forDirCopy(direction);
        nonUniformScale.scale(0.5);

        nonUniformScale.x = 0.8 * (1 - Math.abs(nonUniformScale.x));
        nonUniformScale.y = 0.8 * (1 - Math.abs(nonUniformScale.y));
        nonUniformScale.z = 0.8 * (1 - Math.abs(nonUniformScale.z));

        BoundingBox bb = coreBounds;
        bb = bb.scale(nonUniformScale.x, nonUniformScale.y, nonUniformScale.z);

        double offsetFromEnd = Math.min(bb.sizeX(), bb.sizeY());
        offsetFromEnd = Math.min(offsetFromEnd, bb.sizeZ());
        offsetFromEnd = Math.max(offsetFromEnd, 0.075);
        double transMag = 0.5 - (offsetFromEnd * 1.2);

        Vector3d trans = ForgeDirectionOffsets.forDirCopy(direction);
        trans.scale(transMag);
        bb = bb.translate(trans);
        bb = bb.translate(getTranslation(direction, offset));
        return bb;
    }

    @NotNull
    private BoundingBox createConduitBounds(@NotNull Class<? extends Conduit> type,
                                            @NotNull GeometryKey key) {
        return createConduitBounds(type, key.direction, key.offset);
    }

    @NotNull
    private BoundingBox createConduitBounds(Class<? extends Conduit> type,
                                            EnumFacing direction,
                                            @NotNull Offset offset) {
        BoundingBox bb = coreBounds;

        Vector3d min = bb.getMin();
        Vector3d max = bb.getMax();

        if (direction != null) {
            switch (direction) {
                case WEST:
                    min.x = 0;
                    max.x = bb.minX;
                    break;
                case EAST:
                    min.x = bb.maxX;
                    max.x = 1;
                    break;
                case DOWN:
                    min.y = 0;
                    max.y = bb.minY;
                    break;
                case UP:
                    min.y = bb.maxY;
                    max.y = 1;
                    break;
                case NORTH:
                    min.z = 0;
                    max.z = bb.minZ;
                    break;
                case SOUTH:
                    min.z = bb.maxZ;
                    max.z = 1;
                    break;
                default:
                    break;
            }
        }

        Vector3d trans = getTranslation(direction, offset);
        min.add(trans);
        max.add(trans);
        bb = new BoundingBox(VecmathUtil.clamp(min, 0, 1), VecmathUtil.clamp(max, 0, 1));
        return bb;
    }

    private void createIORingVerts() {
        float scale = 0.9f;
        BoundingBox refBB = coreBounds;
        refBB = refBB.scale(scale, scale, scale);
        refBB = refBB.scale(scale, 1, 1);

        double offset = (halfWidth * scale * scale) + CONNECTOR_DEPTH;

        EnumFacing dir;
        Vector3d trans;

        VertexRotation vrot = new VertexRotation(Math.PI / 2, new Vector3d(0, 1, 0), new Vector3d(0.5, 0.5, 0.5));
        VertexTranslation vtrans = new VertexTranslation(0, 0, 0);
        VertexTransformComposite xform = new VertexTransformComposite(vrot, vtrans);

        dir = EnumFacing.SOUTH;
        trans = offsetScaled(dir, 0.5);
        trans.sub(offsetScaled(dir, offset));
        vtrans.set(trans);
        IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

        dir = EnumFacing.NORTH;
        vrot.setAngle(Math.PI + Math.PI / 2);
        trans = offsetScaled(dir, 0.5);
        trans.sub(offsetScaled(dir, offset));
        vtrans.set(trans);
        IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

        dir = EnumFacing.EAST;
        vrot.setAngle(Math.PI);
        trans = offsetScaled(dir, 0.5);
        trans.sub(offsetScaled(dir, offset));
        vtrans.set(trans);
        IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

        dir = EnumFacing.WEST;
        vrot.setAngle(0);
        trans = offsetScaled(dir, 0.5);
        trans.sub(offsetScaled(dir, offset));
        vtrans.set(trans);
        IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

        vrot.setAxis(new Vector3d(0, 0, 1));

        dir = EnumFacing.UP;
        vrot.setAngle(-Math.PI / 2);
        trans = offsetScaled(dir, 0.5);
        trans.sub(offsetScaled(dir, offset));
        vtrans.set(trans);
        IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));

        dir = EnumFacing.DOWN;
        vrot.setAngle(Math.PI / 2);
        trans = offsetScaled(dir, 0.5);
        trans.sub(offsetScaled(dir, offset));
        vtrans.set(trans);
        IO_RING_VERTS.put(dir, createVerticesForDir(refBB, xform));
    }

    @Nullable
    private List<Vertex> createVerticesForDir(BoundingBox refBB, VertexTransform xform) {
        List<Vertex> result = new ArrayList<>(24);
        for (EnumFacing face : EnumFacing.VALUES) {
            if (face != null) {
                result.addAll(refBB.getCornersWithUvForFace(face));
            }
        }
        for (Vertex v : result) {
            xform.apply(v.xyz);
            Vector3f normal = v.normal;
            if (normal != null) {
                xform.applyToNormal(normal);
            }

        }
        return result;
    }

    @SideOnly(Side.CLIENT)
    public void addModeConnectorQuads(EnumFacing direction,
                                      @NotNull Offset offset,
                                      @NotNull TextureAtlasSprite tex,
                                      Vector4f color, @NotNull List<BakedQuad> quads) {
        List<Vertex> verts = IO_RING_VERTS.get(direction);
        if (verts == null) {
            return;
        }
        Vector3d trans = getTranslation(direction, offset);
        List<Vertex> xFormed = new ArrayList<>(verts.size());
        for (Vertex v : verts) {
            Vertex xf = new Vertex(v);
            xf.xyz.add(trans);
            xFormed.add(xf);
        }
        RenderUtil.addBakedQuads(quads, xFormed, tex, color);
    }

    // IO rings END
}
