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

	/**
	 * Constructor.
	 */
	public FFPipeNetwork() {
		this(null);
	}

	/**
	 * Constructs the network with a fluid type, hbm pipes only work with a single fluid pipe.
	 * @param fluid
	 */
	public FFPipeNetwork(Fluid fluid) {
		//new Exception().printStackTrace();
		this.type = fluid;
		MainRegistry.allPipeNetworks.add(this);
	}
	
	/**
	 * Gets the number of pipes and consumers in the network.
	 * @return - the number of pipes in the network plus the number of consumers
	 */
	public int getSize() {
		return pipes.size() + fillables.size();
	}
	
	/**
	 * Gets a list of things the network is currently trying to fill
	 * @return - list of consumers in the network
	 */
	public List<IFluidHandler> getConsumers(){
		return this.fillables;
	}
	
	/**
	 * Gets a list of pipes in the network.
	 * @return - list of pipes in the network
	 */
	public List<IFluidPipe> getPipes(){
		return this.pipes;
	}
	
	/**
	 * Called whenever the world ticks to fill any connected fluid handlers
	 */
	public void updateTick(){
		if(tickTimer < 20){
			tickTimer ++;
		} else {
			tickTimer = 0;
		}
		if(tickTimer == 9 || tickTimer == 19){
		//	if(pipes.isEmpty())
			//	this.Destroy();
		//	cleanPipes();
			//cleanConsumers();
			fillFluidInit();
		}
		
	}
	
	public void fillFluidInit(){
		//System.out.println(this);
		//Pretty much the same thing as the transfer fluid in Library.java
		if(internalNetworkTank.getFluid() == null || internalNetworkTank.getFluidAmount() <= 0)
			return;
		
		List<IFluidHandler> consumers = new ArrayList<IFluidHandler>();
		for(IFluidHandler handle : this.fillables){
			if(handle != null && handle.canFill(ForgeDirection.UNKNOWN, internalNetworkTank.getFluid().getFluid()) && handle.fill(ForgeDirection.UNKNOWN, new FluidStack(this.type, 1), false) > 0 && !consumers.contains(handle));
				consumers.add(handle);
		}
		int size = consumers.size();
		if(size <= 0)
			return;
		int part = this.internalNetworkTank.getFluidAmount() / size;
		int lastPart = part + this.internalNetworkTank.getFluidAmount() - part * size;
		int i = 1;
		for(IFluidHandler consume : consumers){
			i++;
			if(internalNetworkTank.getFluid() != null)
				internalNetworkTank.drain(consume.fill(ForgeDirection.UNKNOWN, new FluidStack(internalNetworkTank.getFluid().getFluid(), i<consumers.size()?part:lastPart), true), true);
		}
	}

