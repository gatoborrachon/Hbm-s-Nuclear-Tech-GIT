package com.hbm.interfaces;

import com.hbm.forgefluid.FFPipeNetwork;

import net.minecraftforge.fluids.Fluid;

public interface IFluidPipe {
	public FFPipeNetwork getNetwork();
	public Fluid getType();
	
}
