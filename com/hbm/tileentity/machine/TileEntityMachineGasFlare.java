package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.entity.particle.EntityGasFlameFX;
import com.hbm.explosion.ExplosionThermo;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IConsumer;
import com.hbm.interfaces.ISource;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.special.ItemBattery;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class TileEntityMachineGasFlare extends TileEntity implements ISidedInventory, ISource, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];
	
	public long power;
	public static final long maxPower = 100000;
	public int age = 0;
	public List<IConsumer> list = new ArrayList<IConsumer>();
	public Fluid tankType;
	public FluidTank tank;
	public boolean needsUpdate;
	
	private static final int[] slots_top = new int[] {1};
	private static final int[] slots_bottom = new int[] {2, 0};
	private static final int[] slots_side = new int[] {0};
	Random rand = new Random();
	
	private String customName;
	
	public TileEntityMachineGasFlare() {
		slots = new ItemStack[3];
		tankType = ModForgeFluids.gas;
		tank = new FluidTank(64000);
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
		return this.hasCustomInventoryName() ? this.customName : "container.gasFlare";
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
	
	//You scrubs aren't needed for anything (right now)
	@Override
	public void openInventory() {}
	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		if(i == 0)
			if(itemStack.getItem() instanceof ItemBattery)
				return true;
		
		if(i == 1)
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
		tankType = ModForgeFluids.gas;
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
		return false;
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	@Override
	public void updateEntity() {
		
		age++;
		if(age >= 20)
			age -= 20;
		if(age == 9 || age == 19)
			ffgeuaInit();
		
		if(!worldObj.isRemote) {
			if(needsUpdate) {
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[] {tank}));
				needsUpdate = false;
			}

			if(this.inputValidForTank(-1, 0))
				if(FFUtils.fillFromFluidContainer(slots, tank, 1, 2))
					needsUpdate = true;
			
			if(tank.getFluidAmount() >= 10) {
				tank.drain(10, true);
				needsUpdate = true;
				power += 50;
				
				if(power > maxPower)
					power = maxPower;

	    		worldObj.spawnEntityInWorld(new EntityGasFlameFX(worldObj, this.xCoord + 0.5F, this.yCoord + 11F, this.zCoord + 0.5F, 0.0, 0.0, 0.0));
				ExplosionThermo.setEntitiesOnFire(worldObj, this.xCoord, this.yCoord + 11, zCoord, 5);
	    		
	    		if(age % 5 == 0)
					this.worldObj.playSoundEffect(this.xCoord, this.yCoord + 11, this.zCoord, "hbm:weapon.flamethrowerShoot", 1.5F, 1F);
			}
			
			power = Library.chargeItemsFromTE(slots, 0, power, maxPower);

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
		return stack.getFluid() == ModForgeFluids.gas;
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
	public boolean getTact() {
		if (age >= 0 && age < 10) {
			return true;
		}

		return false;
	}

	@Override
	public void clearList() {
		this.list.clear();
	}

	@Override
	public void ffgeuaInit() {
		ffgeua(this.xCoord + 2, this.yCoord, this.zCoord, getTact());
		ffgeua(this.xCoord - 2, this.yCoord, this.zCoord, getTact());
		ffgeua(this.xCoord, this.yCoord, this.zCoord + 2, getTact());
		ffgeua(this.xCoord, this.yCoord, this.zCoord - 2, getTact());
		
	}

	@Override
	public void ffgeua(int x, int y, int z, boolean newTact) {
		Library.ffgeua(x, y, z, newTact, this, worldObj);
	}

	@Override
	public long getSPower() {
		return this.power;
	}

	@Override
	public void setSPower(long i) {
		this.power = i;
	}

	@Override
	public List<IConsumer> getList() {
		return this.list;
	}

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 1) {
			return;
		} else {
			tank.readFromNBT(tags[0]);
		}
		
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(this.isValidFluid(resource)) {
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
		return fluid == ModForgeFluids.gas;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] {tank.getInfo()};
	}
}
