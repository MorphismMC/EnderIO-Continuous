package crazypants.enderio.conduits.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ColorButton;

import crazypants.enderio.base.conduit.ConduitExtractor;
import crazypants.enderio.base.conduit.GuiExternalConnection;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.network.PacketExtractMode;

public class ConduitRedstoneModeControlable implements IRedstoneModeControlable {

    private final ConduitExtractor con;
    private final GuiExternalConnection gui;
    private final ColorButton colorB;

    public ConduitRedstoneModeControlable(@Nonnull ConduitExtractor con, @Nonnull GuiExternalConnection gui,
                                          @Nonnull ColorButton colorB) {
        this.con = con;
        this.gui = gui;
        this.colorB = colorB;
    }

    @Override
    public void setRedstoneControlMode(@Nonnull RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        con.setExtractionRedstoneMode(mode, gui.getDir());
        colorB.onGuiInit();
        colorB.setColorIndex(con.getExtractionSignalColor(gui.getDir()).ordinal());
        if (mode == RedstoneControlMode.OFF || mode == RedstoneControlMode.ON) {
            colorB.setIsVisible(true);
        } else {
            colorB.setIsVisible(false);
        }
        if (curMode != mode) {
            PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(con, gui.getDir()));
        }
    }

    @Override
    @Nonnull
    public RedstoneControlMode getRedstoneControlMode() {
        return con.getExtractionRedstoneMode(gui.getDir());
    }

    @Override
    public boolean getRedstoneControlStatus() {
        return false;
    }
}
