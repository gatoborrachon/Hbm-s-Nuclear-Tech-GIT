package com.hbm.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityDummyFluidPort extends TileEntityDummy implements IFluidHandler {

	TileEntityMachineFluidTank tetarget;
    @Override
	public void updateEntity() {
    	super.updateEntity();
    }

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		tetarget = (TileEntityMachineFluidTank) worldObj.getTileEntity(targetX, targetY, targetZ);
		return tetarget.fill(from, resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		tetarget = (TileEntityMachineFluidTank) worldObj.getTileEntity(targetX, targetY, targetZ);
		return tetarget.drain(from, resource, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		tetarget = (TileEntityMachineFluidTank) worldObj.getTileEntity(targetX, targetY, targetZ);
		return tetarget.drain(from, maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		tetarget = (TileEntityMachineFluidTank) worldObj.getTileEntity(targetX, targetY, targetZ);
		return tetarget.canFill(from, fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		tetarget = (TileEntityMachineFluidTank) worldObj.getTileEntity(targetX, targetY, targetZ);
		return tetarget.canDrain(from, fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		tetarget = (TileEntityMachineFluidTank) worldObj.getTileEntity(targetX, targetY, targetZ);
		return tetarget.getTankInfo(from);
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
