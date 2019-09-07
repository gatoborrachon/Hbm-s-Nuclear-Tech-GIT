package com.hbm.items.special;

import com.hbm.items.ModItems;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.StealthBoyPacket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemStealthBoy extends ItemBattery {

	public ItemStealthBoy(long dura, long chargeRate, long dischargeRate) {
		super(dura, chargeRate, dischargeRate);

	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int integer, boolean bool) {
		if (entity instanceof EntityPlayer && stack.hasTagCompound() && stack.getTagCompound().hasKey("isActive")) {
			if (stack.getTagCompound().getBoolean("isActive")) {
				this.dischargeBattery(stack, 1);
				if (ItemBattery.getCharge(stack) <= 0)
					stack.getTagCompound().setBoolean("isActive", false);
				ItemBattery.updateDamage(stack);
				
				PacketDispatcher.wrapper.sendToAll(new StealthBoyPacket(stack.getTagCompound().getBoolean("isActive"), (EntityPlayer) entity));
				
			}
			((EntityPlayer) entity).getEntityData().setBoolean("StealthActivated",
					stack.getTagCompound().getBoolean("isActive"));

		}

	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

		if (!world.isRemote) {
			if (!stack.hasTagCompound()) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setBoolean("isActive", false);
				stackTag.setLong("charge", this.maxCharge);
				stack.setTagCompound(stackTag);
			}

			NBTTagCompound tag = player.getEntityData();
			if (stack.getTagCompound().getBoolean("isActive")) {
				tag.setBoolean("StealthActivated", true);
			} else if (!stack.getTagCompound().getBoolean("isActive")) {
				tag.setBoolean("StealthActivated", false);
			}

			tryToggle(stack, player);
		}

		return stack;
	}

	private void tryToggle(ItemStack stack, EntityPlayer player) {
		if (!stack.getTagCompound().getBoolean("isActive") && this.getMaxCharge() / 4 < ItemBattery.getCharge(stack)) {
			stack.getTagCompound().setBoolean("isActive", true);
			this.dischargeBattery(stack, this.getMaxCharge() / 4);
			PacketDispatcher.wrapper
					.sendToAll(new StealthBoyPacket(stack.getTagCompound().getBoolean("isActive"), player));
		} else if (stack.getTagCompound().getBoolean("isActive")) {

			stack.getTagCompound().setBoolean("isActive", false);
			PacketDispatcher.wrapper
					.sendToAll(new StealthBoyPacket(stack.getTagCompound().getBoolean("isActive"), player));
		}

	}
}
