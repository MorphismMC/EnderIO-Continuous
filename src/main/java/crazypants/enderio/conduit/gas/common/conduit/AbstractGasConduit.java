package crazypants.enderio.conduit.gas.common.conduit;

import java.util.EnumMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.ConduitClient;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitBundle;
import crazypants.enderio.base.conduit.GuiExternalConnection;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.conduits.conduit.AbstractConduit;
import crazypants.enderio.util.EnumReader;
import crazypants.enderio.conduit.gas.client.GasSettings;
import crazypants.enderio.conduit.gas.common.utils.GasWrapper;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;

public abstract class AbstractGasConduit extends AbstractConduit implements IGasConduit {

    protected final EnumMap<EnumFacing, RedstoneControlMode> extractionModes = new EnumMap<>(EnumFacing.class);
    protected final EnumMap<EnumFacing, DyeColor> extractionColors = new EnumMap<>(EnumFacing.class);

    public static IGasHandler getExternalGasHandler(@Nonnull IBlockAccess world, @Nonnull BlockPos pos,
                                                    @Nonnull EnumFacing side) {
        return world.getTileEntity(pos) instanceof ConduitBundle ? null : GasWrapper.getGasHandler(world, pos, side);
    }

    public IGasHandler getExternalHandler(@Nonnull EnumFacing direction) {
        return getExternalGasHandler(getBundle().getBundleworld(), getBundle().getLocation().offset(direction),
                direction.getOpposite());
    }

    @Override
    public boolean canConnectToExternal(@Nonnull EnumFacing direction, boolean ignoreDisabled) {
        return getExternalHandler(direction) != null;
    }

    @Override
    @Nonnull
    public Class<? extends Conduit> getBaseConduitType() {
        return IGasConduit.class;
    }

    @Override
    public void setExtractionRedstoneMode(@Nonnull RedstoneControlMode mode, @Nonnull EnumFacing direction) {
        extractionModes.put(direction, mode);
    }

    @Override
    @Nonnull
    public RedstoneControlMode getExtractionRedstoneMode(@Nonnull EnumFacing direction) {
        RedstoneControlMode res = extractionModes.get(direction);
        return res == null ? RedstoneControlMode.NEVER : res;
    }

    @Override
    public void setExtractionSignalColor(@Nonnull EnumFacing direction, @Nonnull DyeColor color) {
        extractionColors.put(direction, color);
    }

    @Override
    @Nonnull
    public DyeColor getExtractionSignalColor(@Nonnull EnumFacing direction) {
        DyeColor result = extractionColors.get(direction);
        return result == null ? DyeColor.RED : result;
    }

    @Override
    public boolean canOutputToDir(@Nonnull EnumFacing dir) {
        return canInputToDir(dir) && (conduitConnections.contains(dir) || externalConnections.contains(dir));
    }

    protected boolean autoExtractForDir(@Nonnull EnumFacing dir) {
        if (!canExtractFromDir(dir)) {
            return false;
        }
        RedstoneControlMode mode = getExtractionRedstoneMode(dir);
        return ConduitUtil.isRedstoneControlModeMet(this, mode, getExtractionSignalColor(dir), dir);
    }

    @Override
    public boolean canExtractFromDir(@Nonnull EnumFacing dir) {
        return getConnectionMode(dir).acceptsInput();
    }

    @Override
    public boolean canInputToDir(@Nonnull EnumFacing dir) {
        return getConnectionMode(dir).acceptsOutput() && !autoExtractForDir(dir);
    }

    protected boolean hasExtractableMode() {
        return supportsConnectionMode(ConnectionMode.INPUT) || supportsConnectionMode(ConnectionMode.IN_OUT);
    }

    @Override
    protected void readTypeSettings(@Nonnull EnumFacing direction, @Nonnull NBTTagCompound data) {
        setExtractionSignalColor(direction, EnumReader.get(DyeColor.class, data.getShort("extractionSignalColor")));
        setExtractionRedstoneMode(RedstoneControlMode.fromOrdinal(data.getShort("extractionRedstoneMode")), direction);
    }

