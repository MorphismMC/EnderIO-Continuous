package crazypants.enderio.base.conduit;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.conduit.geom.CollidableComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Stuff that would be on the class, not the object if there were interfaces for classes...
public interface ConduitClient extends Conduit {

    // region Gui contexts

    /**
     * Creates the gui for the conduit within the external connection gui
     *
     * @param gui     The gui to construct the panel inside of.
     * @param conduit The conduit that the gui references.
     * @return        The panel for the conduit's information on the gui.
     */
    @SideOnly(Side.CLIENT)
    @NotNull
    ITabPanel createGuiPanel(@NotNull GuiExternalConnection gui, @NotNull ConduitClient conduit);

    /**
     * Update the gui for updated client conduits.
     * <p>
     * Note that this will be called on all conduits, so you need to test if the panel you get is yours.
     * 
     * @param panel The panel to be updated
     * @return      Returns {@code false} if the panel doesn't belong to you, returns {@code true} if the panel was updated.
     */
    @SideOnly(Side.CLIENT)
    boolean updateGuiPanel(@NotNull ITabPanel panel);

    /**
     * Determines the order the panels are shown in the conduit gui tabs.
     *
     * @return The integer position of the panel in order (top -> bottom).
     */
    @SideOnly(Side.CLIENT)
    int getGuiPanelTabOrder();

    // endregion

    // region Actions

    /**
     * @return Returns {@code true} if the conduit is currently in use.
     */
    boolean isActive();

    /**
     * @see ICapabilityProvider#hasCapability(Capability, EnumFacing)
     */
    default boolean hasClientCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    /**
     * @see ICapabilityProvider#getCapability(Capability, EnumFacing)
     */
    default @Nullable <T> T getClientCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }

    // endregion

    // region Renderer

    /**
     * @return Returns {@code true} if the conduit should render in an error state because the server could not form a
     *         network. The only reason for that at the moment would be that the network would need to extend into
     *         unloaded blocks.
     */
    default boolean renderError() {
        return false;
    }

    interface WithDefaultRendering extends ConduitClient {

        @SideOnly(Side.CLIENT)
        @NotNull
        ConduitTexture getTextureForState(@NotNull CollidableComponent component);

        @SideOnly(Side.CLIENT)
        @Nullable
        ConduitTexture getTransmitionTextureForState(@NotNull CollidableComponent component);

        @SideOnly(Side.CLIENT)
        @Nullable
        Vector4f getTransmitionTextureColorForState(@NotNull CollidableComponent component);

        @SideOnly(Side.CLIENT)
        float getTransmitionGeometryScale();

        @SideOnly(Side.CLIENT)
        float getSelfIlluminationForState(@NotNull CollidableComponent component);

        /**
         * Should the texture of the conduit connectors be mirrored around the conduit node?
         */
        @SideOnly(Side.CLIENT)
        boolean shouldMirrorTexture();

    }

    // endregion

}
