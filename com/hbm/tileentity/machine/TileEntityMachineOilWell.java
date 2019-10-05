package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.particle.EntityGasFX;
import com.hbm.explosion.ExplosionLarge;
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

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityMachineOilWell extends TileEntity implements ISidedInventory, IConsumer, IFluidHandler, ITankPacketAcceptor {

	private ItemStack slots[];

	public long power;
	public int warning;
	public int warning2;
	public static final long maxPower = 100000;
	public int age = 0;
	public int age2 = 0;
	public FluidTank[] tanks;
	public Fluid[] tankTypes;
	public boolean needsUpdate;
	
	private static final int[] slots_top = new int[] {1};
	private static final int[] slots_bottom = new int[] {2, 0};
	private static final int[] slots_side = new int[] {0};
	Random rand = new Random();
	
	private String customName;
	
	public TileEntityMachineOilWell() {
		slots = new ItemStack[6];
		tanks = new FluidTank[2];
		tankTypes = new Fluid[2];
		tanks[0] = new FluidTank(64000);
		tankTypes[0] = ModForgeFluids.oil;
		tanks[1] = new FluidTank(64000);
		tankTypes[1] = ModForgeFluids.gas;
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
		return this.hasCustomInventoryName() ? this.customName : "container.oilWell";
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
		this.age = nbt.getInteger("age");

		NBTTagList tankList = nbt.getTagList("tanks", 10);
		for (int i = 0; i < tankList.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte b0 = tag.getByte("tank");
			if (b0 >= 0 && b0 < tanks.length) {
				tanks[b0].readFromNBT(tag);
			}
		}
		tankTypes[0] = ModForgeFluids.oil;
		tankTypes[1] = ModForgeFluids.gas;
		
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
		nbt.setInteger("age", age);
		
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
		
		int timer = 50;
		
		age++;
		age2++;
		if(age >= timer)
			age -= timer;
		if(age2 >= 20)
			age2 -= 20;
		if(!worldObj.isRemote) {
		if(age2 == 9 || age2 == 19) {
			fillFluidInit(tanks[0]);
			fillFluidInit(tanks[1]);
		}
		
		
			
			if(FFUtils.fillFluidContainer(slots, tanks[0], 1, 2))
				needsUpdate = true;
			if(FFUtils.fillFluidContainer(slots, tanks[1], 3, 4))
				needsUpdate = true;

			if(needsUpdate){
				PacketDispatcher.wrapper.sendToAll(new FluidTankPacket(xCoord, yCoord, zCoord, new FluidTank[]{tanks[0], tanks[1]}));
				needsUpdate = false;
			}
			power = Library.chargeTEFromItems(slots, 0, power, maxPower);
			
			if(power >= 100) {
				
				//operation start
				
				if(age == timer - 1) {
					warning = 0;
					
					//warning 0, green: derrick is operational
					//warning 1, red: derrick is full, has no power or the drill is jammed
					//warning 2, yellow: drill has reached max depth
					
					for(int i = this.yCoord - 1; i > this.yCoord - 1 - 100; i--) {
						
						if(i <= 5) {
							//Code 2: The drilling ended
							warning = 2;
							break;
						}
						
						Block b = worldObj.getBlock(this.xCoord, i, this.zCoord);
						if(b == ModBlocks.oil_pipe)
							continue;
						
						if(b == Blocks.air || b == Blocks.grass || b == Blocks.dirt || 
								b == Blocks.stone || b == Blocks.sand || b == Blocks.sandstone || 
								b == Blocks.clay || b == Blocks.hardened_clay || b == Blocks.stained_hardened_clay || 
								b == Blocks.gravel || isOre(b, worldObj.getBlockMetadata(xCoord, i, zCoord)) ||
								b.isReplaceable(worldObj, xCoord, i, zCoord)) {
							worldObj.setBlock(xCoord, i, zCoord, ModBlocks.oil_pipe);
						
							//Code 2: The drilling ended
							if(i == this.yCoord - 100)
								warning = 2;
							break;
							
						} else if((b == ModBlocks.ore_oil || b == ModBlocks.ore_oil_empty) && this.tanks[0].getFluidAmount() < this.tanks[0].getCapacity() && this.tanks[1].getFluidAmount() < this.tanks[1].getCapacity()) {
							if(succ(this.xCoord, i, this.zCoord)) {
								
								this.tanks[0].fill(new FluidStack(tankTypes[0], 500), true);
								this.tanks[1].fill(new FluidStack(tankTypes[1], (100 + rand.nextInt(401))), true);
								needsUpdate = true;
								
								ExplosionLarge.spawnOilSpills(worldObj, xCoord + 0.5F, yCoord + 5.5F, zCoord + 0.5F, 3);
								worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "game.neutral.swim.splash", 2.0F, 0.5F);
								
								break;
							} else {
								worldObj.setBlock(xCoord, i, zCoord, ModBlocks.oil_pipe);
								break;
							}
							
						} else {
							//Code 1: Drill jammed
							warning = 1;
							break;
						}
					}
				}
				
				//operation end
				
				power -= 100;
			} else {
				warning = 1;
			}

			warning2 = 0;
			if(tanks[1].getFluidAmount() > 0) {
				if(slots[5] != null && (slots[5].getItem() == ModItems.fuse || slots[5].getItem() == ModItems.screwdriver)) {
					warning2 = 2;
					tanks[1].drain(50, true);
					needsUpdate = true;
		    		worldObj.spawnEntityInWorld(new EntityGasFX(worldObj, this.xCoord + 0.5F, this.yCoord + 6.5F, this.zCoord + 0.5F, 0.0, 0.0, 0.0));
				} else {
					warning2 = 1;
				}
			}

			PacketDispatcher.wrapper.sendToAll(new AuxElectricityPacket(xCoord, yCoord, zCoord, power));
		}
		
	}
	
	public boolean isOre(Block b, int meta) {
		
		int[] ids = OreDictionary.getOreIDs(new ItemStack(b, 1, meta));
		
		for(int i = 0; i < ids.length; i++) {
			
			String s = OreDictionary.getOreName(ids[i]);
			
			if(s.length() > 3 && s.substring(0, 3).equals("ore"))
				return true;
		}
		
		return false;
	}
	
	public boolean succ(int x, int y, int z) {
		
		list.clear();
		
		succ1(x, y, z);
		succ2(x, y, z);
		
		if(!list.isEmpty()) {
			
			int i = rand.nextInt(list.size());
			int a = list.get(i)[0];
			int b = list.get(i)[1];
			int c = list.get(i)[2];
			
			if(worldObj.getBlock(a, b, c) == ModBlocks.ore_oil) {
				
				worldObj.setBlock(a, b, c, ModBlocks.ore_oil_empty);
				return true;
			}
		}
		
		return false;
	}
	
	public void succInit1(int x, int y, int z) {
		succ1(x + 1, y, z);
		succ1(x - 1, y, z);
		succ1(x, y + 1, z);
		succ1(x, y - 1, z);
		succ1(x, y, z + 1);
		succ1(x, y, z - 1);
	}
	
	public void succInit2(int x, int y, int z) {
		succ2(x + 1, y, z);
		succ2(x - 1, y, z);
		succ2(x, y + 1, z);
		succ2(x, y - 1, z);
		succ2(x, y, z + 1);
		succ2(x, y, z - 1);
	}
	
	List<int[]> list = new ArrayList<int[]>();
	
	public void succ1(int x, int y, int z) {
		if(worldObj.getBlock(x, y, z) == ModBlocks.ore_oil_empty && 
				worldObj.getBlockMetadata(x, y, z) == 0) {
			worldObj.setBlockMetadataWithNotify(x, y, z, 1, 2);
			succInit1(x, y, z);
		}
	}
	
	public void succ2(int x, int y, int z) {
		if(worldObj.getBlock(x, y, z) == ModBlocks.ore_oil_empty && 
				worldObj.getBlockMetadata(x, y, z) == 1) {
			worldObj.setBlockMetadataWithNotify(x, y, z, 0, 2);
			succInit2(x, y, z);
		} else if(worldObj.getBlock(x, y, z) == ModBlocks.ore_oil) {
			list.add(new int[] { x, y, z });
		}
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
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord - 2, this.yCoord, this.zCoord, 2000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord + 2, this.yCoord, this.zCoord, 2000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord - 2, 2000) || needsUpdate;
		needsUpdate = FFUtils.fillFluid(this, tank, worldObj, this.xCoord, this.yCoord, this.zCoord + 2, 2000) || needsUpdate;
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
		// can't be filled
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource == null){
			return null;
		} else if(resource.getFluid() == tankTypes[0]){
			return tanks[0].drain(resource.amount, doDrain);
		} else if(resource.getFluid() == tankTypes[1]){
			return tanks[1].drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(tanks[0].getFluidAmount() > 0){
			return tanks[0].drain(maxDrain, doDrain);
		} else if(tanks[1].getFluidAmount() > 0){
			return tanks[1].drain(maxDrain, doDrain);
		} else {
			return null;
		}
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		// can't be filled
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == tankTypes[0] || fluid == tankTypes[1];
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{tanks[0].getInfo(), tanks[1].getInfo()};
	}

}
