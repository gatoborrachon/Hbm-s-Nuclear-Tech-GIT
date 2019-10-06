package com.hbm.tileentity.machine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityWatzHatch extends TileEntity implements IFluidHandler {
	@Override
	public void updateEntity(){
		super.updateEntity();
	}
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		TileEntityWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.fill(from, resource, doFill);
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		TileEntityWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.drain(from, resource, doDrain);
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		TileEntityWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.drain(from, maxDrain, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		TileEntityWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.canFill(from, fluid);
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		TileEntityWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
		if(fillable != null)
			return fillable.canDrain(from, fluid);
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		TileEntityWatzCore fillable = this.getReactorTE(worldObj, xCoord, yCoord, zCoord);
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

	private TileEntityWatzCore getReactorTE(World world, int x, int y, int z) {
		if(world.getBlockMetadata(x, y, z) == 2)
		{
			if(world.getTileEntity(x, y, z + 3) != null && world.getTileEntity(x, y, z + 3) instanceof TileEntityWatzCore)
			{
				if(((TileEntityWatzCore)world.getTileEntity(x, y, z + 3)).isStructureValid(world))
				{
					return (TileEntityWatzCore)world.getTileEntity(x, y, z + 3);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 3)
		{
			if(world.getTileEntity(x, y, z - 3) != null && world.getTileEntity(x, y, z - 3) instanceof TileEntityWatzCore)
			{
				if(((TileEntityWatzCore)world.getTileEntity(x, y, z - 3)).isStructureValid(world))
				{
					return (TileEntityWatzCore)world.getTileEntity(x, y, z - 3);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 4)
		{
			if(world.getTileEntity(x + 3, y, z) != null && world.getTileEntity(x + 3, y, z) instanceof TileEntityWatzCore)
			{
				if(((TileEntityWatzCore)world.getTileEntity(x + 3, y, z)).isStructureValid(world))
				{
					return (TileEntityWatzCore)world.getTileEntity(x + 3, y, z);
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		if(world.getBlockMetadata(x, y, z) == 5)
		{
			if(world.getTileEntity(x - 3, y, z) != null && world.getTileEntity(x - 3, y, z) instanceof TileEntityWatzCore)
			{
				if(((TileEntityWatzCore)world.getTileEntity(x - 3, y, z)).isStructureValid(world))
				{
					return (TileEntityWatzCore)world.getTileEntity(x - 3, y, z);
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
