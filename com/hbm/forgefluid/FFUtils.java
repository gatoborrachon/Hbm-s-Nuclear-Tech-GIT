package com.hbm.forgefluid;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.hbm.interfaces.IFluidPipe;
import com.hbm.inventory.gui.GuiInfoContainer;
import com.hbm.tileentity.machine.TileEntityDummy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

public class FFUtils {

	/**
	 * Tessellates a liquid texture across a rectangle without looking weird and stretched.
	 * @param tank - the tank with the fluid to render
	 * @param guiLeft - the left side of the gui
	 * @param guiTop - the top of the gui
	 * @param zLevel - the z level of the gui
	 * @param sizeX - how big the rectangle should be
	 * @param sizeY - how tall the rectangle should be
	 * @param offsetX - where the starting x of the rectangle should be on screen
	 * @param offsetY - where the starting y of the rectangle should be on screen
	 */
	public static void drawLiquid(FluidTank tank, int guiLeft, int guiTop, float zLevel, int sizeX, int sizeY,
			int offsetX, int offsetY) {
		if (tank.getFluid() != null) {
			IIcon liquidIcon = tank.getFluid().getFluid().getStillIcon();

			if (liquidIcon != null) {
				int level = (int) (((double) tank.getFluidAmount() / (double) tank.getCapacity()) * sizeY);

				drawFull(tank, guiLeft, guiTop, zLevel, liquidIcon, level, sizeX, offsetX, offsetY);
			}
		}
	}

