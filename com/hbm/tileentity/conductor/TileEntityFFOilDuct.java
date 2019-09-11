package com.hbm.tileentity.conductor;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

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
	
	@Override
	public void setNetwork(FFPipeNetwork net) {
		this.network = net;
		
	}
	
	public void checkOtherNetworks() {
		List<FFPipeNetwork> list = new ArrayList<FFPipeNetwork>();
		TileEntity te;
		te = this.worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
		if(te != null && te instanceof IFluidPipe && ((IFluidPipe)te).getNetwork() != null) {
			list.add(((IFluidPipe)te).getNetwork());
		}
	}


}
