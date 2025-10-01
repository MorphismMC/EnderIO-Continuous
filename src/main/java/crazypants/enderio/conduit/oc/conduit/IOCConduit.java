package crazypants.enderio.conduit.oc.conduit;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.conduits.conduit.power.IPowerConduit;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;

@InterfaceList({ @Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputersapi|network"),
        @Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = "opencomputersapi|network") })
public interface IOCConduit extends ConduitClient, ConduitServer, Environment, SidedEnvironment {

    public static final String COLOR_CONTROLLER_ID = IPowerConduit.COLOR_CONTROLLER_ID;

    public abstract void setSignalColor(@Nonnull EnumFacing dir, @Nonnull DyeColor col);

    public abstract @Nonnull DyeColor getSignalColor(@Nonnull EnumFacing dir);
}
