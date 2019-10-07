package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IConsumer;
import com.hbm.interfaces.IReactor;
import com.hbm.interfaces.ISource;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.ModItems;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.world.FWatz;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityFWatzCore extends TileEntity implements ISidedInventory,
		IReactor, ISource, IFluidHandler, ITankPacketAcceptor {

	public long power;
	public final static long maxPower = 10000000000L;
	public boolean cooldown = false;

	public FluidTank tanks[];
	public Fluid[] tankTypes;
	public boolean needsUpdate;

	Random rand = new Random();

	private ItemStack slots[];
	public int age = 0;
	public List<IConsumer> list = new ArrayList<IConsumer>();

	private String customName;

	public TileEntityFWatzCore() {
		slots = new ItemStack[7];
		tanks = new FluidTank[3];
		tankTypes = new Fluid[3];
		tanks[0] = new FluidTank(128000);
		tankTypes[0] = ModForgeFluids.coolant;
		tanks[1] = new FluidTank(64000);
		tankTypes[1] = ModForgeFluids.amat;
		tanks[2] = new FluidTank(64000);
		tankTypes[2] = ModForgeFluids.aschrab;
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
		return this.hasCustomInventoryName() ? this.customName
				: "container.fusionaryWatzPlant";
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
			return true;
		}
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		return true;
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
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return null;
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
			int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
			int p_102008_3_) {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("items", 10);

		power = nbt.getLong("power");

		slots = new ItemStack[getSizeInventory()];

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			byte b0 = nbt1.getByte("slot");
			if (b0 >= 0 && b0 < slots.length) {
				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
			}
		}
		NBTTagList tankList = nbt.getTagList("tanks", 10);
		for (int i = 0; i < tankList.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte b0 = tag.getByte("tank");
			if (b0 >= 0 && b0 < tanks.length) {
				tanks[b0].readFromNBT(tag);
			}
		}
		tankTypes[0] = ModForgeFluids.coolant;
		tankTypes[1] = ModForgeFluids.amat;
		tankTypes[2] = ModForgeFluids.aschrab;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setLong("power", power);

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
	}

	@Override
	public boolean isStructureValid(World world) {
		return FWatz.checkHull(world, this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public boolean isCoatingValid(World world) {
		{
			return true;
		}
	}

	@Override
	public boolean hasFuse() {
		return slots[1] != null
				&& (slots[1].getItem() == ModItems.fuse || slots[1].getItem() == ModItems.screwdriver);
	}

	@Override
	public int getCoolantScaled(int i) {
		return 0;
	}

	@Override
	public long getPowerScaled(long i) {
		return (power / 100 * i) / (maxPower / 100);
	}

	@Override
	public int getWaterScaled(int i) {
		return 0;
	}

	@Override
	public int getHeatScaled(int i) {
		return 0;
	}

	public int getSingularityType() {

		if (slots[2] != null) {
			Item item = slots[2].getItem();

			if (item == ModItems.singularity)
				return 1;
			if (item == ModItems.singularity_counter_resonant)
				return 2;
			if (item == ModItems.singularity_super_heated)
				return 3;
			if (item == ModItems.black_hole)
				return 4;
			if (item == ModItems.overfuse)
				return 5;
		}

		return 0;
	}

	@Override
	public void updateEntity() {
		if (this.isStructureValid(this.worldObj) && !worldObj.isRemote) {

			age++;
			if (age >= 20) {
				age = 0;
			}

			if (age == 9 || age == 19)
				ffgeuaInit();
			if (!worldObj.isRemote) {
				if (hasFuse() && getSingularityType() > 0) {
					if (cooldown) {

						int i = getSingularityType();

						if (i == 1)
							tanks[0].fill(new FluidStack(tankTypes[0], 1500),
									true);
						if (i == 2)
							tanks[0].fill(new FluidStack(tankTypes[0], 3000),
									true);
						if (i == 3)
							tanks[0].fill(new FluidStack(tankTypes[0], 750),
									true);
						if (i == 4)
							tanks[0].fill(new FluidStack(tankTypes[0], 500),
									true);
						if (i == 5)
							tanks[0].fill(new FluidStack(tankTypes[0], 15000),
									true);

						if (tanks[0].getFluidAmount() >= tanks[0].getCapacity()) {
							cooldown = false;
						}

					} else {
						int i = getSingularityType();

						if (i == 1 && tanks[1].getFluidAmount() - 75 >= 0
								&& tanks[2].getFluidAmount() - 75 >= 0) {
							tanks[0].drain(150, true);
							tanks[1].drain(75, true);
							tanks[2].drain(75, true);
							needsUpdate = true;
							power += 5000000;
						}
						if (i == 2 && tanks[1].getFluidAmount() - 75 >= 0
								&& tanks[2].getFluidAmount() - 35 >= 0) {
							tanks[0].drain(75, true);
							tanks[1].drain(35, true);
							tanks[2].drain(30, true);
							needsUpdate = true;
							power += 2500000;
						}
						if (i == 3 && tanks[1].getFluidAmount() - 75 >= 0
								&& tanks[2].getFluidAmount() - 140 >= 0) {
							tanks[0].drain(300, true);
							tanks[1].drain(75, true);
							tanks[2].drain(140, true);
							needsUpdate = true;
							power += 10000000;
						}
						if (i == 4 && tanks[1].getFluidAmount() - 100 >= 0
								&& tanks[2].getFluidAmount() - 100 >= 0) {
							tanks[0].drain(100, true);
							tanks[1].drain(100, true);
							tanks[2].drain(100, true);
							needsUpdate = true;
							power += 10000000;
						}
						if (i == 5 && tanks[1].getFluidAmount() - 15 >= 0
								&& tanks[2].getFluidAmount() - 15 >= 0) {
							tanks[0].drain(150, true);
							tanks[1].drain(15, true);
							tanks[2].drain(15, true);
							needsUpdate = true;
							power += 100000000;
						}

						if (power > maxPower)
							power = maxPower;

						if (tanks[0].getFluidAmount() <= 0) {
							cooldown = true;
						}
					}
				}

				if (power > maxPower)
					power = maxPower;

				power = Library.chargeItemsFromTE(slots, 0, power, maxPower);

				if(this.inputValidForTank(1, 3))
					if(FFUtils.fillFromFluidContainer(slots, tanks[1], 3, 5))
						needsUpdate = true;
				if(this.inputValidForTank(2, 4))
					if(FFUtils.fillFromFluidContainer(slots, tanks[2], 4, 6))
						needsUpdate = true;
				if (needsUpdate) {
					PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(
							xCoord, yCoord, zCoord, new FluidTank[] { tanks[0],
									tanks[1], tanks[2] }));
					needsUpdate = false;
				}
			}

		}

		if (this.isRunning()
				&& (tanks[1].getFluidAmount() <= 0
						|| tanks[2].getFluidAmount() <= 0 || !hasFuse() || getSingularityType() == 0)
				|| cooldown || !this.isStructureValid(worldObj))
			this.emptyPlasma();

		if (!this.isRunning() && tanks[1].getFluidAmount() >= 100
				&& tanks[2].getFluidAmount() >= 100 && hasFuse()
				&& getSingularityType() > 0 && !cooldown
				&& this.isStructureValid(worldObj))
			this.fillPlasma();

		PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord,
				yCoord, zCoord, power));
	}

	public void fillPlasma() {
		if (!this.worldObj.isRemote)
			FWatz.fillPlasma(worldObj, this.xCoord, this.yCoord, this.zCoord);
	}

	public void emptyPlasma() {
		if (!this.worldObj.isRemote)
			FWatz.emptyPlasma(worldObj, this.xCoord, this.yCoord, this.zCoord);
	}

	public boolean isRunning() {
		return FWatz.getPlasma(worldObj, this.xCoord, this.yCoord, this.zCoord)
				&& this.isStructureValid(worldObj);
	}

	protected boolean inputValidForTank(int tank, int slot){
		if(slots[slot] != null && tanks[tank] != null){
			if(slots[slot].getItem() instanceof IFluidContainerItem && isValidFluidForTank(tank, ((IFluidContainerItem)slots[slot].getItem()).getFluid(slots[slot]))){
				return true;
			}
			if(FluidContainerRegistry.isFilledContainer(slots[slot]) && isValidFluidForTank(tank, FluidContainerRegistry.getFluidForFilledItem(slots[slot]))){
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidFluidForTank(int tank, FluidStack stack) {
		if(stack == null || tanks[tank] == null)
			return false;
		return stack.getFluid() == tankTypes[tank];
	}
	@Override
	public void ffgeua(int x, int y, int z, boolean newTact) {

		Library.ffgeua(x, y, z, newTact, this, worldObj);
	}

	@Override
	public void ffgeuaInit() {
		ffgeua(this.xCoord + 10, this.yCoord - 11, this.zCoord, getTact());
		ffgeua(this.xCoord - 10, this.yCoord - 11, this.zCoord, getTact());
		ffgeua(this.xCoord, this.yCoord - 11, this.zCoord + 10, getTact());
		ffgeua(this.xCoord, this.yCoord - 11, this.zCoord - 10, getTact());
	}

	@Override
	public boolean getTact() {
		if (age >= 0 && age < 10) {
			return true;
		}

		return false;
	}

	@Override
	public long getSPower() {
		return power;
	}

	@Override
	public void setSPower(long i) {
		this.power = i;
	}

	@Override
	public List<IConsumer> getList() {
		return list;
	}

	@Override
	public void clearList() {
		this.list.clear();
	}

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if (tags.length != 3) {
			return;
		} else {
			tanks[0].readFromNBT(tags[0]);
			tanks[1].readFromNBT(tags[1]);
			tanks[2].readFromNBT(tags[2]);
		}

	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(resource == null){
			return 0;
		} else if(resource.getFluid() == tankTypes[0]){
			needsUpdate = true;
			return tanks[0].fill(resource, doFill);
		} else if(resource.getFluid() == tankTypes[1]){
			needsUpdate = true;
			return tanks[1].fill(resource, doFill);
		} else if(resource.getFluid() == tankTypes[2]){
			needsUpdate = true;
			return tanks[2].fill(resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == tankTypes[0] || fluid == tankTypes[1] || fluid == tankTypes[2];
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tanks[0].getInfo(), tanks[1].getInfo(), tanks[2].getInfo()};
	}
}
