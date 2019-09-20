package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.hbm.forgefluid.FFUtils;
import com.hbm.interfaces.IHBMFluidHandler;
import com.hbm.items.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMachineFluidTank extends TileEntity implements ISidedInventory, IFluidHandler {

	private ItemStack slots[];

	// public static final int maxFill = 64 * 3;
	public FluidTank tank;

	private static final int[] slots_top = new int[] { 0 };
	private static final int[] slots_bottom = new int[] { 0 };
	private static final int[] slots_side = new int[] { 0 };
	public int age = 0;
	public boolean needsUpdate = false;

	public List<IFluidHandler> list = new ArrayList();

	private String customName;

	public TileEntityMachineFluidTank() {
		slots = new ItemStack[7];
		tank = new FluidTank(256000);
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
		if (slots[i] != null) {
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
		if (itemStack != null && itemStack.stackSize > getInventoryStackLimit()) {
			itemStack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "container.fluidtank";
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
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		} else {
			return player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
		}
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (slots[i] != null) {
			if (slots[i].stackSize <= j) {
				ItemStack itemStack = slots[i];
				slots[i] = null;
				return itemStack;
			}
			ItemStack itemStack1 = slots[i].splitStack(j);
			if (slots[i].stackSize == 0) {
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

		tank.readFromNBT(nbt);

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			byte b0 = nbt1.getByte("slot");
			if (b0 >= 0 && b0 < slots.length) {
				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();

		tank.writeToNBT(nbt);

		for (int i = 0; i < slots.length; i++) {
			if (slots[i] != null) {
				NBTTagCompound nbt1 = new NBTTagCompound();
				nbt1.setByte("slot", (byte) i);
				slots[i].writeToNBT(nbt1);
				list.appendTag(nbt1);
			}
		}
		nbt.setTag("items", list);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return p_94128_1_ == 0 ? slots_bottom : (p_94128_1_ == 1 ? slots_top : slots_side);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemStack, int j) {
		return this.isItemValidForSlot(i, itemStack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemStack, int j) {
		return true;
	}

	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {
			age++;
			if (age >= 20) {
				age = 0;
			}

			if ((age == 9 || age == 19))
				if (dna()) {
					fillFluidInit();
				}
			if (needsUpdate) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				needsUpdate = false;
			}

			if (FFUtils.fillFromFluidContainer(slots, tank, 2, 3))
				needsUpdate = true;
			if(FFUtils.fillFluidContainer(slots, tank, 4, 5))
				needsUpdate = true;

		}
	}



	private void fillFluidInit() {
		if (tank.getFluid() != null) {
			fillFluid(this.xCoord + 2, this.yCoord, this.zCoord - 1);
			fillFluid(this.xCoord + 2, this.yCoord, this.zCoord + 1);
			fillFluid(this.xCoord - 2, this.yCoord, this.zCoord - 1);
			fillFluid(this.xCoord - 2, this.yCoord, this.zCoord + 1);
			fillFluid(this.xCoord - 1, this.yCoord, this.zCoord + 2);
			fillFluid(this.xCoord + 1, this.yCoord, this.zCoord + 2);
			fillFluid(this.xCoord - 1, this.yCoord, this.zCoord - 2);
			fillFluid(this.xCoord + 1, this.yCoord, this.zCoord - 2);
		}

	}

	private void fillFluid(int i, int j, int k) {
		if (tank.getFluidAmount() <= 0) {
			return;
		}

		TileEntity te = this.worldObj.getTileEntity(i, j, k);

		if (te != null && te instanceof IFluidHandler) {
			if (te instanceof TileEntityDummy) {
				TileEntityDummy ted = (TileEntityDummy) te;
				if (this.worldObj.getTileEntity(ted.targetX, ted.targetY, ted.targetZ) == this) {
					return;
				}
			}
			IFluidHandler tef = (IFluidHandler) te;
			tank.drain(tef.fill(ForgeDirection.VALID_DIRECTIONS[1],
					new FluidStack(tank.getFluid(), Math.min(6000, tank.getFluidAmount())), true), true);
			needsUpdate = true;
		}

	}

	public boolean dna() {
		if (slots[6] != null && (slots[6].getItem() == ModItems.fuse || slots[6].getItem() == ModItems.screwdriver))
			return true;
		return false;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (this.canFill(from, resource.getFluid())) {
			needsUpdate = true;
			return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {

		if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		if (this.canDrain(from, resource.getFluid())) {
			needsUpdate = true;
			return tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (this.canDrain(from, null)) {
			needsUpdate = true;
			return tank.drain(maxDrain, doDrain);
		}
		return null;

	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (!this.worldObj.isRemote) {
			return !this.dna();
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (!this.worldObj.isRemote) {
			return this.dna();
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}

	@Override
	public Packet getDescriptionPacket() {

		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);

	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}

}
