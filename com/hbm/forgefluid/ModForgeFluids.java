package com.hbm.forgefluid;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModForgeFluids {
	
	public static Fluid steam;
	public static Fluid hotsteam;
	public static Fluid superhotsteam;
	public static Fluid coolant;
	
	public static Fluid deuterium;
	public static Fluid tritium;
	
	public static Fluid oil;
	public static Fluid hotoil;
	
	public static Fluid heavyoil;
	public static Fluid bitumen;
	public static Fluid smear;
	public static Fluid heatingoil;
	
	public static Fluid reclaimed;
	public static Fluid petroil;
	
	public static Fluid lubricant;
	
	public static Fluid naphtha;
	public static Fluid diesel;
	
	public static Fluid lightoil;
	public static Fluid kerosene;
	
	public static Fluid gas;
	public static Fluid petroleum;
	
	public static Fluid biogas;
	public static Fluid biofuel;
	
	public static Fluid nitan;
	
	public static Fluid uf6;
	public static Fluid puf6;
	public static Fluid sas3;
	
	public static Fluid amat;
	public static Fluid aschrab;
	
	public static Fluid acid;
	public static Fluid watz;
	public static Fluid cryogel;
	
	public static Fluid hydrogen;
	public static Fluid oxygen;
	public static Fluid xenon;
	public static Fluid balefire;
	
	public static void PreInit(){
		
		steam = new Fluid("hbmsteam").setUnlocalizedName("hbmsteam");
		hotsteam = new Fluid("hbmhotsteam").setUnlocalizedName("hbmhotsteam");
		superhotsteam = new Fluid("hbmsuperhotsteam").setUnlocalizedName("hbmsuperhotsteam");
		coolant = new Fluid("hbmcoolant").setUnlocalizedName("hbmcoolant");
		
		deuterium = new Fluid("hbmdeuterium").setUnlocalizedName("hbmdeuterium");
		tritium = new Fluid("hbmtritium").setUnlocalizedName("hbmtritium");
		
		oil = new Fluid("hbmoil").setUnlocalizedName("hbmoil");
		hotoil = new Fluid("hbmhotoil").setUnlocalizedName("hbmhotoil");

		heavyoil = new Fluid("hbmheavyoil").setUnlocalizedName("hbmheavyoil");
		bitumen = new Fluid("hbmbitumen").setUnlocalizedName("bmbitumen");
		smear = new Fluid("hbmsmear").setUnlocalizedName("hbmsmear");
		heatingoil = new Fluid("hbmheatingoil").setUnlocalizedName("hbmheatingoil");
		
		reclaimed = new Fluid("hbmreclaimed").setUnlocalizedName("hbmreclaimed");
		petroil = new Fluid("hbmpetroil").setUnlocalizedName("hbmpetroil");
		
		lubricant = new Fluid("hbmlubircant").setUnlocalizedName("hbmlubricant");
		
		naphtha = new Fluid("hbmnaphtha").setUnlocalizedName("hbmnaphtha");
		diesel = new Fluid("hbmdiesel").setUnlocalizedName("hbmdiesel");
		
		lightoil = new Fluid("hbmlightoil").setUnlocalizedName("hbmlightoil");
		kerosene = new Fluid("hbmkerosene").setUnlocalizedName("hbmkerosene");
		
		gas = new Fluid("hbmgas").setUnlocalizedName("hbmgas");
		petroleum = new Fluid("hbmpetroleum").setUnlocalizedName("hbmpetroleum");
		
		biogas = new Fluid("hbmbiogas").setUnlocalizedName("hbmbiogas");
		biofuel = new Fluid("hbmbiofuel").setUnlocalizedName("hbmbiofuel");
		
		nitan = new Fluid("hbmnitan").setUnlocalizedName("hbmnitan");
		
		uf6 = new Fluid("hbmuf6").setUnlocalizedName("hbmuf6");
		puf6 = new Fluid("hbmpuf6").setUnlocalizedName("hbmpuf6");
		sas3 = new Fluid("hbmsas3").setUnlocalizedName("hbmsas3");
		
		amat = new Fluid("hbmamat").setUnlocalizedName("bmamat");
		aschrab = new Fluid("hbmaschrab").setUnlocalizedName("hbmaschrab");
		
		acid = new Fluid("hbmacid").setUnlocalizedName("hbmacid");
		watz = new Fluid("hbmwatz").setUnlocalizedName("hbmwatz");
		cryogel = new Fluid("hbmcryogel").setUnlocalizedName("hbmcryogel");
		
		hydrogen = new Fluid("hbmhydrogen").setUnlocalizedName("hbmhydrogen");
		oxygen = new Fluid("hbmoxygen").setUnlocalizedName("hbmoxygen");
		xenon = new Fluid("hbmxenon").setUnlocalizedName("hbmxenon");
		balefire = new Fluid("hbmbalefire").setUnlocalizedName("hbmbalefire");
		
		
		registerFluids();
	}
	
	private static void registerFluids(){
		FluidRegistry.registerFluid(steam);
		FluidRegistry.registerFluid(hotsteam);
		FluidRegistry.registerFluid(superhotsteam);
		FluidRegistry.registerFluid(coolant);
		
		FluidRegistry.registerFluid(deuterium);
		FluidRegistry.registerFluid(tritium);
		
		FluidRegistry.registerFluid(oil);
		FluidRegistry.registerFluid(hotoil);
		
		FluidRegistry.registerFluid(heavyoil);
		FluidRegistry.registerFluid(bitumen);
		FluidRegistry.registerFluid(smear);
		FluidRegistry.registerFluid(heatingoil);
		
		FluidRegistry.registerFluid(reclaimed);
		FluidRegistry.registerFluid(petroil);
		
		FluidRegistry.registerFluid(lubricant);
		
		FluidRegistry.registerFluid(naphtha);
		FluidRegistry.registerFluid(diesel);
		
		FluidRegistry.registerFluid(lightoil);
		FluidRegistry.registerFluid(kerosene);
		
		FluidRegistry.registerFluid(gas);
		FluidRegistry.registerFluid(petroleum);
		
		FluidRegistry.registerFluid(biogas);
		FluidRegistry.registerFluid(biofuel);
		
		FluidRegistry.registerFluid(nitan);
		
		FluidRegistry.registerFluid(uf6);
		FluidRegistry.registerFluid(puf6);
		FluidRegistry.registerFluid(sas3);
		
		FluidRegistry.registerFluid(amat);
		FluidRegistry.registerFluid(aschrab);
		
		FluidRegistry.registerFluid(acid);
		FluidRegistry.registerFluid(watz);
		FluidRegistry.registerFluid(cryogel);
		
		FluidRegistry.registerFluid(hydrogen);
		FluidRegistry.registerFluid(oxygen);
		FluidRegistry.registerFluid(xenon);
		FluidRegistry.registerFluid(balefire);
		
	}

}
