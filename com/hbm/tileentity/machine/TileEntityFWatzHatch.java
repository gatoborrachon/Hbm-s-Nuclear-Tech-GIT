package com.hbm.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityFWatzHatch extends TileEntity implements IFluidHandler {
	@Override
	public void updateEntity(){
		super.updateEntity();
	}
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		TileEntityFWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.fill(from, resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		TileEntityFWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.drain(from, resource, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		TileEntityFWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.drain(from, maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		TileEntityFWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.canFill(from, fluid);
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		TileEntityFWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.canDrain(from, fluid);
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		TileEntityFWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
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

	private TileEntityFWatzCore getReactorTE(World world, int x, int y, int z) {
		if(world.getBlockMetadata(x, y, z) == 2)
		{
			if(world.getTileEntity(x, y + 11, z + 9) != null && world.getTileEntity(x, y + 11, z + 9) instanceof TileEntityFWatzCore)
			{
				if(((TileEntityFWatzCore)world.getTileEntity(x, y + 11, z + 9)).isStructureValid(world))
				{
					return (TileEntityFWatzCore)world.getTileEntity(x, y + 11, z + 9);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 3)
		{
			if(world.getTileEntity(x, y + 11, z - 9) != null && world.getTileEntity(x, y + 11, z - 9) instanceof TileEntityFWatzCore)
			{
				if(((TileEntityFWatzCore)world.getTileEntity(x, y + 11, z - 9)).isStructureValid(world))
				{
					return (TileEntityFWatzCore)world.getTileEntity(x, y + 11, z - 9);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 4)
		{
			if(world.getTileEntity(x + 9, y + 11, z) != null && world.getTileEntity(x + 9, y + 11, z) instanceof TileEntityFWatzCore)
			{
				if(((TileEntityFWatzCore)world.getTileEntity(x + 9, y + 11, z)).isStructureValid(world))
				{
					return (TileEntityFWatzCore)world.getTileEntity(x + 9, y + 11, z);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 5)
		{
			if(world.getTileEntity(x - 9, y + 11, z) != null && world.getTileEntity(x - 9, y + 11, z) instanceof TileEntityFWatzCore)
			{
				if(((TileEntityFWatzCore)world.getTileEntity(x - 9, y + 11, z)).isStructureValid(world))
				{
					return (TileEntityFWatzCore)world.getTileEntity(x - 9, y + 11, z);
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
