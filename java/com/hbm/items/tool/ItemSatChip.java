package com.hbm.items.tool;

import java.util.List;

import com.hbm.entity.projectile.EntityTom;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemSatChip extends Item {

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
	{
		list.add("Satellite frequency: " + getFreq(itemstack));
	}

	public static int getFreq(ItemStack stack) {
		if(stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
			return 0;
		}
		return stack.stackTagCompound.getInteger("freq");
	}
	
	public static void setFreq(ItemStack stack, int i) {
		if(stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
		}
		stack.stackTagCompound.setInteger("freq", i);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player){
		//EntityTom tom = new EntityTom(world);
	//	tom.setPosition(player.posX, 254, player.posZ);
		//world.spawnEntityInWorld(tom);
		
		return stack;
		
	}
	
}
