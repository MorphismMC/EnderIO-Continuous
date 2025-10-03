package crazypants.enderio.conduits.capability;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import com.enderio.core.common.util.NullHelper;

public class CapabilityUpgradeHolder {

    @CapabilityInject(UpgradeHolder.class)
    @Nonnull
    public static Capability<UpgradeHolder> UPGRADE_HOLDER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(UpgradeHolder.class, new Storage(), new Factory());

        NullHelper.notnullJ(UPGRADE_HOLDER_CAPABILITY, "Filter Holder Capability is not registered");
    }

    private static class Storage implements Capability.IStorage<UpgradeHolder> {

        @Override
        @Nullable
        public NBTBase writeNBT(Capability<UpgradeHolder> capability, UpgradeHolder instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<UpgradeHolder> capability, UpgradeHolder instance, EnumFacing side,
                            NBTBase nbt) {}
    }

    private static class Factory implements Callable<UpgradeHolder> {

        @Override
        public UpgradeHolder call() throws Exception {
            return null;
        }
    }
}
