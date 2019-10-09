package com.hbm.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.hbm.lib.RefStrings;
import com.hbm.tileentity.conductor.TileEntityFFFluidDuct;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

public class RenderFluidDuct extends TileEntitySpecialRenderer {

	public ResourceLocation texture = new ResourceLocation(RefStrings.MODID, "textures/blocks/fluid_duct.png");
	float pixel = 1F / 16F;
	float textureP = 1F / 32F;

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double offsetX, double offsetY, double offsetZ, float f) {
		GL11.glTranslated(offsetX, offsetY, offsetZ);
		GL11.glDisable(GL11.GL_LIGHTING);
		this.bindTexture(texture);
		drawCore(tileentity);
		TileEntityFFFluidDuct cable = (TileEntityFFFluidDuct) tileentity;
		for (int i = 0; i < cable.connections.length; i++) {
			if (cable.connections[i] != null) {
				drawConnection(cable.connections[i], cable.getType());
			}
		}
		GL11.glTranslated(-offsetX, -offsetY, -offsetZ);
		GL11.glEnable(GL11.GL_LIGHTING);

	}

	public void drawCore(TileEntity tileentity) {
		Tessellator tesseract = Tessellator.instance;
		tesseract.startDrawingQuads();
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP,
				0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 0 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 11 * pixel / 2, 1 - 11 * pixel / 2, 0 * textureP, 5 * textureP);

		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 0 * textureP,
				0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 11 * pixel / 2, 1 - 11 * pixel / 2, 0 * textureP, 5 * textureP);

		tesseract.addVertexWithUV(11 * pixel / 2, 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 0 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 11 * pixel / 2, 11 * pixel / 2, 0 * textureP, 5 * textureP);

		tesseract.addVertexWithUV(11 * pixel / 2, 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 0 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 11 * pixel / 2, 11 * pixel / 2, 0 * textureP, 5 * textureP);

		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP,
				5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 0 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 0 * textureP, 5 * textureP);

		tesseract.addVertexWithUV(11 * pixel / 2, 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 11 * pixel / 2, 11 * pixel / 2, 0 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 11 * pixel / 2, 1 - 11 * pixel / 2, 0 * textureP, 5 * textureP);
		tesseract.draw();
	}

	public void drawConnection(ForgeDirection direction, Fluid type) {
		Tessellator tesseract = Tessellator.instance;
		tesseract.startDrawingQuads();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		if (direction.equals(ForgeDirection.UP)) {

		}
		if (direction.equals(ForgeDirection.DOWN)) {
			GL11.glRotatef(180, 1, 0, 0);
		}
		if (direction.equals(ForgeDirection.NORTH)) {
			GL11.glRotatef(270, 1, 0, 0);
		}
		if (direction.equals(ForgeDirection.SOUTH)) {
			GL11.glRotatef(90, 1, 0, 0);
		}
		if (direction.equals(ForgeDirection.EAST)) {
			GL11.glRotatef(270, 0, 0, 1);
		}
		if (direction.equals(ForgeDirection.WEST)) {
			GL11.glRotatef(90, 0, 0, 1);
		}
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP,
				5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1, 1 - 11 * pixel / 2, 10 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1, 1 - 11 * pixel / 2, 10 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP, 0 * textureP);

		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1, 11 * pixel / 2, 10 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1, 11 * pixel / 2, 10 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 0 * textureP);

		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1, 11 * pixel / 2, 10 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1, 1 - 11 * pixel / 2, 10 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP,
				0 * textureP);

		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2, 5 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1, 1 - 11 * pixel / 2, 10 * textureP, 5 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1, 11 * pixel / 2, 10 * textureP, 0 * textureP);
		tesseract.addVertexWithUV(11 * pixel / 2, 1 - 11 * pixel / 2, 11 * pixel / 2, 5 * textureP, 0 * textureP);
		tesseract.draw();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		float p = 0.01F;
		float n = -0.01F;
		int r = 169;
		int g = 169;
		int b = 169;
		if (type == null || type.getStillIcon() == null) {
			tesseract.startDrawing(5);
				tesseract.setColorRGBA(r, g, b, 255);
				tesseract.addVertex(11 * pixel / 2 + p, 1, 1 - 11 * pixel / 2 + n);
				tesseract.addVertex(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2 + n);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1, 1 - 11 * pixel / 2 + n);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2 + n);
			tesseract.draw();

			tesseract.startDrawing(5);
				tesseract.setColorRGBA(r, g, b, 255);
				tesseract.addVertex(11 * pixel / 2 + p, 1, 11 * pixel / 2 + p);
				tesseract.addVertex(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 11 * pixel / 2 + p);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1, 11 * pixel / 2 + p);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 11 * pixel / 2 + p);
			tesseract.draw();

			tesseract.startDrawing(5);
				tesseract.setColorRGBA(r, g, b, 255);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1, 11 * pixel / 2 + p);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 11 * pixel / 2 + p);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1, 1 - 11 * pixel / 2 + n);
				tesseract.addVertex(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2 + n);
			tesseract.draw();

			tesseract.startDrawing(5);
				tesseract.setColorRGBA(r, g, b, 255);
				tesseract.addVertex(11 * pixel / 2 + p, 1, 11 * pixel / 2 + p);
				tesseract.addVertex(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 11 * pixel / 2 + p);
				tesseract.addVertex(11 * pixel / 2 + p, 1, 1 - 11 * pixel / 2 + n);
				tesseract.addVertex(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2 + n);
			tesseract.draw();
		} else {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			float iconMaxU = type.getStillIcon().getInterpolatedU(10);
			float iconMinU = type.getStillIcon().getInterpolatedU(7);
			float iconMaxV = type.getStillIcon().getInterpolatedV(13);
			float iconMinV = type.getStillIcon().getInterpolatedV(12);
		//	bindTexture(Minecraft.getMinecraft().getTextureManager(), type.getSpriteNumber());
			this.bindTexture(TextureMap.locationBlocksTexture);

			tesseract.startDrawingQuads();
			
				
				tesseract.addVertexWithUV(7*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+5*pixel+1*pixel/2, iconMaxU, iconMinV);
				tesseract.addVertexWithUV(10*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+5*pixel+1*pixel/2, iconMinU, iconMinV);
				tesseract.addVertexWithUV(10*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+5*pixel+1*pixel/2, iconMinU, iconMaxV);
				tesseract.addVertexWithUV(7*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+5*pixel+1*pixel/2, iconMaxU, iconMaxV);
				
				tesseract.addVertexWithUV(7*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+5*pixel+1*pixel/2 + 5*pixel, iconMaxU, iconMinV);
				tesseract.addVertexWithUV(10*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+5*pixel+1*pixel/2 + 5*pixel, iconMinU, iconMinV);
				tesseract.addVertexWithUV(10*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+5*pixel+1*pixel/2 + 5*pixel, iconMinU, iconMaxV);
				tesseract.addVertexWithUV(7*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+5*pixel+1*pixel/2 + 5*pixel, iconMaxU, iconMaxV);
				
				tesseract.addVertexWithUV(11*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+1*pixel+1*pixel/2 + 5*pixel, iconMaxU, iconMinV);
				tesseract.addVertexWithUV(11*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+4*pixel+1*pixel/2 + 5*pixel, iconMinU, iconMinV);
				tesseract.addVertexWithUV(11*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+4*pixel+1*pixel/2 + 5*pixel, iconMinU, iconMaxV);
				tesseract.addVertexWithUV(11*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+1*pixel+1*pixel/2 + 5*pixel, iconMaxU, iconMaxV);
				
				tesseract.addVertexWithUV(6*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+1*pixel+1*pixel/2 + 5*pixel, iconMaxU, iconMinV);
				tesseract.addVertexWithUV(6*pixel-1*pixel/2,13*pixel-1*pixel/2+0.013, 0+4*pixel+1*pixel/2 + 5*pixel, iconMinU, iconMinV);
				tesseract.addVertexWithUV(6*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+4*pixel+1*pixel/2 + 5*pixel, iconMinU, iconMaxV);
				tesseract.addVertexWithUV(6*pixel-1*pixel/2,12*pixel-1*pixel/2+0.006, 0+1*pixel+1*pixel/2 + 5*pixel, iconMaxU, iconMaxV);

			tesseract.draw();
			
	/*	tesseract.startDrawingQuads();
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1 + 1, 1 - 11 * pixel / 2 + n, iconMaxU, iconMinV);
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2 + 1, 1 - 11 * pixel / 2 + n, iconMinU, iconMinV);
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1 + 1, 1 - 11 * pixel / 2 + n, iconMinU, iconMaxV);
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1 - 11 * pixel / 2 + 1, 1 - 11 * pixel / 2 + n, iconMaxU, iconMaxV);
			
		tesseract.draw();

		tesseract.startDrawingQuads();
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1, 11 * pixel / 2 + p, iconMinU, iconMaxV);
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 11 * pixel / 2 + p, iconMaxU, iconMaxV);
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1, 11 * pixel / 2 + p, iconMaxU, iconMinV);
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 11 * pixel / 2 + p, iconMinU, iconMinV);
		tesseract.draw();

		tesseract.startDrawingQuads();
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1, 11 * pixel / 2 + p, iconMinU, iconMaxV);
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 11 * pixel / 2 + p, iconMaxU, iconMaxV);
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1, 1 - 11 * pixel / 2 + n, iconMaxU, iconMinV);
			tesseract.addVertexWithUV(1 - 11 * pixel / 2 + n, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2 + n, iconMinU, iconMinV);
		tesseract.draw();

		tesseract.startDrawingQuads();
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1, 11 * pixel / 2 + p, iconMinU, iconMaxV);
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 11 * pixel / 2 + p, iconMaxU, iconMaxV);
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1, 1 - 11 * pixel / 2 + n, iconMaxU, iconMinV);
			tesseract.addVertexWithUV(11 * pixel / 2 + p, 1 - 11 * pixel / 2, 1 - 11 * pixel / 2 + n, iconMinU, iconMinV);
		tesseract.draw(); */
			
			this.bindTexture(texture);

		}
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		if (direction.equals(ForgeDirection.UP)) {

		}
		if (direction.equals(ForgeDirection.DOWN)) {
			GL11.glRotatef(-180, 1, 0, 0);
		}
		if (direction.equals(ForgeDirection.NORTH)) {
			GL11.glRotatef(-270, 1, 0, 0);
		}
		if (direction.equals(ForgeDirection.SOUTH)) {
			GL11.glRotatef(-90, 1, 0, 0);
		}
		if (direction.equals(ForgeDirection.EAST)) {
			GL11.glRotatef(-270, 0, 0, 1);
		}
		if (direction.equals(ForgeDirection.WEST)) {
			GL11.glRotatef(-90, 0, 0, 1);
		}
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
	}
	
	protected void bindTexture(TextureManager renderEngine, int spriteNumber) {

		if (spriteNumber == 0) {
			renderEngine.bindTexture(TextureMap.locationBlocksTexture);
		} else if (spriteNumber == 1) {
			renderEngine.bindTexture(TextureMap.locationItemsTexture);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, spriteNumber);
		}
	}
}
