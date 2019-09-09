package com.hbm.inventory.gui;

import org.lwjgl.opengl.GL11;

import com.hbm.inventory.container.ContainerMachineFluidTank;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityMachineFluidTank;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class GUIMachineFluidTank extends GuiInfoContainer {
	
	private static ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/gui_tank.png");
	private TileEntityMachineFluidTank tank;

	public GUIMachineFluidTank(InventoryPlayer invPlayer, TileEntityMachineFluidTank tedf) {
		super(new ContainerMachineFluidTank(invPlayer, tedf));
		tank = tedf;
		
		this.xSize = 176;
		this.ySize = 166;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
		super.drawScreen(mouseX, mouseY, f);

		//tank.tank.renderTankInfo(this, mouseX, mouseY, guiLeft + 71, guiTop + 69 - 52, 34, 52);
		this.renderTankInfo(mouseX, mouseY, guiLeft + 71, guiTop + 69 - 52, 34, 52);
		
		String[] text = new String[] {
				"Inserting a fuse into the marked",
				"slot will set the tank to output mode" };
		this.drawCustomInfoStat(mouseX, mouseY, guiLeft - 16, guiTop + 36, 16, 16, guiLeft - 8, guiTop + 36 + 16, text);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		String name = this.tank.hasCustomInventoryName() ? this.tank.getInventoryName() : I18n.format(this.tank.getInventoryName());
		
		this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if(tank.dna())
			drawTexturedModalRect(guiLeft + 152, guiTop + 53, 176, 0, 16, 16);
		
		
		
		this.drawInfoPanel(guiLeft - 16, guiTop + 36, 16, 16, 2);
		this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		drawLiquid();
		this.mc.getTextureManager().bindTexture(texture);
		drawTexturedModalRect(guiLeft + 71, guiTop + 17, 0, 204, 34, 52);

	//	Minecraft.getMinecraft().getTextureManager().bindTexture(tank.tank.getSheet());
	//	tank.tank.renderTank(this, guiLeft + 71, guiTop + 69, tank.tank.getTankType().textureX() * FluidTank.x, tank.tank.getTankType().textureY() * FluidTank.y, 16, 52);
	//	tank.tank.renderTank(this, guiLeft + 71 + 16, guiTop + 69, tank.tank.getTankType().textureX() * FluidTank.x, tank.tank.getTankType().textureY() * FluidTank.y, 16, 52);
	//	tank.tank.renderTank(this, guiLeft + 71 + 32, guiTop + 69, tank.tank.getTankType().textureX() * FluidTank.x, tank.tank.getTankType().textureY() * FluidTank.y, 2, 52);
	}

	

	private void drawLiquid() {
		if(tank.tank.getFluid() != null){
		IIcon liquidIcon = tank.tank.getFluid().getFluid().getStillIcon();
		if(liquidIcon != null){
			int level = (int) (((double)tank.tank.getFluidAmount() / (double)tank.tank.getCapacity()) * 52.0D);
		
			drawFull(liquidIcon, level);
		}
	}
	}

	private void drawFull(IIcon liquidIcon, int level) {
		int color = tank.tank.getFluid().getFluid().getColor();
		
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
		//pixels 17 to 69 (-52)
		
		for(int i = 0; i < number; i++){
		tes.addVertexWithUV(guiLeft + 71, guiTop + 69 - 16 - (i * 16), this.zLevel, left, up);
		tes.addVertexWithUV(guiLeft + 71, guiTop + 69 - (i * 16), this.zLevel, left, down);
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (i * 16), this.zLevel, right, down);
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - 16 - (i * 16), this.zLevel, right, up);
	
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - 16 - (i * 16), this.zLevel, left, up);
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (i * 16), this.zLevel, left, down);
		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (i * 16), this.zLevel, right, down);
		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - 16 - (i * 16), this.zLevel, right, up);

		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - 16 - (i * 16), this.zLevel, left, up);
		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (i * 16), this.zLevel, left, down);
		tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (i * 16), this.zLevel, right2, down);
		tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - 16 - (i * 16), this.zLevel, right2, up);
		}
		tes.addVertexWithUV(guiLeft + 71, guiTop + 69 - (number * 16) - (level % 16), this.zLevel, left, up2);
		tes.addVertexWithUV(guiLeft + 71, guiTop + 69 - (number * 16), this.zLevel, left, down);
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (number * 16), this.zLevel, right, down);
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (number * 16) - (level % 16), this.zLevel, right, up2);
	
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (number * 16) - (level % 16), this.zLevel, left, up2);
		tes.addVertexWithUV(guiLeft + 71 + 16, guiTop + 69 - (number * 16), this.zLevel, left, down);
		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16), this.zLevel, right, down);
		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16) - (level % 16), this.zLevel, right, up2);

		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16) - (level % 16), this.zLevel, left, up2);
		tes.addVertexWithUV(guiLeft + 71 + 32, guiTop + 69 - (number * 16), this.zLevel, left, down);
		tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (number * 16), this.zLevel, right2, down);
		tes.addVertexWithUV(guiLeft + 71 + 34, guiTop + 69 - (number * 16) - (level % 16), this.zLevel, right2, up2);

		tes.draw();
		
	}
	public void renderTankInfo(int mouseX, int mouseY, int x, int y, int width, int height) {
		if(x <= mouseX && x + width > mouseX && y < mouseY && y + height >= mouseY){
			if(tank.tank.getFluid() != null){
			this.drawFluidInfo(new String[] { I18n.format(tank.tank.getInfo().fluid.getUnlocalizedName()), tank.tank.getFluidAmount() + "/" + tank.tank.getCapacity() + "mB" }, mouseX, mouseY);
			} else{
				this.drawFluidInfo(new String[] { I18n.format("None"),"0/" + tank.tank.getCapacity() + "mB" }, mouseX, mouseY);
			}
			
			
		}
		}
}
