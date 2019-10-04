package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.entity.logic.EntityNukeExplosionMK4;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IConsumer;
import com.hbm.interfaces.ISource;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.items.ModItems;
import com.hbm.items.special.ItemAMSCore;
import com.hbm.items.special.ItemCatalyst;
import com.hbm.items.tool.ItemSatChip;
import com.hbm.lib.Library;
import com.hbm.lib.ModDamageSource;
import com.hbm.packet.AuxElectricityPacket;
import com.hbm.packet.AuxGaugePacket;
import com.hbm.packet.FluidTankPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.saveddata.SatelliteSaveStructure.SatelliteType;
import com.hbm.saveddata.SatelliteSavedData;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import scala.util.Random;

public class TileEntityAMSBase extends TileEntity implements ISidedInventory, ISource, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];

	public long power = 0;
	public static final long maxPower = 1000000000000000L;
	public int field = 0;
	public static final int maxField = 100;
	public int efficiency = 0;
	public static final int maxEfficiency = 100;
	public int heat = 0;
	public static final int maxHeat = 5000;
	public int age = 0;
	public int warning = 0;
	public int mode = 0;
	public boolean locked = false;
	public FluidTank[] tanks;
	public Fluid[] tankTypes;
	public List<IConsumer> list = new ArrayList<IConsumer>();
	public int color = -1;
	public boolean needsUpdate;
	
	Random rand = new Random();

	private static final int[] slots_top = new int[] { 0 };
	private static final int[] slots_bottom = new int[] { 0 };
	private static final int[] slots_side = new int[] { 0 };
	
	private String customName;
	
	
	//Drillgon200: This is getting replaced anyway so I really don't care if I do a crappy job.
	public TileEntityAMSBase() {
		slots = new ItemStack[16];
		tanks = new FluidTank[4];
		tankTypes = new Fluid[4];
		needsUpdate = false;
		//coolant
		tanks[0] = new FluidTank(8000);
		tankTypes[0] = ModForgeFluids.coolant;
		//cryogel
		tanks[1] = new FluidTank(8000);
		tankTypes[1] = ModForgeFluids.cryogel;
		//deuterium
		tanks[2] = new FluidTank(8000);
		tankTypes[2] = ModForgeFluids.deuterium;
		//tritium
		tanks[3] = new FluidTank(8000);
		tankTypes[3] = ModForgeFluids.tritium;
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
		return this.hasCustomInventoryName() ? this.customName : "container.amsBase";
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
		

		power = nbt.getLong("power");
		field = nbt.getInteger("field");
		efficiency = nbt.getInteger("efficiency");
		heat = nbt.getInteger("heat");
		locked = nbt.getBoolean("locked");
		slots = new ItemStack[getSizeInventory()];
		tanks = new FluidTank[4];
		
		NBTTagList tanksList = nbt.getTagList("tanks", 10);
		for(int i = 0; i < tanksList.tagCount(); i ++){
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte b0 = tag.getByte("tank");
			if(b0 >= 0 && b0 < tanks.length){
				tanks[b0].readFromNBT(tag);
				tankTypes[b0] = FluidRegistry.getFluid(tag.getInteger("fluidTankType"));
			}
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
		tankTypes[0] = ModForgeFluids.coolant;
		tankTypes[1] = ModForgeFluids.cryogel;
		tankTypes[2] = ModForgeFluids.deuterium;
		tankTypes[3] = ModForgeFluids.tritium;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setLong("power", power);
		nbt.setInteger("field", field);
		nbt.setInteger("efficiency", efficiency);
		nbt.setInteger("heat", heat);
		nbt.setBoolean("locked", locked);
		NBTTagList list = new NBTTagList();
		NBTTagList tankList = new NBTTagList();
		
		for(int i = 0; i < tanks.length; i ++){
			if(tanks[i] != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("tank", (byte)i);
				tanks[i].writeToNBT(tag);
				tankList.appendTag(tag);
				tag.setInteger("fluidTankType", FluidRegistry.getFluidID(tankTypes[i]));
			}
		}
		
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
		nbt.setTag("tanks", tankList);
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
	
	@Override
	public void updateEntity() {

		if (!worldObj.isRemote) {
			if(needsUpdate){
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[] {tanks[0], tanks[1], tanks[2], tanks[3]}));
				needsUpdate = false;
			}
				
			
			for(int i = 0; i < tanks.length; i++){
				tanks[i].fill(new FluidStack(tankTypes[i], tanks[i].getCapacity()), true);
				needsUpdate = true;
			}
			
			if(!locked) {
				
				age++;
				if(age >= 20)
				{
					age = 0;
				}
				
				if(age == 9 || age == 19)
					ffgeuaInit();
				
				
				int f1 = 0, f2 = 0, f3 = 0, f4 = 0;
				int booster = 0;

				if(worldObj.getTileEntity(xCoord + 6, yCoord, zCoord) instanceof TileEntityAMSLimiter) {
					TileEntityAMSLimiter te = (TileEntityAMSLimiter)worldObj.getTileEntity(xCoord + 6, yCoord, zCoord);
					if(!te.locked && worldObj.getBlockMetadata(xCoord + 6, yCoord, zCoord) == 4) {
						f1 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				if(worldObj.getTileEntity(xCoord - 6, yCoord, zCoord) instanceof TileEntityAMSLimiter) {
					TileEntityAMSLimiter te = (TileEntityAMSLimiter)worldObj.getTileEntity(xCoord - 6, yCoord, zCoord);
					if(!te.locked && worldObj.getBlockMetadata(xCoord - 6, yCoord, zCoord) == 5) {
						f2 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				if(worldObj.getTileEntity(xCoord, yCoord, zCoord + 6) instanceof TileEntityAMSLimiter) {
					TileEntityAMSLimiter te = (TileEntityAMSLimiter)worldObj.getTileEntity(xCoord, yCoord, zCoord + 6);
					if(!te.locked && worldObj.getBlockMetadata(xCoord, yCoord, zCoord + 6) == 2) {
						f3 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				if(worldObj.getTileEntity(xCoord, yCoord, zCoord - 6) instanceof TileEntityAMSLimiter) {
					TileEntityAMSLimiter te = (TileEntityAMSLimiter)worldObj.getTileEntity(xCoord, yCoord, zCoord - 6);
					if(!te.locked && worldObj.getBlockMetadata(xCoord, yCoord, zCoord - 6) == 3) {
						f4 = te.efficiency;
						if(te.mode == 2)
							booster++;
					}
				}
				
				this.field = Math.round(calcField(f1, f2, f3, f4));
				
				mode = 0;
				if(field > 0)
					mode = 1;
				if(booster > 0)
					mode = 2;
				
				if(worldObj.getTileEntity(xCoord, yCoord + 9, zCoord) instanceof TileEntityAMSEmitter) {
					TileEntityAMSEmitter te = (TileEntityAMSEmitter)worldObj.getTileEntity(xCoord, yCoord + 9, zCoord);
						this.efficiency = te.efficiency;
				}
				
				this.color = -1;
				
				float powerMod = 1;
				float heatMod = 1;
				float fuelMod = 1;
				long powerBase = 0;
				int heatBase = 0;
				int fuelBase = 0;
				
				if(slots[8] != null && slots[9] != null && slots[10] != null && slots[11] != null && slots[12] != null &&
						slots[8].getItem() instanceof ItemCatalyst && slots[9].getItem() instanceof ItemCatalyst &&
						slots[10].getItem() instanceof ItemCatalyst && slots[11].getItem() instanceof ItemCatalyst &&
						slots[12].getItem() instanceof ItemAMSCore && hasResonators() && efficiency > 0) {
					int a = ((ItemCatalyst)slots[8].getItem()).getColor();
					int b = ((ItemCatalyst)slots[9].getItem()).getColor();
					int c = ((ItemCatalyst)slots[10].getItem()).getColor();
					int d = ((ItemCatalyst)slots[11].getItem()).getColor();

					int e = this.calcAvgHex(a, b);
					int f = this.calcAvgHex(c, d);
					
					int g = this.calcAvgHex(e, f);
					
					this.color = g;

					
					for(int i = 8; i < 12; i++) {
						powerBase += ItemCatalyst.getPowerAbs(slots[i]);
						powerMod *= ItemCatalyst.getPowerMod(slots[i]);
						heatMod *= ItemCatalyst.getHeatMod(slots[i]);
						fuelMod *= ItemCatalyst.getFuelMod(slots[i]);
					}

					powerBase = ItemAMSCore.getPowerBase(slots[12]);
					heatBase = ItemAMSCore.getHeatBase(slots[12]);
					fuelBase = ItemAMSCore.getFuelBase(slots[12]);
					
					powerBase *= this.efficiency;
					powerBase *= Math.pow(1.25F, booster);
					heatBase *= Math.pow(1.25F, booster);
					heatBase *= (100 - field);
					
					if(this.getFuelPower(tanks[2].getFluid()) > 0 && this.getFuelPower(tanks[3].getFluid()) > 0 &&
							tanks[2].getFluidAmount() > 0 && tanks[3].getFluidAmount() > 0) {

						power += (powerBase * powerMod * gauss(1, (heat - (maxHeat / 2)) / maxHeat)) / 1000 * getFuelPower(tanks[2].getFluid()) * getFuelPower(tanks[3].getFluid());
						System.out.println((powerBase * powerMod * gauss(1, (heat - (maxHeat / 2)) / maxHeat)) / 1000 * getFuelPower(tanks[2].getFluid()) * getFuelPower(tanks[3].getFluid()));
						heat += (heatBase * heatMod) / (float)(this.field / 100F);
						tanks[2].drain((int)(fuelBase * fuelMod), true);
						tanks[3].drain((int)(fuelBase * fuelMod), true);
						
						radiation();

						if(heat > maxHeat) {
							explode();
							heat = maxHeat;
						}
						
						if(field <= 0)
							explode();
					}
				}
				
				if(power > maxPower)
					power = maxPower;
				
				
				if(heat > 0 && tanks[0].getFluidAmount() > 0 && tanks[1].getFluidAmount() > 0) {
					heat -= (this.getCoolingStrength(tanks[0].getFluid()) * this.getCoolingStrength(tanks[1].getFluid()));

					tanks[0].drain(10, true);
					tanks[1].drain(10, true);
					
					if(heat < 0)
						heat = 0;
				}
				
			} else {
				field = 0;
				efficiency = 0;
				power = 0;
				warning = 3;
			}

			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, locked ? 1 : 0, 0));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, color, 1));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, efficiency, 2));
			PacketDispatcher.wrapper.sendToAll(new AuxGaugePacket(xCoord, yCoord, zCoord, field, 3));
		}
	}
	
	private void radiation() {
		
		double maxSize = 5;
		double minSize = 0.5;
		double scale = minSize;
		scale += ((((double)this.tanks[2].getFluidAmount()) / ((double)this.tanks[2].getCapacity())) + (((double)this.tanks[3].getFluidAmount()) / ((double)this.tanks[3].getCapacity()))) * ((maxSize - minSize) / 2);

		scale *= 0.60;
		
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(xCoord - 10 + 0.5, yCoord - 10 + 0.5 + 6, zCoord - 10 + 0.5, xCoord + 10 + 0.5, yCoord + 10 + 0.5 + 6, zCoord + 10 + 0.5));
		
		for(Entity e : list) {
			if(!(e instanceof EntityPlayer && Library.checkForHazmat((EntityPlayer)e)))
				if(!Library.isObstructed(worldObj, xCoord + 0.5, yCoord + 0.5 + 6, zCoord + 0.5, e.posX, e.posY + e.getEyeHeight(), e.posZ)) {
					e.attackEntityFrom(ModDamageSource.ams, 1000);
					e.setFire(3);
				}
		}

		List<Entity> list2 = worldObj.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(xCoord - scale + 0.5, yCoord - scale + 0.5 + 6, zCoord - scale + 0.5, xCoord + scale + 0.5, yCoord + scale + 0.5 + 6, zCoord + scale + 0.5));
		
		for(Entity e : list2) {
			if(!(e instanceof EntityPlayer && Library.checkForHaz2((EntityPlayer)e)))
					e.attackEntityFrom(ModDamageSource.amsCore, 10000);
		}
	}
	
	private void explode() {
		if(!worldObj.isRemote) {
			
			for(int i = 0; i < 10; i++) {

	    		EntityCloudFleijaRainbow cloud = new EntityCloudFleijaRainbow(this.worldObj, 100);
	    		cloud.posX = xCoord + rand.nextInt(201) - 100;
	    		cloud.posY = yCoord + rand.nextInt(201) - 100;
	    		cloud.posZ = zCoord + rand.nextInt(201) - 100;
	    		this.worldObj.spawnEntityInWorld(cloud);
			}
			
			int radius = (int)(50 + (double)(tanks[2].getFluidAmount() + tanks[3].getFluidAmount()) / 16000D * 150);
			
			worldObj.spawnEntityInWorld(EntityNukeExplosionMK4.statFacExperimental(worldObj, radius, xCoord, yCoord, zCoord));
			
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		}
	}
	
	private int getCoolingStrength(FluidStack type) {
		if(type == null)
			return 0;
		else if(type.getFluid() == FluidRegistry.WATER){
			return 5;
		} else if(type.getFluid() == ModForgeFluids.oil){
			return 15;
		} else if(type.getFluid() == ModForgeFluids.coolant){
			return this.heat / 250;
		} else if(type.getFluid() == ModForgeFluids.cryogel){
			return this.heat > heat/2 ? 25 : 5;
		} else {
			return 0;
		}
	}
	
	private int getFuelPower(FluidStack type) {
		if(type == null)
			return 0;
		else if(type.getFluid() == ModForgeFluids.deuterium){
			return 50;
		} else if(type.getFluid() == ModForgeFluids.tritium){
			return 75;
		} else {
			return 0;
		}
	}
	
	private float gauss(float a, float x) {
		
		//Greater values -> less difference of temperate impact
		double amplifier = 0.10;
		
		return (float) ( (1/Math.sqrt(a * Math.PI)) * Math.pow(Math.E, -1 * Math.pow(x, 2)/amplifier) );
	}
	
	private float calcEffect(float a, float x) {
		return (float) (gauss( 1 / a, x / maxHeat) * Math.sqrt(Math.PI * 2) / (Math.sqrt(2) * Math.sqrt(maxPower)));
	}
	
	private float calcField(int a, int b, int c, int d) {
		return (float)(a + b + c + d) * (a * 25 + b * 25 + c * 25 + d  * 25) / 40000;
	}
	
	private int calcAvgHex(int h1, int h2) {

		int r1 = ((h1 & 0xFF0000) >> 16);
		int g1 = ((h1 & 0x00FF00) >> 8);
		int b1 = ((h1 & 0x0000FF) >> 0);
		
		int r2 = ((h2 & 0xFF0000) >> 16);
		int g2 = ((h2 & 0x00FF00) >> 8);
		int b2 = ((h2 & 0x0000FF) >> 0);

		int r = (((r1 + r2) / 2) << 16);
		int g = (((g1 + g2) / 2) << 8);
		int b = (((b1 + b2) / 2) << 0);
		
		return r | g | b;
	}
	
	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}
	
	public int getEfficiencyScaled(int i) {
		return (efficiency * i) / maxEfficiency;
	}
	
	public int getFieldScaled(int i) {
		return (field * i) / maxField;
	}
	
	public int getHeatScaled(int i) {
		return (heat * i) / maxHeat;
	}
	
	public boolean hasResonators() {
		
		if(slots[13] != null && slots[14] != null && slots[15] != null &&
				slots[13].getItem() == ModItems.sat_chip && slots[14].getItem() == ModItems.sat_chip && slots[15].getItem() == ModItems.sat_chip) {
			
		    SatelliteSavedData data = (SatelliteSavedData)worldObj.perWorldStorage.loadData(SatelliteSavedData.class, "satellites");
		    if(data == null) {
		        worldObj.perWorldStorage.setData("satellites", new SatelliteSavedData(worldObj));
		        data = (SatelliteSavedData)worldObj.perWorldStorage.loadData(SatelliteSavedData.class, "satellites");
		    }
		    data.markDirty();

		    int i1 = ItemSatChip.getFreq(slots[13]);
		    int i2 = ItemSatChip.getFreq(slots[14]);
		    int i3 = ItemSatChip.getFreq(slots[15]);
		    
		    if(data.getSatFromFreq(i1) != null && data.getSatFromFreq(i2) != null && data.getSatFromFreq(i3) != null &&
		    		data.getSatFromFreq(i1).satelliteType.getID() == SatelliteType.RESONATOR.getID() && data.getSatFromFreq(i2).satelliteType.getID() == SatelliteType.RESONATOR.getID() && data.getSatFromFreq(i3).satelliteType.getID() == SatelliteType.RESONATOR.getID() &&
		    		i1 != i2 && i1 != i3 && i2 != i3)
		    	return true;
			
		}
		
		return true;
	}


	@Override
	public void ffgeua(int x, int y, int z, boolean newTact) {
		
		Library.ffgeua(x, y, z, newTact, this, worldObj);
	}

	@Override
	public void ffgeuaInit() {
		ffgeua(this.xCoord - 2, this.yCoord, this.zCoord, getTact());
		ffgeua(this.xCoord + 2, this.yCoord, this.zCoord, getTact());
		ffgeua(this.xCoord, this.yCoord, this.zCoord - 2, getTact());
		ffgeua(this.xCoord, this.yCoord, this.zCoord + 2, getTact());
		ffgeua(this.xCoord, this.yCoord - 1, this.zCoord, getTact());
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
		if(resource == null){
			return 0;
		} else if(resource.getFluid() == ModForgeFluids.coolant){
			return tanks[0].fill(resource, doFill);
		} else if(resource.getFluid() == ModForgeFluids.cryogel){
			return tanks[1].fill(resource, doFill);
		} else if(resource.getFluid() == ModForgeFluids.deuterium){
			return tanks[2].fill(resource, doFill);
		} else if(resource.getFluid() == ModForgeFluids.tritium){
			return tanks[3].fill(resource, doFill);
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
		return fluid == ModForgeFluids.coolant || fluid == ModForgeFluids.cryogel || fluid == ModForgeFluids.deuterium || fluid == ModForgeFluids.tritium;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tanks[0].getInfo(), tanks[1].getInfo(), tanks[2].getInfo(), tanks[3].getInfo()};
	}

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 4){
			return;
		} else {
			tanks[0].readFromNBT(tags[0]);
			tanks[1].readFromNBT(tags[1]);
			tanks[2].readFromNBT(tags[2]);
			tanks[3].readFromNBT(tags[3]);
		}
		
	}
}
