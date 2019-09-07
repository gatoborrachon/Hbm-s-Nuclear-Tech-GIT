package com.hbm.forgefluid;

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;



public class FluidHbmToxic extends Fluid {
	public IIcon iconStill;
	public IIcon iconFlow;
	
	FluidHbmToxic(){
		
		super("hbmtoxic");
		System.out.println("Here");
		//ResourceLocation loc = new ResourceLocation(RefStrings.MODID, "");
		
		//this.setIcons((IIcon)(new ResourceLocation(RefStrings.MODID, "textures/forgefluid/toxic_still.png")), (IIcon)(new ResourceLocation(RefStrings.MODID, "textues/forgefluid/toxic_flowing.png")));
		this.setUnlocalizedName("hbmtoxic");
	}
//	@Override
//	public int getColor(){
//		return 0x5eff00;
//		
//	}

}
