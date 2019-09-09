package com.hbm.tileentity.conductor;

import java.util.ArrayList;
import java.util.List;

import com.hbm.calc.UnionOfTileEntitiesAndBooleansForFluids;
import com.hbm.calc.UnionOfTileEntitiesAndBooleansForGas;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IFluidDuct;
import com.hbm.interfaces.IGasDuct;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

public class TileEntityGasDuctSolid extends TileEntity implements IFluidDuct {

	public Fluid type = ModForgeFluids.gas;
	public List<UnionOfTileEntitiesAndBooleansForFluids> uoteab = new ArrayList<UnionOfTileEntitiesAndBooleansForFluids>();

	@Override
	public Fluid getType() {
		return type;
	}

}
