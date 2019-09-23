package com.hbm.tileentity.conductor;

import com.hbm.forgefluid.ModForgeFluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

public class TileEntityFFGasDuct extends TileEntityFFDuctBase {
	
	public TileEntityFFGasDuct(){
		this.type = ModForgeFluids.gas;
	}
	
	@Override
	public void setType(Fluid fluid) {
		this.type = ModForgeFluids.gas;
	}
	
    @Override
	public void readFromNBT(NBTTagCompound nbt)
    {
		super.readFromNBT(nbt);
		type = ModForgeFluids.gas;
    }

    @Override
	public void writeToNBT(NBTTagCompound nbt)
    {
		super.writeToNBT(nbt);
    }
}
