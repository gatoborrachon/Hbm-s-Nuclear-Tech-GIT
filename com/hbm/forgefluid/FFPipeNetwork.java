package com.hbm.forgefluid;

import java.util.ArrayList;
import java.util.List;

import com.hbm.interfaces.IFluidPipe;
import com.hbm.main.MainRegistry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class FFPipeNetwork implements IFluidHandler {
	protected Fluid type;
	protected List<IFluidHandler> fillables = new ArrayList<IFluidHandler>();
	protected List<IFluidPipe> pipes = new ArrayList<IFluidPipe>();
	
	protected FluidTank internalNetworkTank = new FluidTank(4000);

	private int tickTimer = 0;

	public FFPipeNetwork() {
		this(null);
	}

	public FFPipeNetwork(Fluid fluid) {
		this.type = fluid;
		MainRegistry.allPipeNetworks.add(this);
	}
	
	public int getSize() {
		return pipes.size() + fillables.size();
	}
	
	public List<IFluidHandler> getConsumers(){
		return this.fillables;
	}
	
	public List<IFluidPipe> getPipes(){
		return this.pipes;
	}
	
	public void updateTick(){
		if(tickTimer < 20){
			tickTimer ++;
		} else {
			tickTimer = 0;
		}
		if(tickTimer == 9 || tickTimer == 19){
			if(pipes.isEmpty())
				this.Destroy();
			cleanPipes();
			cleanConsumers();
			fillFluidInit();
		}
		
	}
	
	public void fillFluidInit(){
		//Pretty much the same thing as the transfer fluid in Library.java
		if(internalNetworkTank.getFluid() == null)
			return;
		
		List<IFluidHandler> consumers = new ArrayList<IFluidHandler>();
		for(IFluidHandler handle : this.fillables){
			if(handle != null && handle.canFill(ForgeDirection.UNKNOWN, internalNetworkTank.getFluid().getFluid()));
		}
		int size = consumers.size();
	}

	public void cleanPipes(){
		for(IFluidPipe pipe : pipes){
			if(pipe == null)
				pipes.remove(pipe);
		}
	}
	
	public void cleanConsumers(){
		for(IFluidHandler consumer : fillables){
			if(consumer == null)
				fillables.remove(consumer);
		}
	}
	/**
	 * Merges two pipe networks together. Usually called when you connect two or more pipe networks with another pipe
	 * @param net - the network that you want to merge into
	 * @param merge - the network that gets merged, then deleted
	 * @return The newly merged network
	 */
	public static FFPipeNetwork mergeNetworks(FFPipeNetwork net, FFPipeNetwork merge) {
		if (net != null && merge != null && net != merge) {
			for (IFluidPipe pipe : merge.pipes) {
				net.pipes.add(pipe);
				pipe.setNetwork(net);
			}
			for (IFluidHandler fill : merge.fillables) {
				net.fillables.add(fill);

			}
			merge.Destroy();
			return net;
		} else {
			return null;
		}
	}

	/**
	 * Builds a network around a pipe, usually called when you want to link a lot of pipes together
	 * @param pipe - the pipe tile entity you want the build the network around
	 * @return The newly built network
	 */
	public static FFPipeNetwork buildNewNetwork(TileEntity pipe) {
		FFPipeNetwork net = null;
		if (pipe instanceof IFluidPipe) {
			IFluidPipe fPipe = (IFluidPipe) pipe;
			net = fPipe.getNetwork();
			List[] netVars = iteratePipes(fPipe.getNetwork().pipes, fPipe.getNetwork().fillables, pipe);
			net.pipes = netVars[0];
			net.fillables = netVars[1];
			net.setType(fPipe.getType());
		}
		return net;
	}

	/**
	 * Recursive function that goes through all the pipes and fluid handlers connected to each other and returns a list of both. Called when building a new network.
	 * @param pipes - the list of pipes to add new pipes to
	 * @param consumers - the list of consumers to add new consumers to
	 * @param te - the TileEntity you want to start the iteration from
	 * @return A list array containing the pipes connected to the network and the fluid handlers connected to the network.
	 */
	public static List[] iteratePipes(List<IFluidPipe> pipes, List<IFluidHandler> consumers, TileEntity te) {
		if(pipes == null)
			pipes = new ArrayList<IFluidPipe>();
		if(consumers == null)
			consumers = new ArrayList<IFluidHandler>();
		if (te == null)
			return new List[]{pipes, consumers};
		TileEntity next = null;
		if (te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord) != null) {
			for (int i = 0; i < 6; i++) {
				next = getTileEntityAround(te, i);
				if (next instanceof IFluidHandler && next instanceof IFluidPipe) {
					pipes.add((IFluidPipe) next);
					List[] nextPipe = iteratePipes(pipes, consumers, te);
					pipes.addAll(nextPipe[0]);
					consumers.addAll(nextPipe[1]);
				} else if (next instanceof IFluidHandler) {
					consumers.add((IFluidHandler) next);
				}
			}

		}
		return new List[]{pipes, consumers};
	}
	/**
	 * Should be self explanatory. Normally used in a for loop to get all the surrounding tile entities. No idea why I put it in this class.
	 * @param te - the tile entity that you want to find other tile entities directly touching
	 * @param dir - the direction around the tile entity expressed as an integer
	 * @return The tile entity around the given one. Null if doesn't exist.
	 */
	public static TileEntity getTileEntityAround(TileEntity te, int dir) {
		if (te == null)
			return null;
		switch (dir) {
		case 0:
			return te.getWorldObj().getTileEntity(te.xCoord + 1, te.yCoord, te.zCoord);
		case 1:
			return te.getWorldObj().getTileEntity(te.xCoord - 1, te.yCoord, te.zCoord);
		case 2:
			return te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord + 1);
		case 3:
			return te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord - 1);
		case 4:
			return te.getWorldObj().getTileEntity(te.xCoord, te.yCoord + 1, te.zCoord);
		case 5:
			return te.getWorldObj().getTileEntity(te.xCoord, te.yCoord - 1, te.zCoord);
		default:
			return null;
		}
	}

	/**
	 * Destroys the network and removes it from the registry.
	 */
	public void Destroy() {
		this.fillables.clear();
		this.pipes.clear();
		MainRegistry.allPipeNetworks.remove(this);
	}

	/**
	 * Sets the network fluid type, because that's how HBM pipes work. Also sets every pipe in the network to be this type.
	 * @param fluid - the fluid to set the network's fluid to
	 */
	public void setType(Fluid fluid) {
		for(IFluidPipe pipe : this.pipes){
			pipe.setType(fluid);
		}
		this.type = fluid;
	}

	/**
	 * Gets the network's fluid type.
	 * @return - the network's fluid type
	 */
	public Fluid getType() {
		return this.type;
	}

	/**
	 * Adds a pipe to the network. Used when doing stuff to all the network.
	 * @param pipe - the pipe to be added
	 * @return Whether it succeeded in adding the pipe.
	 */
	public boolean addPipe(IFluidPipe pipe) {
		if (pipe.getType() != null && pipe.getType() == this.getType()) {
			pipes.add(pipe);
			return pipes.add(pipe);
		}
		return false;
	}

	public boolean removePipe(IFluidPipe pipe) {
		if (pipes.contains(pipe)) {
			pipes.remove(pipe);
			return true;
		}
		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource != null && resource.getFluid() == this.type)
			return internalNetworkTank.fill(resource, doFill);
		else
			return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource != null && resource.getFluid() == this.type)
			return internalNetworkTank.drain(resource.amount, doDrain);
		else
			return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return internalNetworkTank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == this.type;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == this.type;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		
		return new FluidTankInfo[]{internalNetworkTank.getInfo()};
	}
}
