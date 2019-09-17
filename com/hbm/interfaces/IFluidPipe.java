package com.hbm.interfaces;

import com.hbm.forgefluid.FFPipeNetwork;

import net.minecraftforge.fluids.Fluid;

public interface IFluidPipe {
	public FFPipeNetwork getNetwork();
	public void setNetwork(FFPipeNetwork net);
	public Fluid getType();
	void setType(Fluid fluid);
	boolean getIsValidForForming();
	
}
