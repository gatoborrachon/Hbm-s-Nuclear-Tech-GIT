package com.hbm.tileentity.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IConsumer;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBattery;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;

public class TileEntityMachineCMBFactory extends TileEntity implements ISidedInventory, IConsumer, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];
	
	public long power = 0;
	public int process = 0;
	public int soundCycle = 0;
	public static final long maxPower = 100000000;
	public static final int processSpeed = 200;
	public FluidTank tank;
	public Fluid tankType = ModForgeFluids.watz;
	public boolean needsUpdate = false;

	private static final int[] slots_top = new int[] {1, 3};
	private static final int[] slots_bottom = new int[] {0, 2, 4};
	private static final int[] slots_side = new int[] {0, 2};
	
	private String customName;
	
	public TileEntityMachineCMBFactory() {
		slots = new ItemStack[6];
		tank = new FluidTank(8000);
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
		return this.hasCustomInventoryName() ? this.customName : "container.machineCMB";
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
		switch(i)
		{
		case 0:
			if(stack.getItem() instanceof ItemBattery)
				return true;
			break;
		case 1:
			if(stack.getItem() == ModItems.ingot_magnetized_tungsten || stack.getItem() == ModItems.powder_magnetized_tungsten)
				return true;
			break;
		case 2:
			if(stack.getItem() == ModItems.bucket_mud || (stack.getItem() == ModItems.tank_waste && stack.getItemDamage() > 0))
				return true;
			break;
		case 3:
			if(stack.getItem() == ModItems.ingot_advanced_alloy || stack.getItem() == ModItems.powder_advanced_alloy)
				return true;
			break;
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
		
		power = nbt.getLong("power");
		tank.readFromNBT(nbt);
		process = nbt.getShort("process");
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
		nbt.setLong("power", power);
		tank.writeToNBT(nbt);
		nbt.setShort("process", (short) process);
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
		if(i == 4)
			return true;
		if(i == 0)
			if (itemStack.getItem() instanceof ItemBattery && ItemBattery.getCharge(itemStack) == 0)
				return true;
		if(i == 2)
			if(itemStack.getItem() == Items.bucket || (itemStack.getItem() == ModItems.tank_waste && itemStack.getItemDamage() <= 0))
				return true;
		
		return false;
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	public int getProgressScaled(int i) {
		return (process * i) / processSpeed;
	}
	
	public boolean canProcess() {
		
		boolean b = false;
		
		if(tank.getFluidAmount() >= 10 && power >= 100000 && slots[1] != null && slots[3] != null && (slots[4] == null || slots[4].stackSize <= 60))
		{
			boolean flag0 = slots[1].getItem() == ModItems.ingot_magnetized_tungsten || slots[1].getItem() == ModItems.powder_magnetized_tungsten;
			boolean flag1 = slots[3].getItem() == ModItems.ingot_advanced_alloy || slots[3].getItem() == ModItems.powder_advanced_alloy;
			
			b = flag0 && flag1;
		}
		
		return  b;
	}
	
	public boolean isProcessing() {
		return process > 0;
	}
	
	public void process() {
		tank.drain(10, true);
		needsUpdate = true;
		power -= 100000;
		
		process++;
		
		if(process >= processSpeed) {
			
			slots[1].stackSize--;
			if (slots[1].stackSize == 0) {
				slots[1] = null;
			}

			slots[3].stackSize--;
			if (slots[3].stackSize == 0) {
				slots[3] = null;
			}
			
			if(slots[4] == null)
			{
				slots[4] = new ItemStack(ModItems.ingot_combine_steel, 4);
			} else {
				
				slots[4].stackSize += 4;
			}
			
			process = 0;
		}
	}
	
	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {
			if (needsUpdate) {
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[] {tank}));
				needsUpdate = false;
			}
			power = Library.chargeTEFromItems(slots, 0, power, maxPower);
			
			if(this.inputValidForTank(-1, 2))
				if(FFUtils.fillFromFluidContainer(slots, tank, 2, 5))
					needsUpdate = true;

			if (canProcess()) {
				process();
				if(soundCycle == 0)
			        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "minecart.base", 1.0F, 1.5F);
				soundCycle++;
					
				if(soundCycle >= 25)
					soundCycle = 0;
			} else {
				process = 0;
			}

			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
		}
	}
	
	protected boolean inputValidForTank(int tank, int slot){
		if(slots[slot] != null){
			if(slots[slot].getItem() instanceof IFluidContainerItem && isValidFluid(((IFluidContainerItem)slots[slot].getItem()).getFluid(slots[slot]))){
				return true;	
			}
			if(FluidContainerRegistry.isFilledContainer(slots[slot]) && isValidFluid(FluidContainerRegistry.getFluidForFilledItem(slots[slot]))){
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidFluid(FluidStack stack) {
		if(stack == null)
			return false;
		return stack.getFluid() == ModForgeFluids.watz;
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

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (isValidFluid(resource)) {
			if(tank.fill(resource, false) > 0)
				needsUpdate = true;
			return tank.fill(resource, doFill);
		}
		return 0;
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
		return true;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {

		return tank.getFluidAmount() != 0;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {

		return new FluidTankInfo[] {tank.getInfo()};
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

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 1) {
			return;
		} else {
			tank.readFromNBT(tags[0]);
		}
		
	}
}