	/**
	 * Internal method to actually render the fluid
	 * @param tank
	 * @param guiLeft
	 * @param guiTop
	 * @param zLevel
	 * @param liquidIcon
	 * @param level
	 * @param sizeX
	 * @param offsetX
	 * @param offsetY
	 */
	private static void drawFull(FluidTank tank, int guiLeft, int guiTop, float zLevel, IIcon liquidIcon, int level,
			int sizeX, int offsetX, int offsetY) {
		int color = tank.getFluid().getFluid().getColor();

		float left = liquidIcon.getMinU();
		float right = liquidIcon.getMaxU();
		float up = liquidIcon.getMinV();
		float down = liquidIcon.getMaxV();
		float right2 = liquidIcon.getInterpolatedU(16 - (sizeX % 16));
		float up2 = liquidIcon.getInterpolatedV(16 - (level % 16));
		int tall = Math.floorDiv(level, 16);
		int thick = Math.floorDiv(sizeX, 16);
		int tallExtraPixels = level % 16;
		int thickExtraPixels = sizeX % 16;

		Tessellator tes = Tessellator.instance;
		tes.startDrawingQuads();
		tes.setColorOpaque_I(color);
		//Draw main area
		for (int j = 0; j < thick; j++) {
			for (int i = 0; i < tall; i++) {
				tes.addVertexWithUV(guiLeft + offsetX + (j*16), guiTop + offsetY - 16 - (i * 16), zLevel, left, up);
				tes.addVertexWithUV(guiLeft + offsetX + (j*16), guiTop + offsetY - (i * 16), zLevel, left, down);
				tes.addVertexWithUV(guiLeft + offsetX + 16 + (j*16), guiTop + offsetY - (i * 16), zLevel, right, down);
				tes.addVertexWithUV(guiLeft + offsetX + 16 + (j*16), guiTop + offsetY - 16 - (i * 16), zLevel, right, up);

				/*
				 * tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - 16 - (i * 16), zLevel,
				 * left, up); tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (i * 16),
				 * zLevel, left, down); tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (i
				 * * 16), zLevel, right, down); tes.addVertexWithUV(guiLeft + 71 + 32, guiTop +
				 * 69 - 16 - (i * 16), zLevel, right, up);
				 * 
				 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - 16 - (i * 16), zLevel,
				 * left, up); tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (i * 16),
				 * zLevel, left, down); tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (i
				 * * 16), zLevel, right2, down); tes.addVertexWithUV(guiLeft + 71 + 34, guiTop +
				 * 69 - 16 - (i * 16), zLevel, right2, up);
				 */
			}
		}
		//Draw top area
		for(int i = 0; i < thick; i++){
			tes.addVertexWithUV(guiLeft + offsetX + (i*16), guiTop + offsetY - (tall*16) - tallExtraPixels, zLevel, left, up2);
			tes.addVertexWithUV(guiLeft + offsetX + (i*16), guiTop + offsetY - (tall*16), zLevel, left, down);
			tes.addVertexWithUV(guiLeft + offsetX + 16 + (i*16), guiTop + offsetY - (tall*16), zLevel, right, down);
			tes.addVertexWithUV(guiLeft + offsetX + 16 + (i*16), guiTop + offsetY - (tall*16) - tallExtraPixels, zLevel, right, up2);
		}
		//Draw wideness extra area
		for(int i = 0; i < tall; i++){
			tes.addVertexWithUV(guiLeft + offsetX + thick*16, guiTop + offsetY - 16 - (i*16), zLevel, left, up);
			tes.addVertexWithUV(guiLeft + offsetX + thick*16, guiTop + offsetY - (i*16), zLevel, left, down);
			tes.addVertexWithUV(guiLeft + offsetX + thick*16 + thickExtraPixels, guiTop + offsetY - (i*16), zLevel, right2, down);
			tes.addVertexWithUV(guiLeft + offsetX + thick*16 + thickExtraPixels, guiTop + offsetY - 16 - (i*16), zLevel, right2, up);
		}
		//Draw bit to complete the square
		tes.addVertexWithUV(guiLeft + offsetX + (thick*16), guiTop + offsetY - (tall*16) - tallExtraPixels, zLevel, left, up2);
		tes.addVertexWithUV(guiLeft + offsetX + (thick*16), guiTop + offsetY - (tall*16), zLevel, left, down);
		tes.addVertexWithUV(guiLeft + offsetX + (thick*16) + thickExtraPixels, guiTop + offsetY - (tall*16), zLevel, right2, down);
		tes.addVertexWithUV(guiLeft + offsetX + (thick*16) + thickExtraPixels, guiTop + offsetY - (tall*16) - tallExtraPixels, zLevel, right2, up2);
		
		
	/*	
		tes.addVertexWithUV(guiLeft + offsetX, guiTop + offsetY - (number * 16) - (level % 16), zLevel, left, up2);
		tes.addVertexWithUV(guiLeft + offsetX, guiTop + offsetY - (number * 16), zLevel, left, down);
		tes.addVertexWithUV(guiLeft + offsetX + 16, guiTop + offsetY - (number * 16), zLevel, right, down);
		tes.addVertexWithUV(guiLeft + offsetX + 16, guiTop + offsetY - (number * 16) - (level % 16), zLevel, right, up2);
	 */
		/*
		 * tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (number * 16) - (level %
		 * 16), zLevel, left, up2); tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 -
		 * (number * 16), zLevel, left, down); tes.addVertexWithUV(guiLeft + 71 + 32,
		 * guiTop + 69 - (number * 16), zLevel, right, down);
		 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16) - (level %
		 * 16), zLevel, right, up2);
		 * 
		 * tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16) - (level %
		 * 16), zLevel, left, up2); tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 -
		 * (number * 16), zLevel, left, down); tes.addVertexWithUV(guiLeft + 71 + 34,
		 * guiTop + 69 - (number * 16), zLevel, right2, down);
		 * tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (number * 16) - (level %
		 * 16), zLevel, right2, up2);
		 */

		tes.draw();

	}

