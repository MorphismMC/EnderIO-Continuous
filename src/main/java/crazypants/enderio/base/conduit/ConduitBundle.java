package crazypants.enderio.base.conduit;

import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.facade.EnumFacadeType;
import crazypants.enderio.base.conduit.geom.CollidableComponent;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;
import crazypants.enderio.base.paint.IPaintable;
import org.jetbrains.annotations.NotNull;

public interface ConduitBundle extends IPaintable.IPaintableTileEntity, IHasConduitProbeData {

    // region In-world actions

    /**
     * @return The {@link TileEntity} of the conduit bundle.
     */
    @NotNull
    TileEntity getTileEntity();

    /**
     * The location of the conduit bundle.
     *
     * @return The {@link BlockPos} of the conduit bundle location.
     */
    @NotNull
    BlockPos getLocation();

    // NB: this has to be named differently to the TE method due to obf.
    @NotNull
    World getBundleworld();

    int getInternalRedstoneSignalForColor(@NotNull DyeColor color, @NotNull EnumFacing direction);

    boolean handleFacadeClick(@NotNull World world, @NotNull BlockPos placeAt, @NotNull EntityPlayer player,
                              @NotNull EnumFacing opposite,
                              @NotNull ItemStack stack, @NotNull EnumHand hand, float hitX, float hitY, float hitZ);

    // endregion

    // region Conduit contexts

    /**
     * Checks if the bundle contains the given conduit type.
     * 
     * @param type The container of the conduit to check for the type of.
     * @return     Returns {@code true} if the bundle has the given type of conduit, otherwise returns {@code false}.
     */
    boolean hasType(Class<? extends Conduit> type);

    /**
     * Gets a conduit of the given conduit type.
     * 
     * @param type The type of conduit to get.
     * @param <C>  The conduit type to return.
     * @return     The conduit of the given type
     */
    <C extends Conduit> C getConduit(Class<C> type);

    /**
     * Adds a conduit to the bundle
     * 
     * @param conduit The conduit to add.
     */
    boolean addConduit(ConduitServer conduit);

    /**
     * Removes a conduit from the bundle.
     * 
     * @param conduit The conduit to remove.
     */
    boolean removeConduit(Conduit conduit);

    /**
     * @return The collection of all the conduits in the bundle.
     */
    Collection<? extends Conduit> getConduits();

    Collection<ConduitServer> getServerConduits();

    @SideOnly(Side.CLIENT)
    Collection<ConduitClient> getClientConduits();

    // endregion

    // region Geometry

    @NotNull
    Offset getOffset(@NotNull Class<? extends Conduit> type, @NotNull EnumFacing direction);

    List<CollidableComponent> getCollidableComponents();

    List<CollidableComponent> getConnectors();

    // endregion

    // region Events

    void onNeighborBlockChange(@NotNull Block blockId);

    void onNeighborChange(@NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull BlockPos neighborPos);

    void onBlockRemoved();

    void dirty();

    // endregion

    // region Facade

    enum FacadeRenderState {
        NONE,
        FULL,
        WIRE_FRAME
    }

    @SideOnly(Side.CLIENT)
    FacadeRenderState getFacadeRenderedAs();

    @SideOnly(Side.CLIENT)
    void setFacadeRenderAs(FacadeRenderState state);

    int getLightOpacity();

    void setLightOpacityOverride(int opacity);

    boolean hasFacade();

    void setFacadeType(@NotNull EnumFacadeType type);

    @NotNull
    EnumFacadeType getFacadeType();

    // endregion

}
