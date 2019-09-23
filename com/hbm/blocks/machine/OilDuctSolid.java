package com.hbm.blocks.machine;

import com.hbm.tileentity.conductor.TileEntityFFDuctBase;
import com.hbm.tileentity.conductor.TileEntityFFOilDuctSolid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class OilDuctSolid extends BlockContainer {

	public OilDuctSolid(Material p_i45386_1_) {
		super(p_i45386_1_);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityFFOilDuctSolid();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int whatever){
		if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityFFDuctBase) {
			((TileEntityFFDuctBase)world.getTileEntity(x, y, z)).breakBlock();
		}
		super.breakBlock(world, x, y, z, block, whatever);
		
	}
	
	@Override
	 public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityFFDuctBase) {
			((TileEntityFFDuctBase)world.getTileEntity(x, y, z)).onNeighborBlockChange();
		}
	}
}
