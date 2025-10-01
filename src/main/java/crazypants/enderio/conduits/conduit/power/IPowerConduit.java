package crazypants.enderio.conduits.conduit.power;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.IExtractor;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.power.IPowerInterface;

public interface IPowerConduit extends IEnergyStorage, IExtractor, ConduitServer, ConduitClient {

    public static final @Nonnull String ICON_KEY = "blocks/power_conduit";
    public static final @Nonnull String ICON_CORE_KEY = "blocks/power_conduit_core";

    public static final String COLOR_CONTROLLER_ID = "ColorController";

    IPowerInterface getExternalPowerReceptor(@Nonnull EnumFacing direction);

    boolean getConnectionsDirty();

    void setEnergyStored(int energy);

    int getMaxEnergyRecieved(@Nonnull EnumFacing dir);

    int getMaxEnergyExtracted(@Nonnull EnumFacing dir);

    void setConnectionsDirty();
}
