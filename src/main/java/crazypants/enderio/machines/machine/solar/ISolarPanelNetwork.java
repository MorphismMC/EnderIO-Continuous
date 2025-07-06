package crazypants.enderio.machines.machine.solar;

import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;

import crazypants.enderio.base.item.conduitprobe.PacketConduitProbe.IHasConduitProbeData;

public interface ISolarPanelNetwork extends IHasConduitProbeData {

    boolean isValid();

    void extractEnergy(int maxExtract);

    int getEnergyAvailableThisTick();

    int getEnergyAvailablePerTick();

    int getEnergyMaxPerTick();

    void destroyNetwork();

    @Nonnull
    Set<BlockPos> getPanels();
}
