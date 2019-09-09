package com.hbm.tileentity.conductor;

import java.util.ArrayList;
import java.util.List;

import com.hbm.calc.UnionOfTileEntitiesAndBooleansForFluids;
import com.hbm.calc.UnionOfTileEntitiesAndBooleansForOil;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IFluidDuct;
import com.hbm.interfaces.IOilDuct;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

public class TileEntityOilDuctSolid extends TileEntity implements IFluidDuct {

	public Fluid type = ModForgeFluids.oil;
	public List<UnionOfTileEntitiesAndBooleansForFluids> uoteab = new ArrayList<UnionOfTileEntitiesAndBooleansForFluids>();

	@Override
	public Fluid getType() {
		return type;
	}

}
