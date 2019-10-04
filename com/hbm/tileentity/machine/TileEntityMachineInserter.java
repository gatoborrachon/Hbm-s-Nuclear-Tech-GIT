package com.hbm.tileentity.machine;

import com.hbm.forgefluid.FFUtils;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.ModItems;
import com.hbm.packet.FluidTankPacket;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMachineInserter extends TileEntity implements ISidedInventory, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];
	
	//public static final int maxFill = 64 * 3;
	public FluidTank tanks[];
	public boolean needsUpdate;

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {0};
	private static final int[] slots_side = new int[] {0};
	public int age = 0;
	
	private String customName;
	
	public TileEntityMachineInserter() {
		slots = new ItemStack[9];
		tanks = new FluidTank[3];
		tanks[0] = new FluidTank(32000);
		tanks[1] = new FluidTank(32000);
		tanks[2] = new FluidTank(32000);
		needsUpdate = false;
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
		return this.hasCustomInventoryName() ? this.customName : "container.inserter";
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
			return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <=64;
		}
	}
	
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
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
		
		slots = new ItemStack[getSizeInventory()];

		NBTTagList tankList = nbt.getTagList("tanks", 10);
		for (int i = 0; i < tankList.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte b0 = tag.getByte("tank");
			if (b0 >= 0 && b0 < tanks.length) {
				tanks[b0].readFromNBT(tag);
			}
		}
		
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
		NBTTagList list = new NBTTagList();

		NBTTagList tankList = new NBTTagList();
		for (int i = 0; i < tanks.length; i++) {
			if (tanks[i] != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("tank", (byte) i);
				tanks[i].writeToNBT(tag);
				tankList.appendTag(tag);
			}
		}
		nbt.setTag("tanks", tankList);
		
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
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemStack, int j) {
		return false;
	}
	
	@Override
	public void updateEntity() {

		if(!worldObj.isRemote)
		{
			age++;
			if(age >= 20)
			{
				age = 0;
			}
			if(needsUpdate) {
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[] {tanks[0], tanks[1], tanks[2]}));
				needsUpdate = false;
			}
			
			if(age == 9 || age == 19) {
				if(dna1())
					fillFluidInit(tanks[0]);
				if(dna2())
					fillFluidInit(tanks[1]);
				if(dna3())
					fillFluidInit(tanks[2]);
			}

		}
	}
	
	public boolean dna1() {
		if(slots[0] != null && (slots[0].getItem() == ModItems.fuse || slots[0].getItem() == ModItems.screwdriver))
			return true;
		return false;
	}
	
	public boolean dna2() {
		if(slots[3] != null && (slots[3].getItem() == ModItems.fuse || slots[3].getItem() == ModItems.screwdriver))
			return true;
		return false;
	}
	
	public boolean dna3() {
		if(slots[6] != null && (slots[6].getItem() == ModItems.fuse || slots[6].getItem() == ModItems.screwdriver))
			return true;
		return false;
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

	public void fillFluidInit(FluidTank tank) {
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord, 4000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord, 4000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord + 1, this.zCoord, 4000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord - 1, this.zCoord, 4000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 1, 4000);
		FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 1, 4000);
	}

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 3) {
			return;
		} else {
			tanks[0].readFromNBT(tags[0]);
			tanks[1].readFromNBT(tags[1]);
			tanks[2].readFromNBT(tags[2]);
		}
		
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource == null) {
			return 0;
		} else if(tanks[0].getFluid() == null || tanks[0].getFluid().getFluid() == resource.getFluid()) {
			needsUpdate = true;
			return tanks[0].fill(resource, doFill);
		} else if(tanks[1].getFluid() != null || tanks[1].getFluid().getFluid() == resource.getFluid()) {
			needsUpdate = true;
			return tanks[1].fill(resource, doFill);
		} else if(tanks[2].getFluid() == null || tanks[2].getFluid().getFluid() == resource.getFluid()) {
			needsUpdate = true;
			return tanks[3].fill(resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource == null) {
			return null;
		} else if(tanks[0].getFluid() != null && tanks[0].getFluid().isFluidEqual(resource)) {
			needsUpdate = true;
			return tanks[0].drain(resource.amount, doDrain);
		} else if(tanks[1].getFluid() != null && tanks[1].getFluid().isFluidEqual(resource)) {
			needsUpdate = true;
			return tanks[1].drain(resource.amount, doDrain);
		} else if(tanks[2].getFluid() != null && tanks[2].getFluid().isFluidEqual(resource)) {
			needsUpdate = true;
			return tanks[3].drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(tanks[0].getFluid() != null) {
			needsUpdate = true;
			return tanks[0].drain(maxDrain, doDrain);
		} else if(tanks[1].getFluid() != null) {
			needsUpdate = true;
			return tanks[1].drain(maxDrain, doDrain);
		} else if(tanks[2].getFluid() != null) {
			return tanks[2].drain(maxDrain, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return (tanks[0].getFluid() == null || tanks[0].getFluid().getFluid() == fluid) || (tanks[1].getFluid() == null || tanks[1].getFluid().getFluid() == fluid) || (tanks[2].getFluid() == null || tanks[2].getFluid().getFluid() == fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return (tanks[0].getFluid() != null && tanks[0].getFluid().getFluid() == fluid) || (tanks[1].getFluid() != null && tanks[1].getFluid().getFluid() == fluid) || (tanks[2].getFluid() != null && tanks[2].getFluid().getFluid() == fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {tanks[0].getInfo(), tanks[1].getInfo(), tanks[2].getInfo()};
	}
}
