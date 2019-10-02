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

import java.util.ArrayList;
import java.util.List;

import com.hbm.blocks.machine.MachineCoal;
import com.hbm.forgefluid.FFUtils;
import com.hbm.interfaces.IConsumer;
import com.hbm.interfaces.ISource;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBattery;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.AuxGaugePacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;

public class TileEntityMachineCoal extends TileEntity implements ISidedInventory, ISource, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];
	
	public long power;
	public int burnTime;
	public static final long maxPower = 100000;
	public int age = 0;
	public List<IConsumer> list = new ArrayList<IConsumer>();
	public FluidTank tank;
	public Fluid tankType = FluidRegistry.WATER;
	public boolean needsUpdate = false;
	
	private static final int[] slots_top = new int[] {1};
	private static final int[] slots_bottom = new int[] {0, 2};
	private static final int[] slots_side = new int[] {0, 2};
	
	private String customName;
	
	public TileEntityMachineCoal() {
		slots = new ItemStack[4];
		tank = new FluidTank(5000);
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
		return this.hasCustomInventoryName() ? this.customName : "container.machineCoal";
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
		if(i == 0)
			if(FluidContainerRegistry.getFluidForFilledItem(stack) != null && FluidContainerRegistry.getFluidForFilledItem(stack).getFluid() == FluidRegistry.WATER)
				return true;
		if(i == 2)
			if(stack.getItem() instanceof ItemBattery)
				return true;
		if(i == 1)
			if(TileEntityFurnace.getItemBurnTime(stack) > 0)
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
		
		this.power = nbt.getLong("powerTime");
		tank.readFromNBT(nbt);
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
		nbt.setLong("powerTime", power);
		tank.writeToNBT(nbt);
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
			if(itemStack.getItem() == Items.bucket || itemStack.getItem() == ModItems.rod_empty || itemStack.getItem() == ModItems.rod_dual_empty || itemStack.getItem() == ModItems.rod_quad_empty)
				return true;
		if(i == 2)
			if (itemStack.getItem() instanceof ItemBattery && ItemBattery.getCharge(itemStack) == ItemBattery.getMaxChargeStatic(itemStack))
				return true;
		
		return false;
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	@Override
	public void updateEntity() {
		age++;
		if(age >= 20)
		{
			age = 0;
		}
		
		if(age == 9 || age == 19)
			ffgeuaInit();
		
		if(!worldObj.isRemote)
		{
			if (needsUpdate) {
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[] {tank}));
				needsUpdate = false;
			}
			//Water
			if(this.inputValidForTank(-1, 0))
				if(FFUtils.fillFromFluidContainer(slots, tank, 0, 3))
					needsUpdate = true;

			//Battery Item
			power = Library.chargeItemsFromTE(slots, 2, power, maxPower);
			
			boolean trigger = true;
			
			if(isItemValid() && this.burnTime == 0)
			{
				trigger = false;
			}
			
			if(trigger)
            {
                MachineCoal.updateBlockState(this.burnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
			
			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, burnTime, 0));
			
			generate();
		}
	}
	
	public void generate() {
		
		if(slots[1] != null && TileEntityFurnace.getItemBurnTime(slots[1]) > 0 && burnTime <= 0)
		{
			burnTime = TileEntityFurnace.getItemBurnTime(slots[1]) / 2;
			slots[1].stackSize -= 1;
			if(slots[1].stackSize == 0)
			{
				if(slots[1].getItem().getContainerItem() != null)
					slots[1] = new ItemStack(slots[1].getItem().getContainerItem());
				else
					slots[1] = null;
			}
		}
		
		if(burnTime > 0)
		{
			burnTime--;
			
			if(tank.getFluidAmount() > 0)
			{
				tank.drain(1, true);
				needsUpdate = true;
				
				if(power + 25 <= maxPower)
				{
					power += 25;
				} else {
					power = maxPower;
				}
			}
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
		return stack.getFluid() == FluidRegistry.WATER;
	}
	
	public boolean isItemValid() {

		if(slots[1] != null && TileEntityFurnace.getItemBurnTime(slots[1]) > 0)
		{
			return true;
		}
		
		return false;
	}

	@Override
	public void ffgeua(int x, int y, int z, boolean newTact) {
		
		Library.ffgeua(x, y, z, newTact, this, worldObj);
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
	
	@Override
	public boolean getTact() {
		if(age >= 0 && age < 10)
		{
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
