package com.hbm.tileentity.conductor;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

import com.hbm.forgefluid.FFPipeNetwork;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IFluidPipe;

public class TileEntityFFOilDuct extends TileEntity implements IFluidPipe {

	public Fluid type = ModForgeFluids.oil;
	public FFPipeNetwork network = null;

	@Override
	public Fluid getType() {
		return this.type;
	}

	public FFPipeNetwork createNewNetwork() {
		return new FFPipeNetwork(this.type);
	}

	@Override
	public FFPipeNetwork getNetwork() {
		if (this.network != null) {
			return this.network;
		} else {
			return this.createNewNetwork();
		}
	}

	@Override
	public void setNetwork(FFPipeNetwork net) {
		this.network = net;

	}

	public void checkOtherNetworks() {
		List<FFPipeNetwork> list = new ArrayList<FFPipeNetwork>();
		TileEntity te;
		FFPipeNetwork largeNet = null;
		for (int i = 0; i < 6; i++) {
			te = FFPipeNetwork.getTileEntityAround(this, i);
			if (te instanceof IFluidPipe
					&& ((IFluidPipe) te).getNetwork() != null
					&& ((IFluidPipe) te).getNetwork().getType() == this
							.getType()) {
				if (!list.contains(((IFluidPipe) te).getNetwork())) {
					list.add(((IFluidPipe) te).getNetwork());
					if (largeNet == null
							|| ((IFluidPipe) te).getNetwork().getSize() > largeNet
									.getSize())
						largeNet = ((IFluidPipe) te).getNetwork();
				}
			}
		}
		if (largeNet != null) {
			for (FFPipeNetwork network : list) {
				FFPipeNetwork.mergeNetworks(largeNet, network);
			}
			this.network = largeNet;
		} else {
			this.network = this.createNewNetwork();
		}
	}

}
