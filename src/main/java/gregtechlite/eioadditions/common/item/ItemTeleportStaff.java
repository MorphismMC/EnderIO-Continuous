package gregtechlite.eioadditions.common.item;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.teleport.TravelController;
import crazypants.enderio.base.teleport.TravelUtil;
import crazypants.enderio.util.ClientUtil;
import crazypants.enderio.util.Mods;
import gregtechlite.eioadditions.common.teleport.AdditionsTravelSource;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemTeleportStaff extends Item implements IItemOfTravel, IAdvancedTooltipProvider, IHaveRenderers {

    public static ItemTeleportStaff create(IModObject modObject, @Nullable Block block) {
        return new ItemTeleportStaff(modObject);
    }

    protected ItemTeleportStaff(IModObject modObject) {
        super();
        modObject.apply(this);
        setMaxStackSize(1);
        setHasSubtypes(false);
        setCreativeTab(EnderIOTab.tabEnderIOItems);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player,
                                                    EnumHand hand) {
        ItemStack equipped = player.getHeldItem(hand);
        if (world.isRemote) {
            if (player.isSneaking()) {
                if (TeleportConfig.enableBlink.get()) {
                    TravelUtil.blink(player, hand, AdditionsTravelSource.TELEPORT_STAFF_BLINK);
                }
            } else {
                TravelController.activateTravelAccessable(equipped, hand, world, player, AdditionsTravelSource.TELEPORT_STAFF);
            }
        }
        player.swingArm(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, equipped);
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        return false;
    }

    @Override
    public void addCommonEntries(@NotNull ItemStack stack, @Nullable EntityPlayer player, @NotNull List<String> list, boolean flag) {
        SpecialTooltipHandler.addCommonTooltipFromResources(list, getTranslationKey());
    }

    @Override
    public void addDetailedEntries(@NotNull ItemStack stack, @Nullable EntityPlayer player, @NotNull List<String> list, boolean flag) {
        SpecialTooltipHandler.addDetailedTooltipFromResources(list, "item.item_travel_staff");
        if (Mods.JourneyMap.isModLoaded()) {
            list.add(EnderIO.lang.localizeExact("item.item_travel_staff.tooltip.waypoint"));
        }
    }

    /*  -------------------------------------------------- Travel Item -------------------------------------------------- */

    @Override
    public boolean isActive(EntityPlayer ep, ItemStack equipped) {
        return true;
    }

    @Override
    public void extractInternal(ItemStack item, int power) {
        // do nothing, infinite energy
    }

    @Override
    public void extractInternal(ItemStack item, IValue<Integer> power) {
        // do nothing, infinite energy
    }

    @Override
    public int getEnergyStored(ItemStack item) {
        return Integer.MAX_VALUE; // infinite energy
    }

    /*  -------------------------------------------------- Rendering -------------------------------------------------- */

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerRenderers(IModObject modObject) {
        ClientUtil.regRenderer(this, 0, new ModelResourceLocation(modObject.getRegistryName(), "inventory"));
    }
}
