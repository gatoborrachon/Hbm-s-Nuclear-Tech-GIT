package com.hbm.tileentity.conductor;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

import com.hbm.forgefluid.FFPipeNetwork;
import com.hbm.interfaces.IFluidPipe;


public class TileEntityFFOilDuct extends TileEntity implements IFluidPipe {

	public Fluid type = null;
	public FFPipeNetwork network = null;
	
	@Override
	public Fluid getType() {
		return this.type;
	}

	public FFPipeNetwork createNewNetwork(){
		return new FFPipeNetwork(this.type);
	}

	@Override
	public FFPipeNetwork getNetwork() {
		if(this.network != null){
		return this.network;
		} else {
			return this.createNewNetwork();
		}
	}
}
