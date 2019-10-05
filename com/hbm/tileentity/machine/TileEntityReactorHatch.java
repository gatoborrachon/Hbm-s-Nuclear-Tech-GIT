package com.hbm.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityReactorHatch extends TileEntity implements IFluidHandler {

	@Override
	public void updateEntity(){
		super.updateEntity();
	}
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		TileEntityMachineReactorLarge fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.fill(from, resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		TileEntityMachineReactorLarge fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.drain(from, resource, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		TileEntityMachineReactorLarge fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.drain(from, maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		TileEntityMachineReactorLarge fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.canFill(from, fluid);
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		TileEntityMachineReactorLarge fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.canDrain(from, fluid);
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		TileEntityMachineReactorLarge fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.getTankInfo(from);
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

	private TileEntityMachineReactorLarge getReactorTE(World world, int x, int y, int z) {
		if(world.getBlockMetadata(x, y, z) == 2)
		{
			if(world.getTileEntity(x, y, z + 2) != null && world.getTileEntity(x, y, z + 2) instanceof TileEntityMachineReactorLarge)
			{
				if(((TileEntityMachineReactorLarge)world.getTileEntity(x, y, z + 2)).checkBody())
				{
					return (TileEntityMachineReactorLarge)world.getTileEntity(x, y, z + 2);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 3)
		{
			if(world.getTileEntity(x, y, z - 2) != null && world.getTileEntity(x, y, z - 2) instanceof TileEntityMachineReactorLarge)
			{
				if(((TileEntityMachineReactorLarge)world.getTileEntity(x, y, z - 2)).checkBody())
				{
					return (TileEntityMachineReactorLarge)world.getTileEntity(x, y, z - 2);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 4)
		{
			if(world.getTileEntity(x + 2, y, z) != null && world.getTileEntity(x + 2, y, z) instanceof TileEntityMachineReactorLarge)
			{
				if(((TileEntityMachineReactorLarge)world.getTileEntity(x + 2, y, z)).checkBody())
				{
					return (TileEntityMachineReactorLarge)world.getTileEntity(x + 2, y, z);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 5)
		{
			if(world.getTileEntity(x - 2, y, z) != null && world.getTileEntity(x - 2, y, z) instanceof TileEntityMachineReactorLarge)
			{
				if(((TileEntityMachineReactorLarge)world.getTileEntity(x - 2, y, z)).checkBody())
				{
					return (TileEntityMachineReactorLarge)world.getTileEntity(x - 2, y, z);
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
