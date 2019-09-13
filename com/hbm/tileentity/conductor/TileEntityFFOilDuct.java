package com.hbm.tileentity.conductor;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

import com.hbm.forgefluid.FFPipeNetwork;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IFluidPipe;
import com.hbm.lib.Library;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.TEFluidTypePacketTest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFFOilDuct extends TileEntity implements IFluidPipe {

	public ForgeDirection[] connections = new ForgeDirection[6];
	public Fluid type = ModForgeFluids.oil;
	public FFPipeNetwork network = null;
	
	public boolean firstUpdate = true;

	public TileEntityFFOilDuct() {
	}
	
	@Override
	public void updateEntity(){
		if(!worldObj.isRemote)
			PacketDispatcher.wrapper.sendToAll(new TEFluidTypePacketTest(xCoord, yCoord, zCoord, type));
		this.updateConnections();
		if(firstUpdate){
			this.getNetwork();
			firstUpdate = false;
		}
	}
	
	public void updateConnections() {
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord + 1, zCoord, type)) connections[0] = ForgeDirection.UP;
		else connections[0] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord - 1, zCoord, type)) connections[1] = ForgeDirection.DOWN;
		else connections[1] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord, zCoord - 1, type)) connections[2] = ForgeDirection.NORTH;
		else connections[2] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord + 1, yCoord, zCoord, type)) connections[3] = ForgeDirection.EAST;
		else connections[3] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord, yCoord, zCoord + 1, type)) connections[4] = ForgeDirection.SOUTH;
		else connections[4] = null;
		
		if(FFUtils.checkFluidConnectables(this.worldObj, xCoord - 1, yCoord, zCoord, type)) connections[5] = ForgeDirection.WEST;
		else connections[5] = null;
	}
	
	@Override
	public Fluid getType() {
		return this.type;
	}
	
	@Override
	public void setType(Fluid fluid) {
		this.type = ModForgeFluids.oil;
	}

	public FFPipeNetwork createNewNetwork() {
		return new FFPipeNetwork(this.type);
	}

	@Override
	public FFPipeNetwork getNetwork() {
		if (this.network != null) {
			return this.network;
		} else {
			this.checkOtherNetworks();
			return this.network;
		}
	}

	@Override
	public void setNetwork(FFPipeNetwork net) {
		this.network = net;

	}

	public void checkOtherNetworks() {
		List<FFPipeNetwork> list = new ArrayList<FFPipeNetwork>();
		TileEntity te;
		FFPipeNetwork largeNet = null;
		for (int i = 0; i < 6; i++) {
			te = FFPipeNetwork.getTileEntityAround(this, i);
			if (te instanceof IFluidPipe && ((IFluidPipe) te).getNetwork() != null && ((IFluidPipe) te).getNetwork().getType() == this.getType()) {
				if (!list.contains(((IFluidPipe) te).getNetwork())) {
					list.add(((IFluidPipe) te).getNetwork());
					if (largeNet == null
							|| ((IFluidPipe) te).getNetwork().getSize() > largeNet
									.getSize())
						largeNet = ((IFluidPipe) te).getNetwork();
				}
			}
		}
		if (largeNet != null) {
			for (FFPipeNetwork network : list) {
				FFPipeNetwork.mergeNetworks(largeNet, network);
			}
			this.network = largeNet;
		} else {
			this.network = this.createNewNetwork();
		}
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
		type = FluidRegistry.getFluid(nbt.getInteger("FluidType"));
    }

    @Override
	public void writeToNBT(NBTTagCompound nbt)
    {
		super.writeToNBT(nbt);
		nbt.setInteger("FluidType", this.type.getID());
    }
    
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}
	


}
