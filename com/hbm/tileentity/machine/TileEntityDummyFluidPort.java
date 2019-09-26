package com.hbm.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityDummyFluidPort extends TileEntityDummy implements IFluidHandler {

	IFluidHandler tetarget;
    @Override
	public void updateEntity() {
    	super.updateEntity();
    }

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		tetarget = (IFluidHandler) worldObj.getTileEntity(targetX, targetY, targetZ);
		if(tetarget != null){
			return tetarget.fill(from, resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		tetarget = (IFluidHandler) worldObj.getTileEntity(targetX, targetY, targetZ);
		if(tetarget != null)
			return tetarget.drain(from, resource, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		tetarget = (IFluidHandler) worldObj.getTileEntity(targetX, targetY, targetZ);
		if(tetarget != null)
			return tetarget.drain(from, maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		tetarget = (IFluidHandler) worldObj.getTileEntity(targetX, targetY, targetZ);
		if(tetarget != null )
			return tetarget.canFill(from, fluid);
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		tetarget = (IFluidHandler) worldObj.getTileEntity(targetX, targetY, targetZ);
		if(tetarget != null)
			return tetarget.canDrain(from, fluid);
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		tetarget = (IFluidHandler) worldObj.getTileEntity(targetX, targetY, targetZ);
		if(tetarget != null)
			return tetarget.getTankInfo(from);
		return null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);
	}

}
