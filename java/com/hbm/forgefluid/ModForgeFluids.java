package com.hbm.forgefluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModForgeFluids {
	
	public static FluidHbmToxic toxic;
	public static Fluid oil;
	
	public static void PreInit(){
		toxic = new FluidHbmToxic();
		oil = new Fluid("hbmoil").setUnlocalizedName("hbmoil");
		FluidRegistry.registerFluid(toxic);
		FluidRegistry.registerFluid(oil);

	}

}
