package com.hbm.calc;

import com.hbm.interfaces.IFluidSource;
import com.hbm.interfaces.IHBMFluidHandler;
import com.hbm.interfaces.IOilSource;

public class UnionOfTileEntitiesAndBooleansForFluids {
	
	public UnionOfTileEntitiesAndBooleansForFluids(IHBMFluidHandler tileentity, boolean bool)
	{
		source = tileentity;
		ticked = bool;
	}

	public IHBMFluidHandler source;
	public boolean ticked = false;
}
