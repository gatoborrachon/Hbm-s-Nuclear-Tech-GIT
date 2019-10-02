package com.hbm.tileentity.conductor;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
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
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IFluidPipe;
import com.hbm.lib.Library;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.TEFluidTypePacketTest;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFFOilDuct extends TileEntityFFDuctBase {

	public TileEntityFFOilDuct(){
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
