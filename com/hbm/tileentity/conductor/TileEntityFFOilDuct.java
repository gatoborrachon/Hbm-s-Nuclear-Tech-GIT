package com.hbm.tileentity.conductor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import com.hbm.forgefluid.ModForgeFluids;

public class TileEntityFFOilDuct extends TileEntityFFDuctBase {

	public TileEntityFFOilDuct(){
		thisIsATest = true;
		this.type = ModForgeFluids.oil;
	}
	
	@Override
	public void setType(Fluid fluid) {
		this.type = ModForgeFluids.oil;
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
		type = ModForgeFluids.oil;
    }

    @Override
	public void writeToNBT(NBTTagCompound nbt)
    {
		super.writeToNBT(nbt);
    }
}
