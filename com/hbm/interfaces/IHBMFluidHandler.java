package com.hbm.interfaces;

import java.util.List;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public interface IHBMFluidHandler extends IFluidHandler {
	
	public List<IFluidHandler> getFluidList(Fluid type);
	public void clearFluidList(Fluid type);
	public boolean getTact();
	public int fillFluid(int x, int i, int z, Boolean newTact, FluidStack type);
	public int getFluidFill(Fluid type);

}
