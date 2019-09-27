package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.machine.MachineBoiler;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IHBMFluidHandler;
import com.hbm.inventory.MachineRecipes;
import com.hbm.lib.Library;
import com.hbm.packet.AuxGaugePacket;
import com.hbm.packet.PacketDispatcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMachineBoiler extends TileEntity implements ISidedInventory, IFluidHandler {

	private ItemStack slots[];

	public int burnTime;
	public int heat = 2000;
	public static final int maxHeat = 50000;
	public int age = 0;
	public FluidTank[] tanks;

	private static final int[] slots_top = new int[] { 4 };
	private static final int[] slots_bottom = new int[] { 6 };
	private static final int[] slots_side = new int[] { 4 };

	private String customName;

	private boolean needsUpdate = false;

	//TODO Something is really messed up with this and fluid pipes.
	public TileEntityMachineBoiler() {
		slots = new ItemStack[7];
		tanks = new FluidTank[2];
		tanks[0] = new FluidTank(8000);
		tanks[1] = new FluidTank(8000);
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
		return this.hasCustomInventoryName() ? this.customName : "container.machineBoiler";
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

	// You scrubs aren't needed for anything (right now)
	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {

		if (i == 4)
			if (TileEntityFurnace.getItemBurnTime(stack) > 0)
				return true;

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
		NBTTagList tanksList = nbt.getTagList("tanks", 10);

		heat = nbt.getInteger("heat");
		burnTime = nbt.getInteger("burnTime");
		for (int i = 0; i < tanksList.tagCount(); i++) {
			NBTTagCompound nbt1 = tanksList.getCompoundTagAt(i);
			byte b0 = nbt1.getByte("tank");
			if (b0 >= 0 && b0 < tanks.length) {
				tanks[i].readFromNBT(nbt1);
				// System.out.println(nbt1);
			}
		}

		slots = new ItemStack[getSizeInventory()];

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
		nbt.setInteger("heat", heat);
		nbt.setInteger("burnTime", burnTime);
		NBTTagList tankList = new NBTTagList();

		for (int i = 0; i < tanks.length; i++) {
			if (tanks[i] != null) {
				NBTTagCompound tankInfo = new NBTTagCompound();
				tankInfo.setByte("tank", (byte) i);
				tanks[i].writeToNBT(tankInfo);
				tankList.appendTag(tankInfo);
			}

		}
		nbt.setTag("tanks", tankList);
		NBTTagList list = new NBTTagList();

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
		return false;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / maxHeat;
	}

	@Override
	public void updateEntity() {

		boolean mark = false;

		if (!worldObj.isRemote) {
			age++;
			if (age >= 20) {
				age = 0;
			}

			if (age == 9 || age == 19)
				fillFluidInit(tanks[1]);

			Object[] outs;
			if (tanks[0].getFluid() != null) {
				outs = MachineRecipes.getBoilerOutput(tanks[0].getFluid().getFluid());
			} else {
				outs = MachineRecipes.getBoilerOutput(null);
			}
			if(FFUtils.fillFromFluidContainer(slots, tanks[0], 2, 3))
				needsUpdate = true;

			if(FFUtils.fillFluidContainer(slots, tanks[1], 4, 5))
				needsUpdate = true;

			if (needsUpdate) {
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				needsUpdate = false;
			}

			boolean flag1 = false;

			if (heat > 2000) {
				heat -= 15;
			}

			if (burnTime > 0) {
				burnTime--;
				heat += 50;
				flag1 = true;
			}

			if (burnTime == 0 && flag1) {
				mark = true;
			}

			if (burnTime > 0 && worldObj.getBlock(xCoord, yCoord, zCoord) == ModBlocks.machine_boiler_on)
				MachineBoiler.updateBlockState(false, worldObj, xCoord, yCoord, zCoord);

			if (heat > maxHeat)
				heat = maxHeat;

			if (burnTime == 0 && TileEntityFurnace.getItemBurnTime(slots[4]) > 0) {
				burnTime = (int) (TileEntityFurnace.getItemBurnTime(slots[4]) * 0.25);
				slots[4].stackSize--;

				if (slots[4].stackSize <= 0) {

					if (slots[4].getItem().getContainerItem() != null)
						slots[4] = new ItemStack(slots[4].getItem().getContainerItem());
					else
						slots[4] = null;
				}

				if (!flag1) {
					mark = true;
				}
			}

			if (burnTime > 0 && worldObj.getBlock(xCoord, yCoord, zCoord) == ModBlocks.machine_boiler_off)
				MachineBoiler.updateBlockState(true, worldObj, xCoord, yCoord, zCoord);

			if (outs != null) {

				for (int i = 0; i < (heat / ((Integer) outs[3]).intValue()); i++) {
					if (tanks[0].getFluidAmount() >= ((Integer) outs[2]).intValue()
							&& tanks[1].getFluidAmount() + ((Integer) outs[1]).intValue() <= tanks[1].getCapacity()) {
						tanks[0].drain((Integer) outs[2], true);
						tanks[1].fill(new FluidStack((Fluid) outs[0], (Integer) outs[1]), true);

						if (i == 0)
							heat -= 25;
						else
							heat -= 40;
					}
				}
			}

			if (heat < 2000) {
				heat = 2000;
			}

			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, heat, 0));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, burnTime, 1));
		}

		if (mark) {
			this.markDirty();
		}
	}

	public boolean isItemValid() {

		if (slots[1] != null && TileEntityFurnace.getItemBurnTime(slots[1]) > 0) {
			return true;
		}

		return false;
	}

	public void fillFluidInit(FluidTank tank) {
		boolean update = needsUpdate;
		
		update = FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord, 2000) || update;
		update = FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord, 2000) || update;
		update = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord - 1, this.zCoord, 2000) || update;
		update = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord + 1, this.zCoord, 2000) || update;
		update = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 1, 2000) || update;
		update = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 1, 2000) || update;

		needsUpdate = update;
	}


	public boolean getTact() {
		if (age >= 0 && age < 10) {
			return true;
		}

		return false;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (isValidFluid(resource)) {
			needsUpdate = true;
			return tanks[0].fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null || !resource.isFluidEqual(tanks[1].getFluid())) {
			return null;
		}
		needsUpdate = true;
		return tanks[1].drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		needsUpdate = true;
		return tanks[1].drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return tanks[0].getFluidAmount() < 8000;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return tanks[1].getFluidAmount() != 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] { tanks[0].getInfo(), tanks[1].getInfo() };
	}

	private boolean isValidFluid(FluidStack stack) {
		return stack.getFluid() == FluidRegistry.WATER || stack.getFluid() == ModForgeFluids.oil
				|| stack.getFluid() == ModForgeFluids.steam || stack.getFluid() == ModForgeFluids.hotsteam;
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
