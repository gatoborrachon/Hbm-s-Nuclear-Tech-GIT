package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.entity.particle.EntityGasFlameFX;
import com.hbm.forgefluid.FFUtils;
import com.hbm.interfaces.IConsumer;
import com.hbm.inventory.MachineRecipes;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemBattery;
import com.hbm.items.tool.ItemChemistryTemplate;
import com.hbm.lib.Library;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.AuxParticlePacket;
import com.hbm.packet.LoopedSoundPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.TEChemplantPacket;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityHopper;
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
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityMachineChemplant extends TileEntity implements ISidedInventory, IConsumer, IFluidHandler {

	private ItemStack slots[];

	public long power;
	public static final long maxPower = 100000;
	public int progress;
	public int maxProgress = 100;
	public boolean isProgressing;
	public boolean needsUpdate = false;
	int age = 0;
	int consumption = 100;
	int speed = 100;
	public FluidTank[] tanks;
	public Fluid[] tankTypes;
	
	Random rand = new Random();
	
	private String customName;
	
	public TileEntityMachineChemplant() {
		slots = new ItemStack[21];
		tanks = new FluidTank[4];
		tanks[0] = new FluidTank(16000);
		tanks[1] = new FluidTank(16000);
		tanks[2] = new FluidTank(16000);
		tanks[3] = new FluidTank(16000);
		tankTypes = new Fluid[]{null, null, null, null};
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
		return this.hasCustomInventoryName() ? this.customName : "container.chemplant";
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
		int[] types;
		
		this.power = nbt.getLong("powerTime");
		slots = new ItemStack[getSizeInventory()];

		tanks[0].readFromNBT((NBTTagCompound) nbt.getTag("input1"));
		tanks[1].readFromNBT((NBTTagCompound) nbt.getTag("input2"));
		tanks[2].readFromNBT((NBTTagCompound) nbt.getTag("output1"));
		tanks[3].readFromNBT((NBTTagCompound) nbt.getTag("output2"));
		
		types = nbt.getIntArray("types");
		
		if(types[0] != -1){
			tankTypes[0] = FluidRegistry.getFluid(types[0]);
		} else {
			tankTypes[0] = null;
		}
		if(types[1] != -1){
			tankTypes[1] = FluidRegistry.getFluid(types[1]);
		} else {
			tankTypes[1] = null;
		}
		if(types[2] != -1){
			tankTypes[2] = FluidRegistry.getFluid(types[2]);
		} else {
			tankTypes[2] = null;
		}
		if(types[3] != -1){
			tankTypes[3] = FluidRegistry.getFluid(types[3]);
		} else {
			tankTypes[3] = null;
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
		nbt.setLong("powerTime", power);
		NBTTagList list = new NBTTagList();
		int[] types = new int[]{tankTypes[0] != null ? tankTypes[0].getID() : -1, tankTypes[1] != null ? tankTypes[1].getID() : -1, tankTypes[2] != null ? tankTypes[2].getID() : -1, tankTypes[3] != null ? tankTypes[3].getID() : -1};

		NBTTagCompound input1 = new NBTTagCompound();
		NBTTagCompound input2 = new NBTTagCompound();
		NBTTagCompound output1 = new NBTTagCompound();
		NBTTagCompound output2 = new NBTTagCompound();
		
		tanks[0].writeToNBT(input1);
		tanks[1].writeToNBT(input2);
		tanks[2].writeToNBT(output1);
		tanks[3].writeToNBT(output2);
		
		nbt.setTag("input1", input1);
		nbt.setTag("input2", input2);
		nbt.setTag("output1", output1);
		nbt.setTag("output2", output2);
		
		nbt.setIntArray("types", types);
		
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
        return new int[] { 0 };
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
	
	public int getProgressScaled(int i) {
		return (progress * i) / maxProgress;
	}
	
	@Override
	public void updateEntity() {
		
		this.consumption = 100;
		this.speed = 100;
		
		for(int i = 1; i < 4; i++) {
			ItemStack stack = slots[i];
			
			if(stack != null) {
				if(stack.getItem() == ModItems.upgrade_speed_1) {
					this.speed -= 25;
					this.consumption += 300;
				}
				if(stack.getItem() == ModItems.upgrade_speed_2) {
					this.speed -= 50;
					this.consumption += 600;
				}
				if(stack.getItem() == ModItems.upgrade_speed_3) {
					this.speed -= 75;
					this.consumption += 900;
				}
				if(stack.getItem() == ModItems.upgrade_power_1) {
					this.consumption -= 30;
					this.speed += 5;
				}
				if(stack.getItem() == ModItems.upgrade_power_2) {
					this.consumption -= 60;
					this.speed += 10;
				}
				if(stack.getItem() == ModItems.upgrade_power_3) {
					this.consumption -= 90;
					this.speed += 15;
				}
			}
		}
		
		if(needsUpdate){
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			needsUpdate = false;
		}
		
		if(speed < 25)
			speed = 25;
		if(consumption < 10)
			consumption = 10;

		if(!worldObj.isRemote)
		{
			int meta = worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			isProgressing = false;
			
			age++;
			if(age >= 20)
			{
				age = 0;
			}
			
			if(age == 9 || age == 19) {
				fillFluidInit(tanks[2]);
				fillFluidInit(tanks[3]);
			}
			
			setContainers();
			
			power = Library.chargeTEFromItems(slots, 0, power, maxPower);
			if(inputValidForTank(0, 17))
				if(FFUtils.fillFromFluidContainer(slots, tanks[0], 17, 19))
					needsUpdate = true;
			if(inputValidForTank(1, 18))
				if(FFUtils.fillFromFluidContainer(slots, tanks[1], 18, 20))
					needsUpdate = true;
			if(FFUtils.fillFluidContainer(slots, tanks[2], 9, 11))
				needsUpdate = true;
			if(FFUtils.fillFluidContainer(slots, tanks[3], 10, 12))
				needsUpdate = true;
			

			FluidStack[] inputs = MachineRecipes.getFluidInputFromTempate(slots[4]);
			FluidStack[] outputs = MachineRecipes.getFluidOutputFromTempate(slots[4]);
			
			if((MachineRecipes.getChemInputFromTempate(slots[4]) != null || !Library.isArrayEmpty(inputs)) && 
					(MachineRecipes.getChemOutputFromTempate(slots[4]) != null || !Library.isArrayEmpty(outputs))) {
				this.maxProgress = (ItemChemistryTemplate.getProcessTime(slots[4]) * speed) / 100;
				
				if(power >= consumption && removeItems(MachineRecipes.getChemInputFromTempate(slots[4]), cloneItemStackProper(slots)) && hasFluidsStored(inputs)) {
					
					if(hasSpaceForItems(MachineRecipes.getChemOutputFromTempate(slots[4])) && hasSpaceForFluids(outputs)) {
						progress++;
						isProgressing = true;
						
						if(progress >= maxProgress) {
							progress = 0;

							addItems(MachineRecipes.getChemOutputFromTempate(slots[4]));
							addFluids(outputs);

							removeItems(MachineRecipes.getChemInputFromTempate(slots[4]), slots);
							removeFluids(inputs);
						}
						
						power -= consumption;
					}
				} else
					progress = 0;
			} else
				progress = 0;

			TileEntity te1 = null;
			TileEntity te2 = null;
			
			if(meta == 2) {
				te1 = worldObj.getTileEntity(xCoord - 2, yCoord, zCoord);
				te2 = worldObj.getTileEntity(xCoord + 3, yCoord, zCoord - 1);
			}
			if(meta == 3) {
				te1 = worldObj.getTileEntity(xCoord + 2, yCoord, zCoord);
				te2 = worldObj.getTileEntity(xCoord - 3, yCoord, zCoord + 1);
			}
			if(meta == 4) {
				te1 = worldObj.getTileEntity(xCoord, yCoord, zCoord + 2);
				te2 = worldObj.getTileEntity(xCoord - 1, yCoord, zCoord - 3);
			}
			if(meta == 5) {
				te1 = worldObj.getTileEntity(xCoord, yCoord, zCoord - 2);
				te2 = worldObj.getTileEntity(xCoord + 1, yCoord, zCoord + 3);
			}
		
			
			tryExchangeTemplates(te1, te2);
			
			if(te1 != null && te1 instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest)te1;
				
				for(int i = 5; i < 9; i++)
					tryFillContainer(chest, i);
			}
			
			if(te1 != null && te1 instanceof TileEntityHopper) {
				TileEntityHopper hopper = (TileEntityHopper)te1;

				for(int i = 5; i < 9; i++)
					tryFillContainer(hopper, i);
			}
			
			if(te1 != null && te1 instanceof TileEntityCrateIron) {
				TileEntityCrateIron hopper = (TileEntityCrateIron)te1;

				for(int i = 5; i < 9; i++)
					tryFillContainer(hopper, i);
			}
			
			if(te1 != null && te1 instanceof TileEntityCrateSteel) {
				TileEntityCrateSteel hopper = (TileEntityCrateSteel)te1;

				for(int i = 5; i < 9; i++)
					tryFillContainer(hopper, i);
			}
			
			
		
			
			if(te2 != null && te2 instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest)te2;
				
				for(int i = 0; i < chest.getSizeInventory(); i++)
					if(tryFillAssembler(chest, i))
						break;
			}
			
			if(te2 != null && te2 instanceof TileEntityHopper) {
				TileEntityHopper hopper = (TileEntityHopper)te2;

				for(int i = 0; i < hopper.getSizeInventory(); i++)
					if(tryFillAssembler(hopper, i))
						break;
			}
			
			if(te2 != null && te2 instanceof TileEntityCrateIron) {
				TileEntityCrateIron chest = (TileEntityCrateIron)te2;
				
				for(int i = 0; i < chest.getSizeInventory(); i++)
					if(tryFillAssembler(chest, i))
						break;
			}
			
			if(te2 != null && te2 instanceof TileEntityCrateSteel) {
				TileEntityCrateSteel hopper = (TileEntityCrateSteel)te2;

				for(int i = 0; i < hopper.getSizeInventory(); i++)
					if(tryFillAssembler(hopper, i))
						break;
			}
			
			
			
			if(isProgressing) {
				if(meta == 2) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(xCoord + 0.375, yCoord + 3, zCoord - 0.625, 1),
							new TargetPoint(worldObj.provider.dimensionId, xCoord + 0.375, yCoord + 3, zCoord - 0.625, 50));
				}
				if(meta == 3) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(xCoord + 0.625, yCoord + 3, zCoord + 1.625, 1),
							new TargetPoint(worldObj.provider.dimensionId, xCoord + 0.625, yCoord + 3, zCoord + 1.625, 50));
				}
				if(meta == 4) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(xCoord - 0.625, yCoord + 3, zCoord + 0.625, 1),
							new TargetPoint(worldObj.provider.dimensionId, xCoord - 0.625, yCoord + 3, zCoord + 0.625, 50));
				}
				if(meta == 5) {
					PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacket(xCoord + 1.625, yCoord + 3, zCoord + 0.375, 1),
							new TargetPoint(worldObj.provider.dimensionId, xCoord + 1.625, yCoord + 3, zCoord + 0.375, 50));
				}
			}
			
			PacketDispatcher.wrapper.sendToAll(new TEChemplantPacket(xCoord, yCoord, zCoord, isProgressing));
			PacketDispatcher.wrapper.sendToAll(new LoopedSoundPacket(xCoord, yCoord, zCoord));
			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
		}
		
	}
	
	public boolean tryExchangeTemplates(TileEntity te1, TileEntity te2) {
		//validateTe sees if it's a valid inventory tile entity
		boolean te1Valid = validateTe(te1);
		boolean te2Valid = validateTe(te2);
		
		if(te1Valid && te2Valid){
			IInventory iTe1 = (IInventory)te1;
			IInventory iTe2 = (IInventory)te2;
			boolean openSlot = false;
			boolean existingTemplate = false;
			boolean filledContainer = false;
			//Check if there's an existing template and an open slot
			for(int i = 0; i < iTe1.getSizeInventory(); i++){
				if(iTe1.getStackInSlot(i) == null){
					openSlot = true;
					
				}
				
			}
			if(this.slots[4] != null && this.slots[4].getItem() instanceof Item){
				existingTemplate = true;
			}
			//Check if there's a template in input
			for(int i = 0; i < iTe2.getSizeInventory(); i++){
				if(iTe2.getStackInSlot(i) != null && iTe2.getStackInSlot(i).getItem() instanceof ItemChemistryTemplate){
					if(openSlot && existingTemplate){
						filledContainer = tryFillContainer(iTe1, 4);
						
					}
					if(filledContainer){
					ItemStack copy = iTe2.getStackInSlot(i).copy();
					iTe2.setInventorySlotContents(i, null);
					this.slots[4] = copy;
					}
				}
				
			}
			
		
		}
		return false;
		
	}

	private boolean validateTe(TileEntity te) {
		if(te != null && te instanceof TileEntityChest) {
			return true;
			
		}
		
		if(te != null && te instanceof TileEntityHopper) {
			return true;
		}
		
		if(te != null && te instanceof TileEntityCrateIron) {
			return true;
		}
		
		if(te != null && te instanceof TileEntityCrateSteel) {
			return true;
		}
		return false;
	}

	private void setContainers() {
		
		if(slots[4] == null || (slots[4] != null && !(slots[4].getItem() instanceof ItemChemistryTemplate))) {
		} else {
			
			FluidStack[] inputs = MachineRecipes.getFluidInputFromTempate(slots[4]);
			FluidStack[] outputs = MachineRecipes.getFluidOutputFromTempate(slots[4]);

			tankTypes[0] = inputs[0] == null ? null : inputs[0].getFluid();
			tankTypes[1] = inputs[1] == null ? null : inputs[1].getFluid();
			tankTypes[2] = outputs[0] == null ? null : outputs[0].getFluid();
			tankTypes[3] = outputs[1] == null ? null : outputs[1].getFluid();
			
			if((inputs[0] != null && tanks[0].getFluid() == null) || tanks[0].getFluid() != null && tanks[0].getFluid().getFluid() != tankTypes[0]){
				tanks[0].setFluid(null);
				needsUpdate = true;
			}
			
			if((inputs[1] != null && tanks[1].getFluid() == null) || tanks[1].getFluid() != null && tanks[1].getFluid().getFluid() != tankTypes[1]){
				tanks[1].setFluid(null);
				needsUpdate = true;
			}
			if((outputs[0] != null && tanks[2].getFluid() == null) || tanks[2].getFluid() != null && tanks[2].getFluid().getFluid() != tankTypes[2]){
				tanks[2].setFluid(null);
				needsUpdate = true;
			}
			if((outputs[1] != null && tanks[3].getFluid() == null) || tanks[3].getFluid() != null && tanks[3].getFluid().getFluid() != tankTypes[3]){
				tanks[3].setFluid(null);
				needsUpdate = true;
			}
		}
	}
	
	protected boolean inputValidForTank(int tank, int slot){
		
		if(slots[slot] != null && tankTypes[tank] != null){
			if(slots[slot].getItem() instanceof IFluidContainerItem && ((IFluidContainerItem)slots[slot].getItem()).getFluid(slots[slot]) != null){
				return ((IFluidContainerItem)slots[slot].getItem()).getFluid(slots[slot]).getFluid() == tankTypes[tank];
			}
			if(FluidContainerRegistry.isFilledContainer(slots[slot]) && FluidContainerRegistry.getFluidForFilledItem(slots[slot]).getFluid() != null){
				return FluidContainerRegistry.getFluidForFilledItem(slots[slot]).getFluid() == tankTypes[tank];
			}
		}
		return false;
	}
	
	public boolean hasFluidsStored(FluidStack[] fluids) {
		if(Library.isArrayEmpty(fluids))
			return true;
		
		if((fluids[0] == null || fluids[0] != null && fluids[0].amount <= tanks[0].getFluidAmount()) && 
				(fluids[1] == null || fluids[1] != null && fluids[1].amount <= tanks[1].getFluidAmount()))
			return true;
		
		return false;
	}
	
	public boolean hasSpaceForFluids(FluidStack[] fluids) {
		if(Library.isArrayEmpty(fluids))
			return true;
		
		if(((fluids[0] == null || fluids[0] != null && tanks[2].fill(fluids[0], false) <= 0) && 
				(fluids[1] == null || fluids[1] != null && tanks[3].fill(fluids[1], false) <= 0)))
			return true;
		
		return false;
	}
	
	public void removeFluids(FluidStack[] fluids) {
		if(Library.isArrayEmpty(fluids))
			return;
		if(fluids[0] != null){
			tanks[0].drain(fluids[0].amount, true);
			this.needsUpdate = true;
		}
		if(fluids[1] != null){
			tanks[1].drain(fluids[1].amount, true);
			this.needsUpdate = true;
		}
	}
	
	public boolean hasSpaceForItems(ItemStack[] stacks) {
		if(stacks == null)
			return true;
		if(stacks != null && Library.isArrayEmpty(stacks))
			return true;

		ItemStack sta0 = Library.carefulCopy(slots[5]);
		if(sta0 != null)
			sta0.stackSize = 1;
		ItemStack sta1 = Library.carefulCopy(stacks[0]);
		if(sta1 != null)
			sta1.stackSize = 1;
		ItemStack sta2 = Library.carefulCopy(slots[6]);
		if(sta2 != null)
			sta2.stackSize = 1;
		ItemStack sta3 = Library.carefulCopy(stacks[1]);
		if(sta3 != null)
			sta3.stackSize = 1;
		ItemStack sta4 = Library.carefulCopy(slots[7]);
		if(sta4 != null)
			sta4.stackSize = 1;
		ItemStack sta5 = Library.carefulCopy(stacks[2]);
		if(sta5 != null)
			sta5.stackSize = 1;
		ItemStack sta6 = Library.carefulCopy(slots[8]);
		if(sta6 != null)
			sta6.stackSize = 1;
		ItemStack sta7 = Library.carefulCopy(stacks[3]);
		if(sta7 != null)
			sta7.stackSize = 1;
		
		if((slots[5] == null || stacks[0] == null || (stacks[0] != null && ItemStack.areItemStacksEqual(sta0, sta1) && ItemStack.areItemStackTagsEqual(sta0, sta1) && slots[5].stackSize + stacks[0].stackSize <= slots[5].getMaxStackSize())) && 
				(slots[6] == null || stacks[1] == null || (stacks[1] != null && ItemStack.areItemStacksEqual(sta2, sta3) && ItemStack.areItemStackTagsEqual(sta2, sta3) && slots[6].stackSize + stacks[1].stackSize <= slots[6].getMaxStackSize())) && 
				(slots[7] == null || stacks[2] == null || (stacks[2] != null && ItemStack.areItemStacksEqual(sta4, sta5) && ItemStack.areItemStackTagsEqual(sta4, sta5) && slots[7].stackSize + stacks[2].stackSize <= slots[7].getMaxStackSize())) && 
				(slots[8] == null || stacks[3] == null || (stacks[3] != null && ItemStack.areItemStacksEqual(sta6, sta7) && ItemStack.areItemStackTagsEqual(sta6, sta7) && slots[8].stackSize + stacks[3].stackSize <= slots[8].getMaxStackSize())))
			return true;
			
		return false;
	}
	
	public void addItems(ItemStack[] stacks) {
		if(slots[5] == null && stacks[0] != null)
			slots[5] = stacks[0].copy();
		else if (slots[5] != null && stacks[0] != null)
			slots[5].stackSize += stacks[0].stackSize;

		if(slots[6] == null && stacks[1] != null)
			slots[6] = stacks[1].copy();
		else if (slots[6] != null && stacks[1] != null)
			slots[6].stackSize += stacks[1].stackSize;

		if(slots[7] == null && stacks[2] != null)
			slots[7] = stacks[2].copy();
		else if (slots[7] != null && stacks[2] != null)
			slots[7].stackSize += stacks[2].stackSize;

		if(slots[8] == null && stacks[3] != null)
			slots[8] = stacks[3].copy();
		else if (slots[8] != null && stacks[3] != null)
			slots[8].stackSize += stacks[3].stackSize;
	}
	
	public void addFluids(FluidStack[] stacks) {
		if(stacks[0] != null)
			tanks[2].fill(stacks[1], true);
		if(stacks[1] != null)
			tanks[3].fill(stacks[1], true);
	}
	
	//I can't believe that worked.
	public ItemStack[] cloneItemStackProper(ItemStack[] array) {
		ItemStack[] stack = new ItemStack[array.length];
		
		for(int i = 0; i < array.length; i++)
			if(array[i] != null)
				stack[i] = array[i].copy();
			else
				stack[i] = null;
		
		return stack;
	}
	
	/**Unloads output into chests*/
	public boolean tryFillContainer(IInventory inventory, int slot) {
		
		int size = inventory.getSizeInventory();

		for(int i = 0; i < size; i++) {
			if(inventory.getStackInSlot(i) != null) {
				
				if(slots[slot] == null)
					return false;
				
				ItemStack sta1 = inventory.getStackInSlot(i).copy();
				ItemStack sta2 = slots[slot].copy();
				if(sta1 != null && sta2 != null) {
					sta1.stackSize = 1;
					sta2.stackSize = 1;
				
					if(ItemStack.areItemStacksEqual(sta1, sta2) && ItemStack.areItemStackTagsEqual(sta1, sta2) && inventory.getStackInSlot(i).stackSize < inventory.getStackInSlot(i).getMaxStackSize()) {
						slots[slot].stackSize--;
						
						if(slots[slot].stackSize <= 0)
							slots[slot] = null;
						
						ItemStack sta3 = inventory.getStackInSlot(i).copy();
						sta3.stackSize++;
						inventory.setInventorySlotContents(i, sta3);
					
						return true;
					}
				}
			}
		}
		for(int i = 0; i < size; i++) {
			
			if(slots[slot] == null)
				return false;
			
			ItemStack sta2 = slots[slot].copy();
			if(inventory.getStackInSlot(i) == null && sta2 != null) {
				sta2.stackSize = 1;
				slots[slot].stackSize--;
				
				if(slots[slot].stackSize <= 0)
					slots[slot] = null;
				
				inventory.setInventorySlotContents(i, sta2);
					
				return true;
			}
		}
		
		return false;
	}
	
	/**Loads assembler's input queue from chests*/
	public boolean tryFillAssembler(IInventory inventory, int slot) {

		FluidStack[] inputs = MachineRecipes.getFluidInputFromTempate(slots[4]);
		FluidStack[] outputs = MachineRecipes.getFluidOutputFromTempate(slots[4]);
		
		if(!((MachineRecipes.getChemInputFromTempate(slots[4]) != null || !Library.isArrayEmpty(inputs)) && 
				(MachineRecipes.getChemOutputFromTempate(slots[4]) != null || !Library.isArrayEmpty(outputs))))
			return false;
		else {
			List<ItemStack> list = MachineRecipes.getChemInputFromTempate(slots[4]);
			if(list == null || list.isEmpty())
				return false;
			
			for(int i = 0; i < list.size(); i++)
				list.get(i).stackSize = 1;


			if(inventory.getStackInSlot(slot) == null)
				return false;
			
			ItemStack stack = inventory.getStackInSlot(slot).copy();
			stack.stackSize = 1;
			
			boolean flag = false;
			
			for(int i = 0; i < list.size(); i++)
				if(ItemStack.areItemStacksEqual(stack, list.get(i)) && ItemStack.areItemStackTagsEqual(stack, list.get(i)))
					flag = true;
			
			if(!flag)
				return false;
			
		}
		
		for(int i = 13; i < 17; i++) {
			
			if(slots[i] != null) {
			
				ItemStack sta1 = inventory.getStackInSlot(slot).copy();
				ItemStack sta2 = slots[i].copy();
				if(sta1 != null && sta2 != null) {
					sta1.stackSize = 1;
					sta2.stackSize = 1;
			
					if(ItemStack.areItemStacksEqual(sta1, sta2) && ItemStack.areItemStackTagsEqual(sta1, sta2) && slots[i].stackSize < slots[i].getMaxStackSize()) {
						ItemStack sta3 = inventory.getStackInSlot(slot).copy();
						sta3.stackSize--;
						if(sta3.stackSize <= 0)
							sta3 = null;
						inventory.setInventorySlotContents(slot, sta3);
				
						slots[i].stackSize++;
						return true;
					}
				}
			}
		}
		
		for(int i = 13; i < 17; i++) {

			ItemStack sta2 = inventory.getStackInSlot(slot).copy();
			if(slots[i] == null && sta2 != null) {
				sta2.stackSize = 1;
				slots[i] = sta2.copy();
				
				ItemStack sta3 = inventory.getStackInSlot(slot).copy();
				sta3.stackSize--;
				if(sta3.stackSize <= 0)
					sta3 = null;
				inventory.setInventorySlotContents(slot, sta3);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**boolean true: remove items, boolean false: simulation mode*/
	public boolean removeItems(List<ItemStack> stack, ItemStack[] array) {
		
		if(stack == null || stack.isEmpty())
			return true;
		
		for(int i = 0; i < stack.size(); i++) {
			for(int j = 0; j < stack.get(i).stackSize; j++) {
				ItemStack sta = stack.get(i).copy();
				sta.stackSize = 1;
			
				if(!canRemoveItemFromArray(sta, array))
					return false;
			}
		}
		
		return true;
		
	}
	
	public boolean canRemoveItemFromArray(ItemStack stack, ItemStack[] array) {

		ItemStack st = stack.copy();
		
		if(st == null)
			return true;
		
		for(int i = 13; i < 17; i++) {
			
			if(array[i] != null) {
				ItemStack sta = array[i].copy();
				sta.stackSize = 1;
			
				if(sta != null && isItemAcceptible(sta, st) && array[i].stackSize > 0) {
					array[i].stackSize--;
					
					if(array[i].stackSize <= 0)
						array[i] = null;
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isItemAcceptible(ItemStack stack1, ItemStack stack2) {
		
		if(stack1 != null && stack2 != null) {
			if(ItemStack.areItemStacksEqual(stack1, stack2))
				return true;
		
			int[] ids1 = OreDictionary.getOreIDs(stack1);
			int[] ids2 = OreDictionary.getOreIDs(stack2);
			
			if(ids1 != null && ids2 != null && ids1.length > 0 && ids2.length > 0) {
				for(int i = 0; i < ids1.length; i++)
					for(int j = 0; j < ids2.length; j++)
						if(ids1[i] == ids2[j])
							return true;
			}
		}
		
		return false;
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
		int meta = worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		boolean update = false || needsUpdate;
		if(meta == 5) {
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 2, this.yCoord, this.zCoord, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 2, this.yCoord, this.zCoord + 1, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 3, this.yCoord, this.zCoord, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 3, this.yCoord, this.zCoord + 1, 2000);
		}
		
		if(meta == 3) {
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 2, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord - 2, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 3, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord + 3, 2000);
		}
		
		if(meta == 2) {
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 2, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord + 2, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 3, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord - 3, 2000);
		}
		
		if(meta == 4) {
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 2, this.yCoord, this.zCoord, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 2, this.yCoord, this.zCoord - 1, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 3, this.yCoord, this.zCoord, 2000);
			update = update || FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 3, this.yCoord, this.zCoord - 1, 2000);
		}
		needsUpdate = update;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
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
