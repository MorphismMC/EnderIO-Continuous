package crazypants.enderio.machines.machine.generator.combustion;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.InventoryPlayer;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;

public class ContainerCombustionGenerator<T extends TileCombustionGenerator> extends AbstractMachineContainer<T> {

    public ContainerCombustionGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
        super(playerInv, te);
    }

    @Override
    protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {}
}
