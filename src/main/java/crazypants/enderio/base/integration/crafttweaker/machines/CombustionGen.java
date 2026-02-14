package crazypants.enderio.base.integration.crafttweaker.machines;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import crazypants.enderio.base.integration.crafttweaker.CTIntegration;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Map;

@ZenClass("mods.enderio.CombustionGen")
@ZenRegister
public class CombustionGen {

    private static final Map<String, IFluidCoolant> coolants = ObfuscationReflectionHelper.getPrivateValue(FluidFuelRegister.class, FluidFuelRegister.instance, "coolants");
    private static final Map<String, IFluidFuel> fuels = ObfuscationReflectionHelper.getPrivateValue(FluidFuelRegister.class, FluidFuelRegister.instance, "fuels");

    @ZenMethod
    public static void addFuel(ILiquidStack fuel, int powerPerCycleRF, int totalBurnTime) {
        CTIntegration.ADDITIONS.add(() -> FluidFuelRegister.instance.addFuel(CraftTweakerMC.getLiquidStack(fuel).getFluid(), powerPerCycleRF, totalBurnTime));
    }

    @ZenMethod
    public static void addCoolant(ILiquidStack coolant, float degreesCoolingPerMB) {
        CTIntegration.ADDITIONS.add(() -> FluidFuelRegister.instance.addCoolant(CraftTweakerMC.getLiquidStack(coolant).getFluid(), degreesCoolingPerMB));
    }

    @ZenMethod
    public static void removeFuel(ILiquidStack fuel) {
        if (fuel == null) {
            CraftTweakerAPI.logError("Cannot remove null fuel.");
            return;
        }
        CTIntegration.REMOVALS.add(() -> fuels.remove(fuel.getName()));
    }

    @ZenMethod
    public static void removeCoolant(ILiquidStack coolant) {
        if (coolant == null) {
            CraftTweakerAPI.logError("Cannot remove null coolant.");
            return;
        }
        CTIntegration.REMOVALS.add(() -> coolants.remove(coolant.getName()));
    }
}