    @Override
    protected void writeTypeSettingsToNBT(@Nonnull EnumFacing dir, @Nonnull NBTTagCompound data) {
        data.setShort("extractionSignalColor", (short) getExtractionSignalColor(dir).ordinal());
        data.setShort("extractionRedstoneMode", (short) getExtractionRedstoneMode(dir).ordinal());
    }

    @Override
    public void writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);

        for (Entry<EnumFacing, RedstoneControlMode> entry : extractionModes.entrySet()) {
            if (entry.getValue() != null) {
                data.setShort("extRM." + entry.getKey().name(), (short) entry.getValue().ordinal());
            }
        }

        for (Entry<EnumFacing, DyeColor> entry : extractionColors.entrySet()) {
            if (entry.getValue() != null) {
                data.setShort("extSC." + entry.getKey().name(), (short) entry.getValue().ordinal());
            }
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);

        for (EnumFacing dir : EnumFacing.VALUES) {
            String key = "extRM." + dir.name();
            if (data.hasKey(key)) {
                short ord = data.getShort(key);
                if (ord >= 0 && ord < RedstoneControlMode.values().length) {
                    extractionModes.put(dir, EnumReader.get(RedstoneControlMode.class, ord));
                }
            }
            key = "extSC." + dir.name();
            if (data.hasKey(key)) {
                short ord = data.getShort(key);
                if (ord >= 0 && ord < DyeColor.values().length) {
                    extractionColors.put(dir, EnumReader.get(DyeColor.class, ord));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public ITabPanel createGuiPanel(@Nonnull GuiExternalConnection gui, @Nonnull ConduitClient conduit) {
        return new GasSettings(gui, conduit);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean updateGuiPanel(@Nonnull ITabPanel panel) {
        return panel instanceof GasSettings && ((GasSettings) panel).updateConduit(this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getGuiPanelTabOrder() {
        return 1;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            if (side != null && containsExternalConnection(side)) {
                ConnectionMode mode = getConnectionMode(side);
                return mode.acceptsInput() || mode.acceptsOutput();
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side) {
        return hasCapability(capability, side) ? (T) getGasDir(side) : null;
    }

    @Override
    @Nullable
    public IGasHandler getGasDir(@Nullable EnumFacing dir) {
        return dir != null ? new ConnectionGasSide(dir) : null;
    }

    /**
     * Inner class for holding the direction of capabilities.
     */
    protected class ConnectionGasSide implements IGasHandler, ICapabilityProvider {

        @Nonnull
        protected EnumFacing side;

        public ConnectionGasSide(@Nonnull EnumFacing side) {
            this.side = side;
        }

        @Override
        public int receiveGas(EnumFacing facing, GasStack resource, boolean doFill) {
            if (canReceiveGas(facing, resource.getGas())) {
                return AbstractGasConduit.this.receiveGas(facing, resource, doFill);
            }
            return 0;
        }

        @Override
        public GasStack drawGas(EnumFacing facing, int maxDrain, boolean doDrain) {
            if (canDrawGas(facing, null)) {
                return AbstractGasConduit.this.drawGas(facing, maxDrain, doDrain);
            }
            return null;
        }

        @Override
        public boolean canReceiveGas(EnumFacing facing, Gas gas) {
            if (side.equals(facing) && getConnectionMode(facing).acceptsInput()) {
                return ConduitUtil.isRedstoneControlModeMet(AbstractGasConduit.this, getExtractionRedstoneMode(facing),
                        getExtractionSignalColor(facing), facing);
            }
            return false;
        }

        @Override
        public boolean canDrawGas(EnumFacing facing, Gas gas) {
            if (side.equals(facing) && getConnectionMode(facing).acceptsOutput()) {
                return ConduitUtil.isRedstoneControlModeMet(AbstractGasConduit.this, getExtractionRedstoneMode(facing),
                        getExtractionSignalColor(facing), facing);
            }
            return false;
        }

        @Override
        @Nonnull
        public GasTankInfo[] getTankInfo() {
            return AbstractGasConduit.this.getTankInfo();
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
                if (!side.equals(facing)) {
                    return false;
                }
                ConnectionMode connectionMode = getConnectionMode(facing);
                return connectionMode.acceptsOutput() || connectionMode.acceptsInput();
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? Capabilities.GAS_HANDLER_CAPABILITY.cast(this) : null;
        }
    }
}
