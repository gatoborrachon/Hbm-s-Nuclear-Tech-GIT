package com.hbm.items.gear;

import com.hbm.lib.ModDamageSource;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ModAxe extends ItemAxe {

	public ModAxe(ToolMaterial p_i45327_1_) {
		super(p_i45327_1_);
	}

	@Override
	public boolean hitEntity(ItemStack p_77644_1_, EntityLivingBase playerd, EntityLivingBase player) {
		player.attackEntityFrom(DamageSource.anvil, 25f);
		return super.hitEntity(p_77644_1_, playerd, player);
	}

}
