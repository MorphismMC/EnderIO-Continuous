package crazypants.enderio.conduit.gas.common.conduit.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

import crazypants.enderio.base.conduit.ConduitUtil;
import crazypants.enderio.base.conduit.UnloadedBlockException;
import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.conduit.gas.common.conduit.AbstractGasTankConduit;
import crazypants.enderio.conduit.gas.common.conduit.AbstractGasTankConduitNetwork;
import crazypants.enderio.conduit.gas.common.conduit.ConduitGasTank;
import crazypants.enderio.conduit.gas.common.conduit.GasConduit;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;

public class GasConduitNetwork extends AbstractGasTankConduitNetwork<GasConduitImpl> {

    public GasConduitNetwork() {
        super(GasConduitImpl.class);
    }

    private int ticksEmpty = 0;
    private int maxFlowsPerTick = 10;
    private int lastFlowIndex = 0;
    private int lastPushToken = 0;

    private boolean inputLocked = false;

    public boolean lockNetworkForFill() {
        if (inputLocked) {
            return false;
        }
        inputLocked = true;
        return true;
    }

    public void unlockNetworkFromFill() {
        inputLocked = false;
    }

    @Override
    public void tickEnd(ServerTickEvent event, @Nullable Profiler profiler) {
        List<GasConduitImpl> cons = getConduits();
        if (cons.isEmpty()) {
            return;
        }

        if (isEmpty()) {
            if (!gasTypeLocked && gasType != null) {
                ticksEmpty++;
                if (ticksEmpty > 40) {
                    setGasType(null);
                    ticksEmpty = 0;
                }
            }
            return;
        }

        ticksEmpty = 0;
        long curTime = cons.get(0).getBundle().getTileEntity().getWorld().getTotalWorldTime();

        if (gasType != null && gasType.getGas() != null && !isEmpty()) {
            if (curTime % 2 == 0) {
                Prof.start(profiler, "flow");
                doFlow();
                Prof.stop(profiler);
            }
        }
    }

    void addedFromExternal(int res) {}

    void outputedToExternal(int filled) {}

    int getNextPushToken() {
        return ++lastPushToken;
    }

    private boolean doFlow() {
        int pushToken = getNextPushToken();
        List<FlowAction> actions = new ArrayList<>();
        for (int i = 0; i < Math.min(maxFlowsPerTick, getConduits().size()); i++) {
            if (lastFlowIndex >= getConduits().size()) {
                lastFlowIndex = 0;
            }
            flowFrom(getConduits().get(lastFlowIndex), actions, pushToken);
            ++lastFlowIndex;

        }
        actions.forEach(FlowAction::apply);
        boolean result = !actions.isEmpty();

        // Flush any tanks with a tiny bit left
        List<GasConduitImpl> toEmpty = new ArrayList<>();
        for (GasConduitImpl con : getConduits()) {
            if (con != null && con.getTank().getStored() < 10) {
                toEmpty.add(con);
            } else {
                // some of the conduits have gas left in them so don't do the final drawGas yet
                return result;
            }
        }
        if (toEmpty.isEmpty()) {
            return result;
        }

        List<LocatedGasHandler> externals = new ArrayList<>();
        for (AbstractGasTankConduit con : getConduits()) {
            Set<EnumFacing> extCons = con.getExternalConnections();
            for (EnumFacing dir : extCons) {
                if (con.canOutputToDir(dir)) {
                    IGasHandler externalTank = con.getExternalHandler(dir);
                    if (externalTank != null) {
                        externals.add(new LocatedGasHandler(externalTank, con.getBundle().getLocation().offset(dir),
                                dir.getOpposite()));
                    }
                }
            }
        }
        if (externals.isEmpty()) {
            return result;
        }
        toEmpty.forEach(con -> drainConduitToNearestExternal(con, externals));
        return result;
    }

    @Override
    public void setGasTypeLocked(boolean gasTypeLocked) {
        super.setGasTypeLocked(gasTypeLocked);
        if (!gasTypeLocked && isEmpty()) {
            setGasType(null);
        }
    }

    private boolean isEmpty() {
        return getConduits().stream().noneMatch(con -> con.getTank().getStored() > 0);
    }

    private void drainConduitToNearestExternal(@Nonnull GasConduitImpl con, List<LocatedGasHandler> externals) {
        BlockPos conPos = con.getBundle().getLocation();
        GasStack toDrain = con.getTank().getGas();
        if (toDrain == null) {
            return;
        }
        int closestDistance = Integer.MAX_VALUE;
        LocatedGasHandler closestTank = null;
        for (LocatedGasHandler lh : externals) {
            int distance = (int) lh.pos.distanceSq(conPos);
            if (distance < closestDistance && con.canOutputToDir(lh.dir.getOpposite())) {
                int couldFill = lh.tank.receiveGas(lh.dir, toDrain.copy(), false);
                if (couldFill > 0) {
                    closestTank = lh;
                    closestDistance = distance;
                }
            }
        }

        if (closestTank != null) {
            int filled = closestTank.tank.receiveGas(closestTank.dir, toDrain.copy(), true);
            con.getTank().addAmount(-filled);
        }
    }

