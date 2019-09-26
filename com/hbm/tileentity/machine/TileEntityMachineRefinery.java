package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IConsumer;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBattery;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.PacketDispatcher;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMachineRefinery extends TileEntity implements ISidedInventory, IConsumer, IFluidHandler {

	private ItemStack slots[];

	public long power = 0;
	public int sulfur = 0;
	public static final int maxSulfur = 100;
	public static final long maxPower = 1000;
	public int age = 0;
	public boolean needsUpdate = false;
	public FluidTank[] tanks;
	public Fluid[] tankTypes;

	private static final int[] slots_top = new int[] { 1 };
	private static final int[] slots_bottom = new int[] { 0, 2, 4, 6, 8, 10, 11};
	private static final int[] slots_side = new int[] { 0, 3, 5, 7, 9 };
	
	private String customName;
	
	public TileEntityMachineRefinery() {
		slots = new ItemStack[12];
		tanks = new FluidTank[5];
		tankTypes = new Fluid[] {ModForgeFluids.hotoil, ModForgeFluids.heavyoil, ModForgeFluids.napatha, ModForgeFluids.lightoil, ModForgeFluids.petroleum};
		tanks[0] = new FluidTank(64000);
		tanks[1] = new FluidTank(16000);
		tanks[2] = new FluidTank(16000);
		tanks[3] = new FluidTank(16000);
		tanks[4] = new FluidTank(16000);
	}

	@Override
	public int getSizeInventory() {
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return slots[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if(slots[i] != null)
		{
			ItemStack itemStack = slots[i];
			slots[i] = null;
			return itemStack;
		} else {
		return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		slots[i] = itemStack;
		if(itemStack != null && itemStack.stackSize > getInventoryStackLimit())
		{
			itemStack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container.machineRefinery";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}
	
	public void setCustomName(String name) {
		this.customName = name;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(worldObj.getTileEntity(xCoord, yCoord, zCoord) != this)
		{
			return false;
		}else{
			return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <=128;
		}
	}
	
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		
		if(i == 0 && stack.getItem() instanceof ItemBattery)
			return true;
		if(i == 1 && FluidContainerRegistry.getFluidForFilledItem(stack) != null && FluidContainerRegistry.getFluidForFilledItem(stack).getFluid() == ModForgeFluids.hotoil)
			return true;
		if(stack.getItem() == ModItems.canister_empty) {
			if(i == 3)
				return true;
			if(i == 5)
				return true;
			if(i == 7)
				return true;
			if(i == 9)
				return true;
		}
		
		return false;
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j) {
		if(slots[i] != null)
		{
			if(slots[i].stackSize <= j)
			{
				ItemStack itemStack = slots[i];
				slots[i] = null;
				return itemStack;
			}
			ItemStack itemStack1 = slots[i].splitStack(j);
			if (slots[i].stackSize == 0)
			{
				slots[i] = null;
			}
			
			return itemStack1;
		} else {
			return null;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("items", 10);
		
		tankTypes[0] = ModForgeFluids.hotoil;
		tankTypes[1] = ModForgeFluids.heavyoil;
		tankTypes[2] = ModForgeFluids.napatha;
		tankTypes[3] = ModForgeFluids.lightoil;
		tankTypes[4] = ModForgeFluids.petroleum;
		
		power = nbt.getLong("power");
		tanks[0].readFromNBT(nbt.getCompoundTag("inputTank1"));
		tanks[1].readFromNBT(nbt.getCompoundTag("outputTank1"));
		tanks[2].readFromNBT(nbt.getCompoundTag("outputTank2"));
		tanks[3].readFromNBT(nbt.getCompoundTag("outputTank3"));
		tanks[4].readFromNBT(nbt.getCompoundTag("outputTank4"));
		sulfur = nbt.getInteger("sulfur");
		slots = new ItemStack[getSizeInventory()];
		
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			byte b0 = nbt1.getByte("slot");
			if(b0 >= 0 && b0 < slots.length)
			{
				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound inputTank1 = new NBTTagCompound();
		NBTTagCompound outputTank1 = new NBTTagCompound();
		NBTTagCompound outputTank2 = new NBTTagCompound();
		NBTTagCompound outputTank3 = new NBTTagCompound();
		NBTTagCompound outputTank4 = new NBTTagCompound();
		
		nbt.setLong("power", power);
		tanks[0].writeToNBT(inputTank1);
		tanks[1].writeToNBT(outputTank1);
		tanks[2].writeToNBT(outputTank2);
		tanks[3].writeToNBT(outputTank3);
		tanks[4].writeToNBT(outputTank4);
		
		nbt.setTag("inputTank1", inputTank1);
		nbt.setTag("outputTank1", outputTank1);
		nbt.setTag("outputTank2", outputTank2);
		nbt.setTag("outputTank3", outputTank3);
		nbt.setTag("outputTank4", outputTank4);
		
		nbt.setInteger("sulfur", sulfur);
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < slots.length; i++)
		{
			if(slots[i] != null)
			{
				NBTTagCompound nbt1 = new NBTTagCompound();
				nbt1.setByte("slot", (byte)i);
				slots[i].writeToNBT(nbt1);
				list.appendTag(nbt1);
			}
		}
		nbt.setTag("items", list);
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_)
    {
        return p_94128_1_ == 0 ? slots_bottom : (p_94128_1_ == 1 ? slots_top : slots_side);
    }

	@Override
	public boolean canInsertItem(int i, ItemStack itemStack, int j) {
		return this.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemStack, int j) {
		if(i == 0)
			if (itemStack.getItem() instanceof ItemBattery && ItemBattery.getCharge(itemStack) == 0)
				return true;
		if(i == 2)
			return true;
		if(i == 4)
			return true;
		if(i == 6)
			return true;
		if(i == 8)
			return true;
		if(i == 10)
			return true;
		if(i == 11)
			return true;
		
		return false;
	}
	
	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {
			
			power = Library.chargeTEFromItems(slots, 0, power, maxPower);

			age++;
			if(age >= 20)
			{
				age = 0;
			}
			
			if(age == 9 || age == 19) {
				fillFluidInit(tanks[1]);
				fillFluidInit(tanks[2]);
				fillFluidInit(tanks[3]);
				fillFluidInit(tanks[4]);
			}
			
			tanks[0].loadTank(1, 2, slots);
			
			int ho = 50;
			int nt = 25;
			int lo = 15;
			int pe = 10;
			
			if(power >= 5 && tanks[0].getFill() >= 100 &&
					tanks[1].getFill() + ho <= tanks[1].getMaxFill() && 
					tanks[2].getFill() + nt <= tanks[2].getMaxFill() && 
					tanks[3].getFill() + lo <= tanks[3].getMaxFill() && 
					tanks[4].getFill() + pe <= tanks[4].getMaxFill()) {

				tanks[0].setFill(tanks[0].getFill() - 100);
				tanks[1].setFill(tanks[1].getFill() + ho);
				tanks[2].setFill(tanks[2].getFill() + nt);
				tanks[3].setFill(tanks[3].getFill() + lo);
				tanks[4].setFill(tanks[4].getFill() + pe);
				sulfur += 1;
				power -= 5;
			}

			tanks[1].unloadTank(3, 4, slots);
			tanks[2].unloadTank(5, 6, slots);
			tanks[3].unloadTank(7, 8, slots);
			tanks[4].unloadTank(9, 10, slots);
			
			for(int i = 0; i < 5; i++) {
				tanks[i].updateTank(xCoord, yCoord, zCoord);
			}
			
			if(sulfur >= maxSulfur) {
				if(slots[11] == null) {
					slots[11] = new ItemStack(ModItems.sulfur);
					sulfur -= maxSulfur;
				} else if(slots[11] != null && slots[11].getItem() == ModItems.sulfur && slots[11].stackSize < slots[11].getMaxStackSize()) {
					slots[11].stackSize++;
					sulfur -= maxSulfur;
				}
			}
			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
		}
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}

	@Override
	public void setPower(long i) {
		power = i;
		
	}

	@Override
	public long getPower() {
		return power;
		
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	public void fillFluidInit(FluidTank tank) {
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord - 2, 2000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord + 2, 2000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord - 2, 2000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord + 2, 2000);
		
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 2, this.yCoord, this.zCoord + 1, 2000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 2, this.yCoord, this.zCoord + 1, 2000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 2, this.yCoord, this.zCoord - 1, 2000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 2, this.yCoord, this.zCoord - 1, 2000);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		// TODO Auto-generated method stub
		return null;
	}
}
