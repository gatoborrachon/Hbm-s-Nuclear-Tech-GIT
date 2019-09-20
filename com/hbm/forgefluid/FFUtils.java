package com.hbm.forgefluid;

import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.handler.FluidTypeHandler.FluidType;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidDuct;
import com.hbm.interfaces.IFluidPipe;
import com.hbm.interfaces.IFluidSource;
import com.hbm.inventory.gui.GuiInfoContainer;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class FFUtils {
	public int sendFluid(FluidStack fluid, boolean doFill, List<IFluidHandler> list) {
		
		return 0;
	}
	
	public static void drawLiquid(FluidTank tank, int guiLeft, int guiTop,
			float zLevel, int sizeX, int sizeY, int offsetX, int offsetY) {
		if (tank.getFluid() != null) {
			IIcon liquidIcon = tank.getFluid().getFluid().getStillIcon();
			
			if (liquidIcon != null) {
				int level = (int) (((double) tank.getFluidAmount() / (double) tank
						.getCapacity()) * 52.0D);

				drawFull(tank, guiLeft, guiTop, zLevel, liquidIcon, level,
						sizeX, sizeY, offsetX, offsetY);
			}
		}
	}

	public static void drawFull(FluidTank tank, int guiLeft, int guiTop,
			float zLevel, IIcon liquidIcon, int level, int sizeX, int sizeY,
			int offsetX, int offsetY) {
		int color = tank.getFluid().getFluid().getColor();

		float left = liquidIcon.getMinU();
		float right = liquidIcon.getMaxU();
		float up = liquidIcon.getMinV();
		float down = liquidIcon.getMaxV();
		float right2 = liquidIcon.getInterpolatedU(2);
		float up2 = liquidIcon.getInterpolatedV(16 - (level % 16));
		int number = Math.floorDiv(level, 16);

		Tessellator tes = Tessellator.instance;
		tes.startDrawingQuads();
		tes.setColorOpaque_I(color);
		// pixels 17 to 69 (-52)

		for (int i = 0; i < number; i++) {
			tes.addVertexWithUV(guiLeft + offsetX, guiTop + offsetY - 16
					- (i * 16), zLevel, left, up);
			tes.addVertexWithUV(guiLeft + offsetX, guiTop + offsetY - (i * 16),
					zLevel, left, down);
			tes.addVertexWithUV(guiLeft + offsetX + 16, guiTop + offsetY
					- (i * 16), zLevel, right, down);
			tes.addVertexWithUV(guiLeft + offsetX + 16, guiTop + offsetY - 16
					- (i * 16), zLevel, right, up);

			/*
			 * tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - 16 - (i *
			 * 16), zLevel, left, up); tes.addVertexWithUV(guiLeft + 71 + 16,
			 * guiTop + 69 - (i * 16), zLevel, left, down);
			 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (i * 16),
			 * zLevel, right, down); tes.addVertexWithUV(guiLeft + 71 + 32,
			 * guiTop + 69 - 16 - (i * 16), zLevel, right, up);
			 * 
			 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - 16 - (i *
			 * 16), zLevel, left, up); tes.addVertexWithUV(guiLeft + 71 + 32,
			 * guiTop + 69 - (i * 16), zLevel, left, down);
			 * tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (i * 16),
			 * zLevel, right2, down); tes.addVertexWithUV(guiLeft + 71 + 34,
			 * guiTop + 69 - 16 - (i * 16), zLevel, right2, up);
			 */
		}
		tes.addVertexWithUV(guiLeft + offsetX, guiTop + offsetY - (number * 16)
				- (level % 16), zLevel, left, up2);
		tes.addVertexWithUV(guiLeft + offsetX,
				guiTop + offsetY - (number * 16), zLevel, left, down);
		tes.addVertexWithUV(guiLeft + offsetX + 16, guiTop + offsetY
				- (number * 16), zLevel, right, down);
		tes.addVertexWithUV(guiLeft + offsetX + 16, guiTop + offsetY
				- (number * 16) - (level % 16), zLevel, right, up2);

		/*
		 * tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (number * 16) -
		 * (level % 16), zLevel, left, up2); tes.addVertexWithUV(guiLeft + 71 +
		 * 16, guiTop + 69 - (number * 16), zLevel, left, down);
		 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16),
		 * zLevel, right, down); tes.addVertexWithUV(guiLeft + 71 + 32, guiTop +
		 * 69 - (number * 16) - (level % 16), zLevel, right, up2);
		 * 
		 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16) -
		 * (level % 16), zLevel, left, up2); tes.addVertexWithUV(guiLeft + 71 +
		 * 32, guiTop + 69 - (number * 16), zLevel, left, down);
		 * tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (number * 16),
		 * zLevel, right2, down); tes.addVertexWithUV(guiLeft + 71 + 34, guiTop
		 * + 69 - (number * 16) - (level % 16), zLevel, right2, up2);
		 */

		tes.draw();

	}

	/**
	 * Renders tank info, like fluid type and millibucket amount. Same as the hbm one, just centrallized to a utility file.
	 * @param gui - the gui to render the fluid info on
	 * @param mouseX - the cursor's x position
	 * @param mouseY - the cursor's y position
	 * @param x - the x left corner of where to render the info
	 * @param y - the y top corner of where to render the info
	 * @param width - how wide the area to render info inside is
	 * @param height - how tall the area to render info inside is
	 * @param fluidTank - the tank to render info of
	 */
	public static void renderTankInfo(GuiInfoContainer gui, int mouseX,
			int mouseY, int x, int y, int width, int height, FluidTank fluidTank) {
		if (x <= mouseX && x + width > mouseX && y < mouseY
				&& y + height >= mouseY) {
			if (fluidTank.getFluid() != null) {
				gui.drawFluidInfo(
						new String[] {
								I18n.format(fluidTank.getFluid().getFluid()
										.getUnlocalizedName()),
								fluidTank.getFluidAmount() + "/"
										+ fluidTank.getCapacity() + "mB" },
						mouseX, mouseY);
			} else {
				gui.drawFluidInfo(new String[] {I18n.format("None"), fluidTank.getFluidAmount() + "/" + fluidTank.getCapacity() + "mB" }, mouseX, mouseY);
			}
		}
	}
	
	public static boolean checkFluidConnectables(World world, int x, int y, int z, FFPipeNetwork net)
	{
		TileEntity tileentity = world.getTileEntity(x, y, z);
		if(tileentity != null && tileentity instanceof IFluidPipe && ((IFluidPipe)tileentity).getNetworkTrue() != null && ((IFluidPipe)tileentity).getNetworkTrue() == net)
			return true;
		if(tileentity != null && !(tileentity instanceof IFluidPipe) && tileentity instanceof IFluidHandler)
		{
			return true;
		}
		return false;
	}
	
	public static Slot[] transferFluidToItem(){
		
		return null;
	}

	/**
	 * Fills a tank from a fluid handler item.
	 * @param slots - the slot inventory
	 * @param tank - the tank to be filled
	 * @param slot1 - the slot with the full container
	 * @param slot2 - the output slot
	 */
	public static boolean fillFluidContainer(ItemStack[] slots, FluidTank tank, int slot1, int slot2) {
		if(slots == null || tank == null || tank.getFluid() == null || slots.length < slot1 || slots.length < slot2 || slots[slot1] == null){
			return false;
		}
		if(slots[slot1].getItem() instanceof IFluidContainerItem){
			
		} else if (FluidContainerRegistry.isEmptyContainer(slots[slot1])){
			if(FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[slot1]) != null) {
				FluidStack fStack = tank.getFluid();
				if(fillEmpty(slots, slot1, slot2, fStack)) {
					tank.drain(FluidContainerRegistry.getFluidForFilledItem(slots[slot2]).amount, true);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Fills a fluid handling item from a tank
	 * @param slots - the slot inventory
	 * @param tank - the tank to fill from
	 * @param slot1 - the slot with an empty container
	 * @param slot2 - the output slot.
	 */
	public static boolean fillFromFluidContainer(ItemStack[] slots, FluidTank tank, int slot1, int slot2){
		
		if(slots == null || tank == null || slots.length < slot1 || slots.length < slot2 || slots[slot1] == null){
			return false;
		}
		if(slots[slot1].getItem() instanceof IFluidContainerItem){
			
			tank.fill(((IFluidContainerItem)slots[slot1].getItem()).drain(slots[slot1], Math.min(6000, tank.getCapacity() - tank.getFluidAmount()), true), true);
			if(((IFluidContainerItem)slots[slot1].getItem()).getFluid(slots[slot2]) == null)
				moveItems(slots, slot2, slot2);
			return true;
				
		} else if(FluidContainerRegistry.isFilledContainer(slots[slot1]) && FluidContainerRegistry.getFluidForFilledItem(slots[slot1]) != null){
			if(tank.getCapacity() - tank.getFluidAmount() >= FluidContainerRegistry.getContainerCapacity(slots[slot1]) && (tank.getFluid() == null || tank.getFluid().getFluid() == FluidContainerRegistry.getFluidForFilledItem(slots[slot1]).getFluid())){
				ItemStack temp = slots[slot1];
				if(moveFullToEmpty(slots, slot1, slot2)) {
					tank.fill(FluidContainerRegistry.getFluidForFilledItem(temp), true);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean moveFullToEmpty(ItemStack[] slots, int in, int out) {
		if(slots[in] != null && FluidContainerRegistry.drainFluidContainer(slots[in]) != null){
			if(slots[out] == null){
				slots[out] = FluidContainerRegistry.drainFluidContainer(slots[in]);
				slots[in].stackSize --;
				if(slots[in].stackSize <= 0){
					slots[in] = null;
				}
				return true;
			} else if(slots[out] != null && slots[out].getItem() == FluidContainerRegistry.drainFluidContainer(slots[in]).getItem() && slots[out].stackSize < slots[out].getMaxStackSize()){
				slots[in].stackSize--;
				if(slots[in].stackSize <= 0)
					slots[in] = null;
				slots[out].stackSize++;
				return true;
			}
		}
		return false;
	}
	
	private static boolean fillEmpty(ItemStack[] slots, int in, int out, FluidStack fStack) {
		if(slots[in] != null && FluidContainerRegistry.fillFluidContainer(fStack, slots[in]) != null){
			if(slots[out] == null){
				slots[out] = FluidContainerRegistry.fillFluidContainer(fStack, slots[in]);
				slots[in].stackSize --;
				if(slots[in].stackSize <= 0){
					slots[in] = null;
				}
				return true;
			} else if(slots[out] != null && slots[out].getItem() == FluidContainerRegistry.fillFluidContainer(fStack, slots[in]).getItem() && slots[out].stackSize < slots[out].getMaxStackSize()){
				slots[in].stackSize--;
				if(slots[in].stackSize <= 0)
					slots[in] = null;
				slots[out].stackSize++;
				return true;
			}
		}
		return false;
	}
	
	private static boolean moveItems(ItemStack[] slots, int in, int out) {
		if(slots[in] != null){
			if(slots[out] == null){
				slots[out] = slots[in];
				slots[in] = null;
				return true;
			} else {
				int amountToTransfer = Math.min(slots[out].getMaxStackSize() - slots[out].stackSize, slots[in].stackSize);
				slots[in].stackSize -= amountToTransfer;
				if(slots[in].stackSize <= 0)
					slots[in] = null;
				slots[out].stackSize =+ amountToTransfer;
				return true;
			}
		}
		return false;
	}
}