	/**
	 * Renders tank info, like fluid type and millibucket amount. Same as the hbm
	 * one, just centrallized to a utility file.
	 * 
	 * @param gui       - the gui to render the fluid info on
	 * @param mouseX    - the cursor's x position
	 * @param mouseY    - the cursor's y position
	 * @param x         - the x left corner of where to render the info
	 * @param y         - the y top corner of where to render the info
	 * @param width     - how wide the area to render info inside is
	 * @param height    - how tall the area to render info inside is
	 * @param fluidTank - the tank to render info of
	 */
	public static void renderTankInfo(GuiInfoContainer gui, int mouseX, int mouseY, int x, int y, int width, int height,
			FluidTank fluidTank) {
		if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			if (fluidTank.getFluid() != null) {
				gui.drawFluidInfo(new String[] { ""
						+ (StatCollector.translateToLocal(fluidTank.getFluid().getFluid().getUnlocalizedName())).trim(),
						fluidTank.getFluidAmount() + "/" + fluidTank.getCapacity() + "mB" }, mouseX, mouseY);
			} else {
				gui.drawFluidInfo(new String[] { I18n.format("None"),
						fluidTank.getFluidAmount() + "/" + fluidTank.getCapacity() + "mB" }, mouseX, mouseY);
			}
		}
	}

	public static void renderTankInfo(GuiInfoContainer gui, int mouseX, int mouseY, int x, int y, int width, int height,
			FluidTank fluidTank, Fluid fluid) {
		if (x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY) {
			if (fluid != null) {
				gui.drawFluidInfo(
						new String[] { "" + (StatCollector.translateToLocal(fluid.getUnlocalizedName())).trim(),
								fluidTank.getFluidAmount() + "/" + fluidTank.getCapacity() + "mB" },
						mouseX, mouseY);
			} else {
				gui.drawFluidInfo(new String[] { I18n.format("None"),
						fluidTank.getFluidAmount() + "/" + fluidTank.getCapacity() + "mB" }, mouseX, mouseY);
			}
		}
	}

	public static boolean checkFluidConnectables(World world, int x, int y, int z, FFPipeNetwork net) {
		TileEntity tileentity = world.getTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof IFluidPipe && ((IFluidPipe) tileentity).getNetworkTrue() == net)
			return true;
		if (tileentity != null && !(tileentity instanceof IFluidPipe) && tileentity instanceof IFluidHandler) {
			return true;
		}
		return false;
	}

	/**
	 * Replacement method for the old method of transferring fluids out of a machine
	 * 
	 * @param tileEntity - the tile entity it is filling from
	 * @param tank       - the fluid tank to fill from
	 * @param world      - the world the filling is taking place in
	 * @param i          - x coord of place to fill
	 * @param j          - y coord of place to fill
	 * @param k          - z coord of place to fill
	 * @param maxDrain   - the maximum amount that can be drained from the tank at a
	 *                   time
	 * @return Whether something was actually filled or not, or whether it needs 
	 * an
	 *         update
	 */
	public static boolean fillFluid(TileEntity tileEntity, FluidTank tank, World world, int i, int j, int k,
			int maxDrain) {

		if (tank.getFluidAmount() <= 0 || tank.getFluid() == null) {
			return false;
		}
	
		TileEntity te = world.getTileEntity(i, j, k);
		System.out.println(i + " " + j + " " + k);
		if (te != null && te instanceof IFluidHandler) {
			if (te instanceof TileEntityDummy) {
				TileEntityDummy ted = (TileEntityDummy) te;
				if (world.getTileEntity(ted.targetX, ted.targetY, ted.targetZ) == tileEntity) {
					return false;
				}
			}
			IFluidHandler tef = (IFluidHandler) te;
			tank.drain(tef.fill(ForgeDirection.UNKNOWN,
					new FluidStack(tank.getFluid(), Math.min(maxDrain, tank.getFluidAmount())), true), true);
			
			return true;
		}
		return false;
	}
	
	public static void testing(FluidTank tank){
		
	}

	/**
	 * Fills a tank from a fluid handler item.
	 * 
	 * @param slots - the slot inventory
	 * @param tank  - the tank to be filled
	 * @param slot1 - the slot with the full container
	 * @param slot2 - the output slot
	 */
	public static boolean fillFluidContainer(ItemStack[] slots, FluidTank tank, int slot1, int slot2) {
		if (slots == null || tank == null || tank.getFluid() == null || slots.length < slot1 || slots.length < slot2
				|| slots[slot1] == null) {
			return false;
		}
		if (slots[slot1].getItem() instanceof IFluidContainerItem) {
			boolean returnValue = false;
			if (((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1]) == null
					|| ((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1]).getFluid() == tank
							.getFluid().getFluid()) {
				tank.drain(((IFluidContainerItem) slots[slot1].getItem()).fill(slots[slot1],
						new FluidStack(tank.getFluid(), Math.min(6000, tank.getFluidAmount())), true), true);
				returnValue = true;
			}
			if (((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1]) != null
					&& (tank.getFluid() == null
							|| ((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1]).getFluid() != tank
									.getFluid().getFluid()
							|| ((IFluidContainerItem) slots[slot1].getItem())
									.getFluid(slots[slot1]).amount >= ((IFluidContainerItem) slots[slot1].getItem())
											.getCapacity(slots[slot1]))) {
				moveItems(slots, slot1, slot2);
			}
			return returnValue;
		} else if (FluidContainerRegistry.isEmptyContainer(slots[slot1])) {
			if (FluidContainerRegistry.fillFluidContainer(tank.getFluid(), slots[slot1]) != null) {
				FluidStack fStack = tank.getFluid();
				if (fillEmpty(slots, slot1, slot2, fStack)) {
					tank.drain(FluidContainerRegistry.getFluidForFilledItem(slots[slot2]).amount, true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Fills a fluid handling item from a tank
	 * 
	 * @param slots - the slot inventory
	 * @param tank  - the tank to fill from
	 * @param slot1 - the slot with an empty container
	 * @param slot2 - the output slot.
	 * @return true if something was actually filled
	 */
	public static boolean fillFromFluidContainer(ItemStack[] slots, FluidTank tank, int slot1, int slot2) {

		if (slots == null || tank == null || slots.length < slot1 || slots.length < slot2 || slots[slot1] == null) {
			return false;
		}
		if (slots[slot1].getItem() instanceof IFluidContainerItem) {
			boolean returnValue = false;
			if (((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1]) != null
					&& (tank.getFluid() == null || ((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1])
							.getFluid() == tank.getFluid().getFluid())) {
				tank.fill(((IFluidContainerItem) slots[slot1].getItem()).drain(slots[slot1],
						Math.min(6000, tank.getCapacity() - tank.getFluidAmount()), true), true);
				returnValue = true;
			}
			if (((IFluidContainerItem) slots[slot1].getItem()).getFluid(slots[slot1]) == null)
				moveItems(slots, slot1, slot2);
			return returnValue;
		} else if (FluidContainerRegistry.isFilledContainer(slots[slot1])
				&& FluidContainerRegistry.getFluidForFilledItem(slots[slot1]) != null) {
			if (tank.getCapacity() - tank.getFluidAmount() >= FluidContainerRegistry.getContainerCapacity(slots[slot1])
					&& (tank.getFluid() == null || tank.getFluid().getFluid() == FluidContainerRegistry
							.getFluidForFilledItem(slots[slot1]).getFluid())) {
				ItemStack temp = slots[slot1];
				if (moveFullToEmpty(slots, slot1, slot2)) {
					tank.fill(FluidContainerRegistry.getFluidForFilledItem(temp), true);
					return true;
				}
			}
		}
		return false;
	}

	private static boolean moveFullToEmpty(ItemStack[] slots, int in, int out) {
		if (slots[in] != null && FluidContainerRegistry.drainFluidContainer(slots[in]) != null) {
			if (slots[out] == null) {
				slots[out] = FluidContainerRegistry.drainFluidContainer(slots[in]);
				slots[in].stackSize--;
				if (slots[in].stackSize <= 0) {
					slots[in] = null;
				}
				return true;
			} else if (slots[out] != null
					&& slots[out].getItem() == FluidContainerRegistry.drainFluidContainer(slots[in]).getItem()
					&& slots[out].stackSize < slots[out].getMaxStackSize()) {
				slots[in].stackSize--;
				if (slots[in].stackSize <= 0)
					slots[in] = null;
				slots[out].stackSize++;
				return true;
			}
		}
		return false;
	}

	private static boolean fillEmpty(ItemStack[] slots, int in, int out, FluidStack fStack) {
		if (slots[in] != null && FluidContainerRegistry.fillFluidContainer(fStack, slots[in]) != null) {
			if (slots[out] == null) {
				slots[out] = FluidContainerRegistry.fillFluidContainer(fStack, slots[in]);
				slots[in].stackSize--;
				if (slots[in].stackSize <= 0) {
					slots[in] = null;
				}
				return true;
			} else if (slots[out] != null
					&& slots[out].getItem() == FluidContainerRegistry.fillFluidContainer(fStack, slots[in]).getItem()
					&& slots[out].stackSize < slots[out].getMaxStackSize()) {
				slots[in].stackSize--;
				if (slots[in].stackSize <= 0)
					slots[in] = null;
				slots[out].stackSize++;
				return true;
			}
		}
		return false;
	}

	private static boolean moveItems(ItemStack[] slots, int in, int out) {
		if (slots[in] != null) {

			if (slots[out] == null) {

				slots[out] = slots[in];
				slots[in] = null;
				return true;
			} else {
				int amountToTransfer = Math.min(slots[out].getMaxStackSize() - slots[out].stackSize,
						slots[in].stackSize);
				slots[in].stackSize -= amountToTransfer;
				if (slots[in].stackSize <= 0)
					slots[in] = null;
				slots[out].stackSize += amountToTransfer;
				return true;
			}
		}
		return false;
	}
}
