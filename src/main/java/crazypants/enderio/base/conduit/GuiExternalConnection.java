package crazypants.enderio.base.conduit;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumFacing;

import com.enderio.core.api.client.gui.IGuiScreen;

public interface GuiExternalConnection extends IGuiScreen {

    /**
     * Gets the direction of the conduit's connection
     */
    @Nonnull
    EnumFacing getDir();

    /**
     * Gets the conduit container
     */
    ExternalConnectionContainer getContainer();

    // The following are handled by the Gui class already but are needed here for abstraction to base
    int getGuiTop();

    FontRenderer getFontRenderer();

    void setGuiID(int id);

    int getGuiID();
}
