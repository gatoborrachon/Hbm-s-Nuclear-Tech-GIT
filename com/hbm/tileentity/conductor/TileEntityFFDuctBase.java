package com.hbm.tileentity.conductor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

import com.hbm.forgefluid.FFPipeNetwork;
import com.hbm.forgefluid.FFUtils;
import com.hbm.interfaces.IFluidPipe;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.TEFluidTypePacketTest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFFDuctBase extends TileEntity implements IFluidPipe, IFluidHandler {

	public ForgeDirection[] connections = new ForgeDirection[6];
	public Fluid type = null;
	public FFPipeNetwork network = null;
	public IFluidHandler[] fluidHandlerCache = new IFluidHandler[6];
	
	public boolean isValidForForming = true;
	public boolean firstUpdate = true;
	public boolean needsBuildNetwork = false;
	public boolean thisIsATest = false;
	
	public int weirdTest = 0;

	public TileEntityFFDuctBase() {
	}
	
	@Override
	public void onChunkUnload(){
		if(this.getNetworkTrue() != null){
			this.getNetworkTrue().getPipes().remove(this);
		}
	}
	@Override
	public void updateEntity(){
		if(!worldObj.isRemote && type != null)
			PacketDispatcher.wrapper.sendToAll(new TEFluidTypePacketTest(xCoord, yCoord, zCoord, type));
		this.updateConnections();
		if(needsBuildNetwork){
			//this.getNetwork();
			//this.checkOtherNetworks();
			//this.network.addPipe(this);
			this.checkOtherNetworks();
			
			
			if(this.network == null) {
				FFPipeNetwork.buildNewNetwork(this);
				System.out.println("here");
			}
			this.checkFluidHandlers();
			needsBuildNetwork = false;
		}
		
		if(thisIsATest){
			System.out.println("dfasfdadf");
			this.typeChanged(this.type);
			this.checkFluidHandlers();
			thisIsATest = false;
		}
	}
	
	public void updateConnections() {
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord + 1, zCoord, getNetworkTrue())) connections[0] = ForgeDirection.UP;
		else connections[0] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord - 1, zCoord, getNetworkTrue())) connections[1] = ForgeDirection.DOWN;
		else connections[1] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord, zCoord - 1, getNetworkTrue())) connections[2] = ForgeDirection.NORTH;
		else connections[2] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord + 1, yCoord, zCoord, getNetworkTrue())) connections[3] = ForgeDirection.EAST;
		else connections[3] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord, zCoord + 1, getNetworkTrue())) connections[4] = ForgeDirection.SOUTH;
		else connections[4] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord - 1, yCoord, zCoord, getNetworkTrue())) connections[5] = ForgeDirection.WEST;
		else connections[5] = null;
	}
	
	@Override
	public Fluid getType() {
		return this.type;
	}
	
	@Override
	public void setType(Fluid fluid) {
		this.type = fluid;
		this.typeChanged(fluid);
	}
	
	@Override
	public void setTypeTrue(Fluid fluid){
		this.type = fluid;
	}

	public FFPipeNetwork createNewNetwork() {
		return new FFPipeNetwork(this.type);
	}

	@Override
	public FFPipeNetwork getNetwork() {
		if (this.network != null) {
			return this.network;
		} else {
			this.network = new FFPipeNetwork();
			this.network.setType(this.getType());
			this.network.addPipe(this);
			return this.network;
		}
	}

	@Override
	public FFPipeNetwork getNetworkTrue() {
		return this.network;
	}
	@Override
	public void setNetwork(FFPipeNetwork net) {
		this.network = net;
	}
	

	public void checkOtherNetworks() {
		List<FFPipeNetwork> list = new ArrayList<FFPipeNetwork>();
		list.add(this.getNetworkTrue());
		TileEntity te;
		FFPipeNetwork largeNet = null;
		for (int i = 0; i < 6; i++) {
			te = FFPipeNetwork.getTileEntityAround(this, i);
			if (te instanceof IFluidPipe && ((IFluidPipe) te).getNetworkTrue() != null && ((IFluidPipe) te).getNetworkTrue().getType() == this.getType()) {
				if (!list.contains(((IFluidPipe) te).getNetworkTrue())) {
					list.add(((IFluidPipe) te).getNetworkTrue());
					if (largeNet == null
							|| ((IFluidPipe) te).getNetworkTrue().getSize() > largeNet
									.getSize())
						largeNet = ((IFluidPipe) te).getNetworkTrue();
				}
			}
		}
		if (largeNet != null) {
			for (FFPipeNetwork network : list) {
				FFPipeNetwork.mergeNetworks(largeNet, network);
			}
			this.network = largeNet;
		} else {
			this.getNetwork().Destroy();
			this.network = this.createNewNetwork();
		}
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
		type = FluidRegistry.getFluid(nbt.getInteger("FluidType"));
	//	if(this.network == null) {
		//	FFPipeNetwork.buildNewNetwork(this);
		//}
		needsBuildNetwork = true;
    }

    @Override
	public void writeToNBT(NBTTagCompound nbt)
    {
		super.writeToNBT(nbt);
		if(this.type != null)
			nbt.setInteger("FluidType", this.type.getID());
    }
    
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}
	
	@Override
	public void breakBlock() {
	//	if(!this.worldObj.isRemote)
		//	PacketDispatcher.wrapper.sendToAll(new TEFFPipeDestructorPacket(this.xCoord, this.yCoord, this.zCoord));
		this.getNetwork().Destroy();
		this.isValidForForming = false;
		for(int i = 0; i < 6; i++){
			TileEntity ent = FFPipeNetwork.getTileEntityAround(this, i);
			if(ent != null && ent instanceof IFluidPipe){
				FFPipeNetwork.buildNewNetwork(ent);
			}
		}
	}
	
	public void typeChanged(Fluid type){
		
		this.getNetwork().setType(type);;
		for(int i = 0; i < 6; i++){
			TileEntity ent = FFPipeNetwork.getTileEntityAround(this, i);
			if(ent != null && ent instanceof IFluidPipe){
				FFPipeNetwork.buildNewNetwork(ent);
			}
		}
	}

	public void onNeighborBlockChange() {
		this.checkFluidHandlers();
	}
	
	public void checkFluidHandlers() {
		if(this.network == null) {
			return;
		}
		for(int i = 0; i < 6;i++) {
			TileEntity te = FFPipeNetwork.getTileEntityAround(this, i);
			if(te != null && !(te instanceof IFluidPipe) && te instanceof IFluidHandler) {
				if(fluidHandlerCache[i] != null){
					this.network.getConsumers().remove(fluidHandlerCache[i]);
				}
				if(!this.network.getConsumers().contains(te)) {
					this.network.getConsumers().add((IFluidHandler) te);
				}
				
				fluidHandlerCache[i] = (IFluidHandler)te;
					
			}
		}
	}
	
	@Override
	public boolean getIsValidForForming() {
		return this.isValidForForming;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		//System.out.println(this.getNetworkTrue().getType());
		return this.getNetwork().fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return this.getNetwork().drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return this.getNetwork().drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return this.getNetwork().canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return this.getNetwork().canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return this.getNetwork().getTankInfo(from);
	}
	
}
