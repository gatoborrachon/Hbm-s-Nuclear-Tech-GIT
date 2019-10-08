package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IConsumer;
import com.hbm.interfaces.ISource;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.inventory.MachineRecipes;
import com.hbm.items.special.ItemBattery;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.FluidTypePacketTest;
import com.hbm.packet.PacketDispatcher;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMachineTurbine extends TileEntity implements ISidedInventory, IFluidHandler, ITankPacketAcceptor, ISource {

	private ItemStack slots[];

	public long power;
	public static final long maxPower = 1000000;
	public int age = 0;
	public List<IConsumer> list1 = new ArrayList<IConsumer>();
	public FluidTank[] tanks;
	public Fluid[] tankTypes;
	public boolean needsUpdate;
	
	private static final int[] slots_top = new int[] {4};
	private static final int[] slots_bottom = new int[] {6};
	private static final int[] slots_side = new int[] {4};
	
	private String customName;
	
	public TileEntityMachineTurbine() {
		slots = new ItemStack[7];
		tanks = new FluidTank[2];
		tankTypes = new Fluid[2];
		tanks[0] = new FluidTank(64000);
		tankTypes[0] = ModForgeFluids.steam;
		tanks[1] = new FluidTank(128000);
		tankTypes[1] = FluidRegistry.WATER;
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
		return this.hasCustomInventoryName() ? this.customName : "container.machineTurbine";
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
	
	//You scrubs aren't needed for anything (right now)
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		
		if(i == 4)
			if(stack != null && stack.getItem() instanceof ItemBattery)
				return true;
		
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

		NBTTagList tankList = nbt.getTagList("tanks", 10);
		for(int i = 0; i < tankList.tagCount(); i ++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte b0 = tag.getByte("tank");
			if(b0 >= 0 && b0 < tanks.length){
				tanks[b0].readFromNBT(tag);
			}
		}
		tankTypes[0] = FluidRegistry.getFluid(nbt.getInteger("tankType0"));
		tankTypes[1] = FluidRegistry.getFluid(nbt.getInteger("tankType1"));
		
		power = nbt.getLong("power");
		
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
		
		NBTTagList tankList = new NBTTagList();
		for(int i = 0; i < tanks.length; i ++){
			if(tanks[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("tank", (byte)i);
				tanks[i].writeToNBT(tag);
				tankList.appendTag(tag);
			}
		}
		nbt.setTag("tanks", tankList);
		nbt.setInteger("tankType0", FluidRegistry.getFluidID(tankTypes[0]));
		nbt.setInteger("tankType1", FluidRegistry.getFluidID(tankTypes[1]));
		
		nbt.setLong("power", power);
		
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
		return false;
	}
	
	public long getPowerScaled(int i) {
		return (power * i) / maxPower;
	}
	
	@Override
	public void updateEntity() {
		if(!worldObj.isRemote)
		{
			age++;
			if(age >= 2)
			{
				age = 0;
			}
			
			fillFluidInit(tanks[1]);
			ffgeuaInit();

			if(inputValidForTank(0, 2))
				if(FFUtils.fillFromFluidContainer(slots, tanks[0], 2, 3)) {
					if(tanks[0].getFluid() != null){
						tankTypes[0] = tanks[0].getFluid().getFluid();
					}
					needsUpdate = true;
				}
			
			Object[] outs = MachineRecipes.getTurbineOutput(tanks[0].getFluid() == null ? null : tanks[0].getFluid().getFluid());
			
			if(outs == null) {
				
			} else {
				tankTypes[1] = ((Fluid) outs[0]);
				
				for(int i = 0; i < 1200; i++) {
					if(tanks[0].getFluidAmount() >= (Integer)outs[2] && tanks[1].getFluidAmount() + (Integer)outs[1] <= tanks[1].getCapacity()) {
						tanks[0].drain((Integer)outs[2], true);
						
						// Empty tank if it doesn't match the machine recipe
						if(tanks[1].getFluid() != null && tanks[1].getFluid().getFluid() != tankTypes[1]) {
							tanks[1].drain(tanks[1].getCapacity(), true);
						}
						
						tanks[1].fill(new FluidStack(tankTypes[1], (Integer)outs[1]), true);
						
						power += (Integer)outs[3];
						
						if(power > maxPower)
							power = maxPower;
						needsUpdate = true;
					} else {
						break;
					}
				}
			}
			
			if(FFUtils.fillFluidContainer(slots, tanks[1], 5, 6))
				needsUpdate = true;
			
			if(needsUpdate){
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[]{tanks[0], tanks[1]}));
				PacketDispatcher.wrapper.sendToAll(new FluidTypePacketTest(xCoord, yCoord, zCoord, tankTypes));
				needsUpdate = false;
			}
			
			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
		}
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
		return stack.getFluid() == ModForgeFluids.steam || stack.getFluid() == ModForgeFluids.hotsteam || stack.getFluid() == ModForgeFluids.superhotsteam;
	}

	@Override
	public void ffgeua(int x, int y, int z, boolean newTact) {
		
		Library.ffgeua(x, y, z, newTact, this, worldObj);
	}

	@Override
	public boolean getTact() {
		if(age == 0)
		{
			return true;
		}
		
		return false;
	}
	@Override
	public void ffgeuaInit() {
		ffgeua(this.xCoord, this.yCoord + 1, this.zCoord, getTact());
		ffgeua(this.xCoord, this.yCoord - 1, this.zCoord, getTact());
		ffgeua(this.xCoord - 1, this.yCoord, this.zCoord, getTact());
		ffgeua(this.xCoord + 1, this.yCoord, this.zCoord, getTact());
		ffgeua(this.xCoord, this.yCoord, this.zCoord - 1, getTact());
		ffgeua(this.xCoord, this.yCoord, this.zCoord + 1, getTact());
	}

	public void fillFluidInit(FluidTank tank) {
		
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord, 4000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord, 4000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord + 1, this.zCoord, 4000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord - 1, this.zCoord, 4000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 1, 4000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 1, 4000) || needsUpdate;
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
		return list1;
	}

	@Override
	public void clearList() {
		this.list1.clear();
	}

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 2){
			return;
		} else {
			tanks[0].readFromNBT(tags[0]);
			tanks[1].readFromNBT(tags[1]);
		}
		
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(isValidFluidForTank(0, resource) && resource.amount > 0){
			needsUpdate = true;
			if(tanks[0].getFluid() != null) {
				tankTypes[0] = tanks[0].getFluid().getFluid();
			}
			return tanks[0].fill(resource, doFill);
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource != null && resource.getFluid() == tankTypes[1] && resource.amount > 0) {
			needsUpdate = true;
			return tanks[1].drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(maxDrain > 0) {
			needsUpdate = true;
			return tanks[1].drain(maxDrain, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == ModForgeFluids.hotsteam || fluid == ModForgeFluids.steam || fluid == ModForgeFluids.superhotsteam;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == tankTypes[1];
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {tanks[0].getInfo(), tanks[1].getInfo()};
	}

}