/*	public void cleanPipes(){
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
	}*/
	/**
	 * Merges two pipe networks together. Usually called when you connect two or more pipe networks with another pipe
	 * @param net - the network that you want to merge into
	 * @param merge - the network that gets merged, then deleted
	 * @return The newly merged network
	 */
	public static FFPipeNetwork mergeNetworks(FFPipeNetwork net, FFPipeNetwork merge) {
		if (net != null && merge != null && net != merge) {
			for (IFluidPipe pipe : merge.pipes) {
				net.addPipe(pipe);
				pipe.setNetwork(net);
			}
			merge.pipes.clear();
			for (IFluidHandler fill : merge.fillables) {
				net.fillables.add(fill);

			}
			merge.Destroy();
			return net;
		} else if(net != null) {
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
//			if(!pipe.getWorldObj().isRemote)
			//	return null;
			IFluidPipe fPipe = (IFluidPipe) pipe;
			fPipe.getNetwork().Destroy();
			//System.out.println("true net: " + fPipe.getNetworkTrue());
			net = new FFPipeNetwork(fPipe.getType());
			net.setType(fPipe.getType());
			List[] netVars = iteratePipes(null, null, null, pipe, net.getType());
		//	net.pipes = netVars[0];
			net.pipes.clear();
			net.pipes.addAll(netVars[0]);
		//	System.out.println(netVars[0].size());
			//System.out.println(netVars[1].size());
			//System.out.println(netVars[2].size());
			for(IFluidPipe setPipe : net.pipes){
				setPipe.setNetwork(net);
			}
		//	net.fillables = netVars[1];
			net.fillables.clear();
			net.fillables.addAll(netVars[1]);
			
			List<FFPipeNetwork> mergeList = netVars[2];
			for(FFPipeNetwork network : mergeList){
				mergeNetworks(net, network);
			}
			
		}
		return net;
	}

	/**
	 * Recursive function that goes through all the pipes and fluid handlers connected to each other and returns a list of both. Called when building a new network.
	 * @param pipes - the list of pipes to add new pipes to
	 * @param consumers - the list of consumers to add new consumers to
	 * @param networks - this list of networks the currently iterating pipe network is connected to
	 * @param te - the TileEntity you want to start the iteration from
	 * @param type - the type of fluid the network hsa
	 * @return A list array containing the pipes connected to the network and the fluid handlers connected to the network.
	 */
	public static List[] iteratePipes(List<IFluidPipe> pipes, List<IFluidHandler> consumers, List<FFPipeNetwork> networks, TileEntity te, Fluid type) {
		
		if(pipes == null)
			pipes = new ArrayList<IFluidPipe>();
		if(consumers == null)
			consumers = new ArrayList<IFluidHandler>();
		if(networks == null)
			networks = new ArrayList<FFPipeNetwork>();
		if (te == null)
			return new List[]{pipes, consumers, networks};
		TileEntity next = null;
		if (te.getWorldObj().getTileEntity(te.xCoord, te.yCoord, te.zCoord) != null) {
			if(!pipes.contains((IFluidPipe)te) && ((IFluidPipe)te).getIsValidForForming()){
				pipes.add((IFluidPipe) te);
				//System.out.println("TE Coords: " + te.xCoord + " " + te.yCoord + " " + te.zCoord);
			}
			for (int i = 0; i < 6; i++) {
				next = getTileEntityAround(te, i);
				if (next instanceof IFluidHandler && next instanceof IFluidPipe && ((IFluidPipe)next).getIsValidForForming() && !pipes.contains((IFluidPipe)next)) {

					List[] nextPipe = iteratePipes(pipes, consumers, networks, next, type);
					//So java really does pass by location and not by value. I feel dumb now.
					
					//System.out.println("pipes length 1: " + pipes.size());
					//pipes.clear();
					//pipes.addAll(nextPipe[0]);
					//System.out.println("pipes length 2: " + pipes.size());
					//consumers.addAll(nextPipe[1]);
					//networks.addAll(nextPipe[2]);
				} else if (next instanceof IFluidHandler && !(next instanceof IFluidPipe)) {
				
					consumers.add((IFluidHandler) next);
				}
			}
			if(((IFluidPipe)te).getNetworkTrue() != null && ((IFluidPipe)te).getIsValidForForming() && ((IFluidPipe)te).getNetworkTrue().getType() == type && !networks.contains(((IFluidPipe)te).getNetwork())){
				networks.add(((IFluidPipe)te).getNetwork());
			}
		}
		
		return new List[]{pipes, consumers, networks};
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
		for(IFluidPipe pipe : pipes){
			pipe.setNetwork(null);
		}
		this.pipes.clear();
		MainRegistry.allPipeNetworks.remove(this);
	}
	
	public void destroySoft() {
		this.fillables.clear();
		for(IFluidPipe pipe : pipes){
			pipe.setNetwork(null);
		}
		this.pipes.clear();
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

	/**
	 * Remove a pipe from the network.
	 * @param pipe - the pipe to be removed
	 * @return if it successfully removed the pipe
	 */
	public boolean removePipe(IFluidPipe pipe) {
		if (pipes.contains(pipe)) {
			return pipes.remove(pipe);
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
