package com.hbm.interfaces;

import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

public interface IHBMFluidHandler extends IFluidHandler {
	
	public List<IFluidHandler> getFluidList(Fluid type);
	public void clearFluidList(Fluid type);
	public Object getTact();
	public void fillFluid(int x, int i, int z, Object tact, Fluid type);

}
