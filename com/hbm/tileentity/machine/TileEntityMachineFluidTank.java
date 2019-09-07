package com.hbm.tileentity.machine;


import org.lwjgl.opengl.GL11;

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
	
	//public static final int maxFill = 64 * 3;
	public FluidTank tank;

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {0};
	private static final int[] slots_side = new int[] {0};
	public int age = 0;
	public boolean needsUpdate = false;

	
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
		
		tank.readFromNBT(nbt);
		
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
		
		tank.writeToNBT(nbt);
		
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
		return true;
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
			
			if((age == 9 || age == 19))
				if(dna()){
				fillFluidInit();
				}
				if(needsUpdate){
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				needsUpdate = false;
				}
				if(slots[2] != null){
					if(slots[2].getItem() instanceof IFluidContainerItem){
				
				tank.fill(((IFluidContainerItem)slots[2].getItem()).drain(slots[2], Math.min(6000, tank.getCapacity() - tank.getFluidAmount()), true), true);
				needsUpdate = true;
					if(((IFluidContainerItem)slots[2].getItem()).getFluid(slots[2]) == null && slots[3] == null){
						MoveItems(2, 3);
						}
					} else if(FluidContainerRegistry.isContainer(slots[2]) && !FluidContainerRegistry.isEmptyContainer(slots[2])){
						if(tank.getFluid() == null || (tank.getFluid().getFluid() == ((FluidContainerRegistry.getFluidForFilledItem(slots[2])).getFluid()) && (tank.getCapacity() - tank.getFluidAmount()) >= FluidContainerRegistry.getContainerCapacity(slots[2]))){
							if(slots[3] == null || (slots[3].getItem() == FluidContainerRegistry.drainFluidContainer(slots[2]).getItem() && slots[3].stackSize < slots[3].getMaxStackSize())){
							tank.fill(FluidContainerRegistry.getFluidForFilledItem(slots[2]), true);
							
							if(slots[3] == null){
								slots[3] = FluidContainerRegistry.drainFluidContainer(slots[2]);
							}else{
								slots[3].stackSize++;
							}
							if(slots[2].stackSize > 1){
								slots[2].stackSize--;
								}else{
									slots[2] = null;
								}
							needsUpdate = true;
							}
							
						}
					}
				}
				if(slots[4] != null&& tank.getFluid() != null){
					if(slots[4].getItem() instanceof IFluidContainerItem){
					tank.drain(((IFluidContainerItem)slots[4].getItem()).fill(slots[4],  new FluidStack(tank.getFluid(), Math.min(6000, tank.getFluidAmount())), true), true);
					//System.out.println(tank.getFluid().getFluid().getStillIcon());
					needsUpdate = true;
						if(((IFluidContainerItem)slots[4].getItem()).getFluid(slots[4]) != null &&((IFluidContainerItem)slots[4].getItem()).getFluid(slots[4]).amount == ((IFluidContainerItem)slots[4].getItem()).getCapacity(slots[4]) && slots[5] == null){
							MoveItems(4, 5);
						}
					} else if(FluidContainerRegistry.isContainer(slots[4]) && FluidContainerRegistry.isEmptyContainer(slots[4])){
						if(tank.getFluid() != null && tank.getFluidAmount() >= FluidContainerRegistry.getContainerCapacity(slots[4])){
							if(slots[5] == null || (slots[5].getItem() == FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[4]).getItem() && slots[5].stackSize < slots[5].getMaxStackSize())){
								if(FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[4]) != null){
								if(slots[5] == null){
									slots[5] = FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[4]);
									tank.drain(FluidContainerRegistry.getContainerCapacity(FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[4])), true);
									
								}else{
									slots[5].stackSize++;
									tank.drain(FluidContainerRegistry.getContainerCapacity(FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[4])), true);
									
								}
								if(slots[4].stackSize > 1){
									slots[4].stackSize--;
								}else{
									slots[4] = null;
								}

								needsUpdate = true;
							}
							}
							
						}
					}
				}
				
		}
	}
	


	private void MoveItems(int slot1, int slot2) {
		if(slots[slot1] != null){
		if(slots[slot2] == null){
			ItemStack temp = slots[slot1];
			slots[slot1] = null;
			slots[slot2] = temp;
		} else if(slots[slot2].getItem() == slots[slot1].getItem() && slots[slot2].isStackable() && slots[slot2].getMaxStackSize() > slots[slot2].stackSize){
			slots[slot1] = null;
			slots[slot2].stackSize ++;
		}
		}
	}


	private void fillFluidInit() {
		if(tank.getFluid() != null){
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
		if(tank.getFluidAmount() <= 0){
			return;
		}
		
		TileEntity te = this.worldObj.getTileEntity(i, j, k);
		
		if(te !=null && te instanceof IFluidHandler){
			if(te instanceof TileEntityDummy){
				TileEntityDummy ted = (TileEntityDummy)te;
				if(this.worldObj.getTileEntity(ted.targetX, ted.targetY, ted.targetZ) == this){
					return;
				}
			}
			IFluidHandler tef = (IFluidHandler)te;
			tank.drain(tef.fill(ForgeDirection.VALID_DIRECTIONS[1], new FluidStack(tank.getFluid(), Math.min(6000, tank.getFluidAmount())), true), true);
			needsUpdate = true;
		}
		
	}

	public boolean dna() {
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



	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(this.canFill(from, resource.getFluid())){
		needsUpdate = true;
		return tank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		
		if (resource == null || !resource.isFluidEqual(tank.getFluid()))
        {
            return null;
        }
		if(this.canDrain(from, resource.getFluid())){
        needsUpdate = true;
        return tank.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(this.canDrain(from, null)){
		needsUpdate = true;
		return tank.drain(maxDrain, doDrain);
		}
		return null;
		
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if(!this.worldObj.isRemote){
		return !this.dna();
		}
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if(!this.worldObj.isRemote){
		return this.dna();
		}
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };
	}
	
    @Override
    public Packet getDescriptionPacket(){
 
    	NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
        
    	
    }
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
    	readFromNBT(pkt.func_148857_g());
    }
}
