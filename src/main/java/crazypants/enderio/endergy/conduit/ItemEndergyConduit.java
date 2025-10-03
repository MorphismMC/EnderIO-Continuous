package crazypants.enderio.endergy.conduit;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.Conduit;
import crazypants.enderio.base.conduit.ConduitServer;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.conduits.conduit.AbstractConduitItem;
import crazypants.enderio.conduits.conduit.power.PowerConduit;
import crazypants.enderio.conduits.conduit.power.PowerConduitData;
import crazypants.enderio.conduits.conduit.power.PowerConduitImpl;

public class ItemEndergyConduit extends AbstractConduitItem {

    public static ItemEndergyConduit create(@Nonnull IModObject modObject, @Nullable Block block) {
        return new ItemEndergyConduit(modObject);
    }

    protected ItemEndergyConduit(@Nonnull IModObject modObject) {
        super(modObject, EndergyPowerConduitData.createSubTypes(modObject));
    }

    @Override
    public @Nonnull Class<? extends Conduit> getBaseConduitType() {
        return PowerConduit.class;
    }

    @Override
    public ConduitServer createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
        return new PowerConduitImpl(
                PowerConduitData.Registry.fromID(EndergyPowerConduitData.damage2id(stack.getItemDamage())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack itemStack, @Nullable World world, @Nonnull List<String> list,
                               @Nonnull ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        int cap = PowerConduitImpl.getMaxEnergyIO(
                PowerConduitData.Registry.fromID(EndergyPowerConduitData.damage2id(itemStack.getItemDamage())));
        String prefix = EnderIO.lang.localize("power.max_output") + " ";
        list.add(prefix + LangPower.RFt(cap));
    }

    @Override
    public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
        return true;
    }
}
