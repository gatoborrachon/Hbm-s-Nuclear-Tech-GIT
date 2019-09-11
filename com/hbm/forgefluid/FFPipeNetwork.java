package com.hbm.forgefluid;

import java.util.ArrayList;
import java.util.List;

import com.hbm.interfaces.IFluidPipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

public class FFPipeNetwork {
	protected Fluid type;
	protected List<IFluidHandler> fillables = new ArrayList<IFluidHandler>();
	protected List<IFluidPipe> pipes = new ArrayList<IFluidPipe>();
	
	public FFPipeNetwork(){
		this(null);
	}
	public FFPipeNetwork(Fluid fluid){
		this.type = fluid;
	}
	
	public static FFPipeNetwork mergeNetworks(FFPipeNetwork net, FFPipeNetwork merge) {
		if(net != null && merge != null) {
			for(IFluidPipe pipe : merge.pipes) {
				net.pipes.add(pipe);
				pipe.setNetwork(net);
			}
			for(IFluidHandler fill : merge.fillables) {
				net.fillables.add(fill);
				
			}
			return net;
		} else {
			return null;
		}
	}
	
	public void setType(Fluid fluid){
		this.type = fluid;
	}
	
	public Fluid getType(){
		return this.type;
	}
	
	public boolean addPipe(IFluidPipe pipe){
		if(pipe.getType() != null && pipe.getType() == this.getType()){
			pipes.add(pipe);
			return true;
		}
		return false;
	}
	
	public boolean removePipe(IFluidPipe pipe){
		if(pipes.contains(pipe)){
			pipes.remove(pipe);
			return true;
		}
		return false;
	}
}
