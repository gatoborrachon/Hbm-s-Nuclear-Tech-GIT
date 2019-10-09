package com.hbm.tileentity.conductor;

import com.hbm.forgefluid.ModForgeFluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

public class TileEntityFFGasDuctSolid extends TileEntityFFDuctBase {
	public TileEntityFFGasDuctSolid(){
		System.out.println("whateafd");
		thisIsATest = true;
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
