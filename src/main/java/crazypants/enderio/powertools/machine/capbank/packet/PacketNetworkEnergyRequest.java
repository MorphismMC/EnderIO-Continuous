package crazypants.enderio.powertools.machine.capbank.packet;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import crazypants.enderio.powertools.machine.capbank.network.ICapBankNetwork;

public class PacketNetworkEnergyRequest extends PacketCapBank<PacketNetworkEnergyRequest, PacketNetworkEnergyResponse> {

    public PacketNetworkEnergyRequest() {}

    public PacketNetworkEnergyRequest(@Nonnull TileCapBank capBank) {
        super(capBank);
    }

    @Override
    protected PacketNetworkEnergyResponse handleMessage(TileCapBank te, PacketNetworkEnergyRequest message,
                                                        MessageContext ctx) {
        final ICapBankNetwork network = te.getNetwork();
        if (network != null) {
            return new PacketNetworkEnergyResponse(network);
        }
        return null;
    }
}