    private void flowFrom(@Nonnull GasConduitImpl con, List<FlowAction> actions, int pushPoken) {
        ConduitGasTank tank = con.getTank();
        int totalAmount = tank.getStored();
        if (totalAmount <= 0) {
            return;
        }

        GasStack available = tank.getGas();
        if (available == null) {
            return;
        }
        int totalRequested = 0;
        int numRequests = 0;
        // Then to external connections
        for (EnumFacing dir : con.getExternalConnections()) {
            if (con.canOutputToDir(dir)) {
                IGasHandler extCon = con.getExternalHandler(dir);
                if (extCon != null && extCon.canReceiveGas(dir.getOpposite(), available.getGas())) {
                    int amount = extCon.receiveGas(dir.getOpposite(), available.copy(), false);
                    if (amount > 0) {
                        totalRequested += amount;
                        numRequests++;
                    }
                }
            }
        }

        int maxFlowVolume = 20;

        if (numRequests > 0) {
            int amountPerRequest = Math.min(totalAmount, totalRequested) / numRequests;
            amountPerRequest = Math.min(maxFlowVolume, amountPerRequest);

            GasStack requestSource = available.copy();
            requestSource.amount = amountPerRequest;
            for (EnumFacing dir : con.getExternalConnections()) {
                if (con.canOutputToDir(dir)) {
                    IGasHandler extCon = con.getExternalHandler(dir);
                    if (extCon != null && extCon.canReceiveGas(dir.getOpposite(), requestSource.getGas())) {
                        int amount = extCon.receiveGas(dir.getOpposite(), requestSource.copy(), true);
                        if (amount > 0) {
                            outputedToExternal(amount);
                            tank.addAmount(-amount);
                        }
                    }
                }
            }
        }

        totalAmount = tank.getStored();
        if (totalAmount <= 0) {
            return;
        }
        int totalCapacity = tank.getMaxGas();

        try {
            BlockPos pos = con.getBundle().getLocation();
            Collection<GasConduit> connections = ConduitUtil.getConnectedConduits(con.getBundle().getTileEntity().getWorld(), pos, GasConduit.class);
            for (GasConduit n : connections) {
                GasConduitImpl neighbour = (GasConduitImpl) n;
                if (canFlowTo(con, neighbour)) { // can only flow within same network
                    totalAmount += neighbour.getTank().getStored();
                    totalCapacity += neighbour.getTank().getMaxGas();
                }
            }

            float targetRatio = (float) totalAmount / totalCapacity;
            int flowVolume = (int) Math.floor((targetRatio - tank.getFilledRatio()) * tank.getMaxGas());
            flowVolume = Math.min(maxFlowVolume, flowVolume);

            if (Math.abs(flowVolume) < 2) {
                return; // dont bother with transfers of less than a thousands of a bucket
            }

            for (GasConduit n : connections) {
                GasConduitImpl neighbour = (GasConduitImpl) n;
                if (canFlowTo(con, neighbour)) { // can only flow within same network
                    flowVolume = (int) Math.floor(
                            (targetRatio - neighbour.getTank().getFilledRatio()) * neighbour.getTank().getMaxGas());
                    if (flowVolume != 0) {
                        actions.add(new FlowAction(con, neighbour, flowVolume));
                    }
                }
            }
        } catch (UnloadedBlockException e) {
            // NOP, should be impossible
        }
    }

    private boolean canFlowTo(GasConduitImpl con, GasConduitImpl neighbour) {
        if (con == null || neighbour == null) {
            return false;
        }
        if (neighbour.getNetwork() != this) {
            return false;
        }
        return neighbour.getTank().getFilledRatio() < con.getTank().getFilledRatio();
    }

    private static class FlowAction {

        private final GasConduitImpl from;
        private final GasConduitImpl to;
        private final int amount;

        private FlowAction(GasConduitImpl fromIn, GasConduitImpl toIn, int amountIn) {
            if (amountIn < 0) {
                to = fromIn;
                from = toIn;
                amount = -amountIn;
            } else {
                to = toIn;
                from = fromIn;
                amount = amountIn;
            }
        }

        private void apply() {
            if (amount != 0) {
                // don't take more than it has
                int actual = Math.min(amount, from.getTank().getStored());
                // and don't add more than it can take
                actual = Math.min(actual, to.getTank().getNeeded());
                from.getTank().addAmount(-actual);
                to.getTank().addAmount(actual);
            }
        }
    }

    private static class LocatedGasHandler {

        private final IGasHandler tank;
        private final BlockPos pos;
        private final EnumFacing dir;

        private LocatedGasHandler(IGasHandler tank, BlockPos pos, EnumFacing dir) {
            this.tank = tank;
            this.pos = pos;
            this.dir = dir;
        }
    }
}
