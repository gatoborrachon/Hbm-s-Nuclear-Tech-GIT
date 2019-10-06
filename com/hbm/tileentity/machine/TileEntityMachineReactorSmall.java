package com.hbm.tileentity.machine;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemFuelRod;
import com.hbm.packet.AuxGaugePacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.saveddata.RadiationSavedData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

public class TileEntityMachineReactorSmall extends TileEntity implements ISidedInventory, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];

	public int hullHeat;
	public final int maxHullHeat = 100000;
	public int coreHeat;
	public final int maxCoreHeat = 50000;
	public int rods;
	public final int rodsMax = 100;
	public boolean retracting = true;
	public int age = 0;
	public FluidTank[] tanks;
	public Fluid[] tankTypes;
	public boolean needsUpdate;
	public int compression = 0;

	private static final int[] slots_top = new int[] { 0 };
	private static final int[] slots_bottom = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 16 };
	private static final int[] slots_side = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 16 };

	private String customName;

	public TileEntityMachineReactorSmall() {
		slots = new ItemStack[16];
		tanks = new FluidTank[3];
		tankTypes = new Fluid[3];
		tanks[0] = new FluidTank(32000);
		tankTypes[0] = FluidRegistry.WATER;
		tanks[1] = new FluidTank(16000);
		tankTypes[1] = ModForgeFluids.coolant;
		tanks[2] = new FluidTank(8000);
		tankTypes[2] = ModForgeFluids.steam;
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
		return this.hasCustomInventoryName() ? this.customName : "container.reactorSmall";
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

	public void compress(int level){
		if(level == compression)
			return;
		if(level >= 0 && level < 3){
			if(compression == 0){
				if(level == 1){
					tankTypes[2] = ModForgeFluids.hotsteam;
					int newAmount = (int) (tanks[2].getFluidAmount()/10D);
					tanks[2].drain(tanks[2].getCapacity(), true);
					tanks[2].fill(new FluidStack(tankTypes[2], newAmount), true);
				}
				if(level == 2){
					tankTypes[2] = ModForgeFluids.superhotsteam;
					int newAmount = (int) (tanks[2].getFluidAmount()/100D);
					tanks[2].drain(tanks[2].getCapacity(), true);
					tanks[2].fill(new FluidStack(tankTypes[2], newAmount), true);
				}
			}
			if(compression == 1){
				if(level == 0){
					tankTypes[2] = ModForgeFluids.steam;
					int newAmount = (int) (tanks[2].getFluidAmount()*10);
					tanks[2].drain(tanks[2].getCapacity(), true);
					tanks[2].fill(new FluidStack(tankTypes[2], newAmount), true);
				}
				if(level == 2){
					tankTypes[2] = ModForgeFluids.superhotsteam;
					int newAmount = (int) (tanks[2].getFluidAmount()/10D);
					tanks[2].drain(tanks[2].getCapacity(), true);
					tanks[2].fill(new FluidStack(tankTypes[2], newAmount), true);
				}
			}
			if(compression == 2){
				if(level == 0){
					tankTypes[2] = ModForgeFluids.steam;
					int newAmount = (int) (tanks[2].getFluidAmount()*100);
					tanks[2].drain(tanks[2].getCapacity(), true);
					tanks[2].fill(new FluidStack(tankTypes[2], newAmount), true);
				}
				if(level == 1){
					tankTypes[2] = ModForgeFluids.hotsteam;
					int newAmount = (int) (tanks[2].getFluidAmount()*10D);
					tanks[2].drain(tanks[2].getCapacity(), true);
					tanks[2].fill(new FluidStack(tankTypes[2], newAmount), true);
				}
			}
			
			compression = level;
		}
	}
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemStack) {
		if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10
				|| i == 11)
			if (itemStack.getItem() instanceof ItemFuelRod)
				return true;
		if (i == 12)
			if (itemStack.getItem() == ModItems.rod_water || itemStack.getItem() == ModItems.rod_dual_water
					|| itemStack.getItem() == ModItems.rod_quad_water || itemStack.getItem() == Items.water_bucket)
				return true;
		if (i == 14)
			if (itemStack.getItem() == ModItems.rod_coolant || itemStack.getItem() == ModItems.rod_dual_coolant
					|| itemStack.getItem() == ModItems.rod_quad_coolant)
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

		coreHeat = nbt.getInteger("heat");
		hullHeat = nbt.getInteger("hullHeat");
		rods = nbt.getInteger("rods");
		retracting = nbt.getBoolean("ret");
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
		tankTypes[0] = FluidRegistry.WATER;
		tankTypes[1] = ModForgeFluids.coolant;
		compression = nbt.getInteger("compression");
		if(compression == 0){
			tankTypes[2] = ModForgeFluids.steam;
		} else if(compression == 1){
			tankTypes[2] = ModForgeFluids.hotsteam;
		} else if(compression == 2){
			tankTypes[2] = ModForgeFluids.superhotsteam;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("heat", coreHeat);
		nbt.setInteger("hullHeat", hullHeat);
		nbt.setInteger("rods", rods);
		nbt.setBoolean("ret", retracting);
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
		nbt.setInteger("compression", compression);
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
		if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8 || i == 9 || i == 10
				|| i == 11)
			if (itemStack.getItem() == ModItems.rod_uranium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_dual_uranium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_quad_uranium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_plutonium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_dual_plutonium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_quad_plutonium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_mox_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_dual_mox_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_quad_mox_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_schrabidium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_dual_schrabidium_fuel_depleted
					|| itemStack.getItem() == ModItems.rod_quad_schrabidium_fuel_depleted)
				return true;
		if (i == 13 || i == 15)
			if (itemStack.getItem() == Items.bucket || itemStack.getItem() == ModItems.rod_empty
					|| itemStack.getItem() == ModItems.rod_dual_empty || itemStack.getItem() == ModItems.rod_quad_empty
					|| itemStack.getItem() == ModItems.fluid_tank_empty
					|| itemStack.getItem() == ModItems.fluid_barrel_empty)
				return true;

		return false;
	
	}

	public int getCoreHeatScaled(int i) {
		return (coreHeat * i) / maxCoreHeat;
	}

	public int getHullHeatScaled(int i) {
		return (hullHeat * i) / maxHullHeat;
	}

	public int getSteamScaled(int i) {
		return (tanks[2].getFluidAmount() * i) / tanks[2].getCapacity();
	}

	public boolean hasCoreHeat() {
		return coreHeat > 0;
	}

	public boolean hasHullHeat() {
		return hullHeat > 0;
	}

	private int[] getNeighbouringSlots(int id) {

		switch (id) {
		case 0:
			return new int[] { 1, 5 };
		case 1:
			return new int[] { 0, 6 };
		case 2:
			return new int[] { 3, 7 };
		case 3:
			return new int[] { 2, 4, 8 };
		case 4:
			return new int[] { 3, 9 };
		case 5:
			return new int[] { 0, 6, 0xA };
		case 6:
			return new int[] { 1, 5, 0xB };
		case 7:
			return new int[] { 2, 8 };
		case 8:
			return new int[] { 3, 7, 9 };
		case 9:
			return new int[] { 4, 8 };
		case 10:
			return new int[] { 5, 0xB };
		case 11:
			return new int[] { 6, 0xA };
		}

		return null;
	}
	
	public int getFuelPercent() {
		
		if(getRodCount() == 0)
			return 0;
		
		int rodMax = 0;
		int rod = 0;
		
		for(int i = 0; i < 12; i++) {
			
			if(slots[i] != null && slots[i].getItem() instanceof ItemFuelRod) {
				rodMax += ((ItemFuelRod)slots[i].getItem()).lifeTime;
				rod += ((ItemFuelRod)slots[i].getItem()).lifeTime - ItemFuelRod.getLifeTime(slots[i]);
			}
		}
		
		if(rodMax == 0)
			return 0;
		
		return rod * 100 / rodMax;
	}

	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {

			age++;
			if (age >= 20) {
				age = 0;
			}

			if (age == 9 || age == 19)
				fillFluidInit(tanks[2]);
			
			if(inputValidForTank(0, 12))
				if(FFUtils.fillFromFluidContainer(slots, tanks[0], 12, 13))
					needsUpdate = true;
			if(inputValidForTank(1, 14))
				if(FFUtils.fillFromFluidContainer(slots, tanks[1], 14, 15))
					needsUpdate = true;

			if (retracting && rods > 0) {

				if (rods == rodsMax)
					this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "hbm:block.reactorStart", 1.0F,
							0.75F);

				rods--;

				if (rods == 0)
					this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "hbm:block.reactorStop", 1.0F,
							1.0F);
			}
			if (!retracting && rods < rodsMax) {

				if (rods == 0)
					this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "hbm:block.reactorStart", 1.0F,
							0.75F);

				rods++;

				if (rods == rodsMax)
					this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "hbm:block.reactorStop", 1.0F,
							1.0F);
			}

			if (rods >= rodsMax)
				for (int i = 0; i < 12; i++) {
					if (slots[i] != null && slots[i].getItem() instanceof ItemFuelRod)
						decay(i);
				}

			coreHeatMod = 1.0;
			hullHeatMod = 1.0;
			conversionMod = 1.0;
			decayMod = 1.0;
			
			getInteractions();

			if (this.coreHeat > 0 && this.tanks[1].getFluidAmount() > 0 && this.hullHeat < this.maxHullHeat) {
				this.hullHeat += this.coreHeat * 0.175 * hullHeatMod;
				this.coreHeat -= this.coreHeat * 0.1;

				this.tanks[1].drain(10, true);
				needsUpdate = true;
			}

			if (this.hullHeat > maxHullHeat) {
				this.hullHeat = maxHullHeat;
			}

			if (this.hullHeat > 0 && this.tanks[0].getFluidAmount() > 0) {
				generateSteam();
				this.hullHeat -= this.hullHeat * 0.085;
			}

			if (this.coreHeat > maxCoreHeat) {
				this.explode();
			}

			if (rods > 0 && coreHeat > 0
					&& !(blocksRad(xCoord + 1, yCoord + 1, zCoord)
							&& blocksRad(xCoord - 1, yCoord + 1, zCoord)
							&& blocksRad(xCoord, yCoord + 1, zCoord + 1)
							&& blocksRad(xCoord, yCoord + 1, zCoord - 1))) {

				/*List<Entity> list = (List<Entity>) worldObj.getEntitiesWithinAABBExcludingEntity(null,
						AxisAlignedBB.getBoundingBox(xCoord + 0.5 - 5, yCoord + 1.5 - 5, zCoord + 0.5 - 5,
								xCoord + 0.5 + 5, yCoord + 1.5 + 5, zCoord + 0.5 + 5));

				for (Entity e : list) {
					if (e instanceof EntityLivingBase)
                		Library.applyRadiation((EntityLivingBase)e, 80, 24, 60, 19);
				}*/
				
				float rad = (float)coreHeat / (float)maxCoreHeat * 50F;
				RadiationSavedData.incrementRad(worldObj, xCoord, zCoord, rad, rad * 4);
			}

			if(needsUpdate){
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[]{tanks[0], tanks[1], tanks[2]}));
				needsUpdate = false;
			}

			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, rods, 0));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, retracting ? 1 : 0, 1));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, coreHeat, 2));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, hullHeat, 3));
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
		return stack.getFluid() == tankTypes[tank];
	}
	
	private void generateSteam() {

		//function of SHS produced per tick
		//maxes out at heat% * tank capacity / 20
		double steam = (((double)hullHeat / (double)maxHullHeat) * ((double)tanks[2].getCapacity() / 50D)) * conversionMod;
		
		double water = steam;
		
		if(tankTypes[2] == ModForgeFluids.steam){
			water /= 100D;
		} else if(tankTypes[2] == ModForgeFluids.hotsteam){
			water /= 10D;
		}
		
		tanks[0].drain((int)Math.ceil(water), true);
		tanks[2].fill(new FluidStack(tankTypes[2], (int)Math.floor(steam)), true);
		needsUpdate = true;
		
	}
	
	private void getInteractions() {

		getInteractionForBlock(xCoord + 1, yCoord + 1, zCoord);
		getInteractionForBlock(xCoord - 1, yCoord + 1, zCoord);
		getInteractionForBlock(xCoord, yCoord + 1, zCoord + 1);
		getInteractionForBlock(xCoord, yCoord + 1, zCoord - 1);
		
		TileEntity te1 = worldObj.getTileEntity(xCoord + 2, yCoord, zCoord);
		TileEntity te2 = worldObj.getTileEntity(xCoord - 2, yCoord, zCoord);
		TileEntity te3 = worldObj.getTileEntity(xCoord, yCoord, zCoord + 2);
		TileEntity te4 = worldObj.getTileEntity(xCoord, yCoord, zCoord - 2);

		boolean b1 = blocksRad(xCoord + 1, yCoord + 1, zCoord);
		boolean b2 = blocksRad(xCoord - 1, yCoord + 1, zCoord);
		boolean b3 = blocksRad(xCoord, yCoord + 1, zCoord + 1);
		boolean b4 = blocksRad(xCoord, yCoord + 1, zCoord - 1);
		
		TileEntityMachineReactorSmall[] reactors = new TileEntityMachineReactorSmall[4];

		reactors[0] = ((te1 instanceof TileEntityMachineReactorSmall && !b1) ? (TileEntityMachineReactorSmall)te1 : null);
		reactors[1] = ((te2 instanceof TileEntityMachineReactorSmall && !b2) ? (TileEntityMachineReactorSmall)te2 : null);
		reactors[2] = ((te3 instanceof TileEntityMachineReactorSmall && !b3) ? (TileEntityMachineReactorSmall)te3 : null);
		reactors[3] = ((te4 instanceof TileEntityMachineReactorSmall && !b4) ? (TileEntityMachineReactorSmall)te4 : null);
		
		for(int i = 0; i < 4; i++) {
			
			if(reactors[i] != null && reactors[i].rods >= rodsMax && reactors[i].getRodCount() > 0) {
				decayMod += reactors[i].getRodCount() / 2D;
			}
		}
	}

	private double decayMod = 1.0D;
	private double coreHeatMod = 1.0D;
	private double hullHeatMod = 1.0D;
	private double conversionMod = 1.0D;
	
	private void getInteractionForBlock(int x, int y, int z) {
		
		Block b = worldObj.getBlock(x, y, z);
		TileEntity te = worldObj.getTileEntity(x, y, z);
		
		if(b == Blocks.lava || b == Blocks.flowing_lava) {
			hullHeatMod *= 3;
			conversionMod *= 0.5;
			
		} else if(b == Blocks.redstone_block) {
			conversionMod *= 1.15;
			
		} else if(b == ModBlocks.block_lead) {
			decayMod += 1;
			
		} else if(b == Blocks.water || b == Blocks.flowing_water) {
			tanks[0].fill(new FluidStack(tankTypes[0], 25), true);
			needsUpdate = true;
		} else if(b == ModBlocks.block_niter) {
			if(tanks[0].getFluidAmount() >= 50 && tanks[1].getFluidAmount() + 5 <= tanks[1].getCapacity()) {
				tanks[0].drain(50, true);
				tanks[1].fill(new FluidStack(tankTypes[1], 5), true);
				needsUpdate = true;
			}
			
		} else if(te instanceof TileEntityMachineReactor) {
			TileEntityMachineReactor reactor = (TileEntityMachineReactor)te;
			if(reactor.dualPower < 1 && this.coreHeat > 0)
				reactor.dualPower = 1;
			
		} else if(te instanceof TileEntityNukeFurnace) {
			TileEntityNukeFurnace reactor = (TileEntityNukeFurnace)te;
			if(reactor.dualPower < 1 && this.coreHeat > 0)
				reactor.dualPower = 1;
			
		} else if(b == ModBlocks.block_uranium) {
			coreHeatMod *= 1.05;
			
		} else if(b == Blocks.coal_block) {
			hullHeatMod *= 1.1;
			
		} else if(b == ModBlocks.block_beryllium) {
			hullHeatMod *= 0.95;
			conversionMod *= 1.05;
			
		} else if(b == ModBlocks.block_schrabidium) {
			decayMod += 1;
			conversionMod *= 1.25;
			hullHeatMod *= 1.1;
			
		} else if(b == ModBlocks.block_waste) {
			decayMod += 3;
			
		}
	}
	
	private boolean blocksRad(int x, int y, int z) {
		
		Block b = worldObj.getBlock(x, y, z);
		
		if(b == ModBlocks.block_lead || b == ModBlocks.block_desh || b == ModBlocks.brick_concrete)
			return true;
		
		if(b.getExplosionResistance(null) >= 100)
			return true;
		
		return false;
	}
	
	public int getRodCount() {
		
		int count = 0;
		
		for(int i = 0; i < 12; i++) {
			
			if(slots[i] != null && slots[i].getItem() instanceof ItemFuelRod)
				count++;
		}
		
		return count;
	}

	private boolean hasFuelRod(int id) {
		if (id > 11)
			return false;

		if (slots[id] != null)
			return slots[id].getItem() instanceof ItemFuelRod;

		return false;
	}

	private int getNeightbourCount(int id) {

		int[] neighbours = this.getNeighbouringSlots(id);

		if (neighbours == null)
			return 0;

		int count = 0;

		for (int i = 0; i < neighbours.length; i++)
			if (hasFuelRod(neighbours[i]))
				count++;

		return count;

	}

	// itemstack in slots[id] has to contain ItemFuelRod item
	private void decay(int id) {
		if (id > 11)
			return;

		int decay = getNeightbourCount(id) + 1;
		
		decay *= decayMod;

		for (int i = 0; i < decay; i++) {
			ItemFuelRod rod = ((ItemFuelRod) slots[id].getItem());
			this.coreHeat += rod.heat * coreHeatMod;
			ItemFuelRod.setLifeTime(slots[id], ItemFuelRod.getLifeTime(slots[id]) + 1);
			ItemFuelRod.updateDamage(slots[id]);

			if (ItemFuelRod.getLifeTime(slots[id]) > ((ItemFuelRod) slots[id].getItem()).lifeTime) {
				onRunOut(id);
				return;
			}
		}
	}

	// itemstack in slots[id] has to contain ItemFuelRod item
	private void onRunOut(int id) {

		//System.out.println("aaa");

		Item item = slots[id].getItem();

		if (item == ModItems.rod_uranium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_uranium_fuel_depleted);

		} else if (item == ModItems.rod_thorium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_thorium_fuel_depleted);

		} else if (item == ModItems.rod_plutonium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_plutonium_fuel_depleted);

		} else if (item == ModItems.rod_mox_fuel) {
			slots[id] = new ItemStack(ModItems.rod_mox_fuel_depleted);

		} else if (item == ModItems.rod_schrabidium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_schrabidium_fuel_depleted);

		} else if (item == ModItems.rod_dual_uranium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_dual_uranium_fuel_depleted);

		} else if (item == ModItems.rod_dual_thorium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_dual_thorium_fuel_depleted);

		} else if (item == ModItems.rod_dual_plutonium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_dual_plutonium_fuel_depleted);

		} else if (item == ModItems.rod_dual_mox_fuel) {
			slots[id] = new ItemStack(ModItems.rod_dual_mox_fuel_depleted);

		} else if (item == ModItems.rod_dual_schrabidium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_dual_schrabidium_fuel_depleted);

		} else if (item == ModItems.rod_quad_uranium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_quad_uranium_fuel_depleted);

		} else if (item == ModItems.rod_quad_thorium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_quad_thorium_fuel_depleted);

		} else if (item == ModItems.rod_quad_plutonium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_quad_plutonium_fuel_depleted);

		} else if (item == ModItems.rod_quad_mox_fuel) {
			slots[id] = new ItemStack(ModItems.rod_quad_mox_fuel_depleted);

		} else if (item == ModItems.rod_quad_schrabidium_fuel) {
			slots[id] = new ItemStack(ModItems.rod_quad_schrabidium_fuel_depleted);
		}
	}

	private void explode() {
		for (int i = 0; i < slots.length; i++) {
			this.slots[i] = null;
		}

		worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 18.0F, true);
		ExplosionNukeGeneric.waste(worldObj, this.xCoord, this.yCoord, this.zCoord, 35);
		worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, ModBlocks.toxic_block);

		RadiationSavedData.incrementRad(worldObj, xCoord, zCoord, 1000F, 2000F);
	}

	public void fillFluidInit(FluidTank tank) {
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord, this.zCoord, 1000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord, this.zCoord, 1000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 1, 1000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 1, 1000) || needsUpdate;

		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 1, this.yCoord + 2, this.zCoord, 1000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 1, this.yCoord + 2, this.zCoord, 1000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord + 2, this.zCoord - 1, 1000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord + 2, this.zCoord + 1, 1000) || needsUpdate;
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
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 3){
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
		} else {
			return 0;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource != null && resource.getFluid() == tankTypes[2]){
			needsUpdate = true;
			return tanks[2].drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(tanks[2].getFluidAmount() > 0){
			needsUpdate = true;
			return tanks[2].drain(maxDrain, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == tankTypes[0] || fluid == tankTypes[1];
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == tankTypes[2];
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tanks[0].getInfo(), tanks[1].getInfo(), tanks[2].getInfo()};
	}
}