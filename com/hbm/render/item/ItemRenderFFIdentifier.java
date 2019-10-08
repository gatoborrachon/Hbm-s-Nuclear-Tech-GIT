package com.hbm.render.item;

import org.lwjgl.opengl.GL11;

import com.hbm.items.tool.ItemForgeFluidIdentifier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fluids.Fluid;

@SideOnly(Side.CLIENT)
public class ItemRenderFFIdentifier implements IItemRenderer {

	RenderItem renderItem = new RenderItem();

	public ItemRenderFFIdentifier() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (item.getItem() instanceof ItemForgeFluidIdentifier) {
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		if (helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal()) {

			return true;
		}
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

		Tessellator tes = Tessellator.instance;
		if (item.getItem() instanceof ItemForgeFluidIdentifier) {
			Fluid fluid = ItemForgeFluidIdentifier.getType(item);
			IIcon itemIcon = item.getItem().getIcon(item, 0);
			IIcon fluidIcon = fluid.getStillIcon();

			float iconMaxU2 = itemIcon.getMaxU();
			float iconMinU2 = itemIcon.getMinU();
			float iconMaxV2 = itemIcon.getMaxV();
			float iconMinV2 = itemIcon.getMinV();

			if (type == ItemRenderType.INVENTORY) {
				RenderHelper.enableGUIStandardItemLighting();
				GL11.glDisable(GL11.GL_LIGHTING);

				bindTexture(Minecraft.getMinecraft().getTextureManager(), item.getItem().getSpriteNumber());

				tes.startDrawingQuads();

				tes.addVertexWithUV(0, 16, 0, iconMinU2, iconMaxV2);
				tes.addVertexWithUV(16, 16, 0, iconMaxU2, iconMaxV2);
				tes.addVertexWithUV(16, 0, 0, iconMaxU2, iconMinV2);
				tes.addVertexWithUV(0, 0, 0, iconMinU2, iconMinV2);

				tes.draw();
				if (fluid.getStillIcon() != null) {
					GL11.glDepthMask(false);
					this.drawLiquid(fluidIcon, fluid, tes);

					GL11.glDepthMask(true);
				}
			} else {
				if (type == ItemRenderType.ENTITY) {
					tes.addTranslation(-0.5F, -0.25F, 0F);
					// GL11.glTranslatef(0.5f, 4 / -16f, 0);
					// GL11.glRotatef(180, 0, 1, 0);
				}

				ItemRenderer.renderItemIn2D(tes, iconMaxU2, iconMinV2, iconMinU2, iconMaxV2, itemIcon.getIconWidth(),
						itemIcon.getIconHeight(), 0.0625F);
				if (fluidIcon != null) {
					float iconMaxU = fluidIcon.getInterpolatedU(9);
					float iconMinU = fluidIcon.getInterpolatedU(7);
					float iconMaxV = fluidIcon.getInterpolatedV(11);
					float iconMinV = fluidIcon.getInterpolatedV(5);

					bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
					/*
					 * tes.startDrawingQuads(); tes.setNormal(0.0F, 0.0F, 0.5F);
					 * tes.addVertexWithUV((7.0*0.0625), (5.0*0.0625), 0.0D,
					 * (double)iconMaxU, (double)iconMaxV);
					 * tes.addVertexWithUV((9.0*0.0625), (5.0*0.0625), 0.0D,
					 * (double)iconMinU, (double)iconMaxV);
					 * tes.addVertexWithUV((9.0*0.0625), (11.0*0.0625), 0.0D,
					 * (double)iconMinU, (double)iconMinV);
					 * tes.addVertexWithUV((7.0*0.0625), (11.0*0.0625), 0.0D,
					 * (double)iconMaxU, (double)iconMinV); tes.draw();
					 * tes.startDrawingQuads(); tes.setNormal(0.0F, 0.0F,
					 * -.05F); tes.addVertexWithUV((7.0*0.0625), (11.0*0.0625),
					 * (double)(0.0F - 0.0625), (double)iconMaxU,
					 * (double)iconMinV); tes.addVertexWithUV((9.0*0.0625),
					 * (11.0*0.0625), (double)(0.0F - 0.0625), (double)iconMinU,
					 * (double)iconMinV); tes.addVertexWithUV((9.0*0.0625),
					 * (5.0*0.0625), (double)(0.0F - 0.0625), (double)iconMinU,
					 * (double)iconMaxV); tes.addVertexWithUV((7.0*0.0625),
					 * (5.0*0.0625), (double)(0.0F - 0.0625), (double)iconMaxU,
					 * (double)iconMaxV); tes.draw();
					 */

					this.drawLiquidIn3D(fluidIcon, fluid, tes, 0.5F, 0.0D, false);
					this.drawLiquidIn3D(fluidIcon, fluid, tes, -0.5F, -0.0625D, true);

				}
				if (type == ItemRenderType.ENTITY) {
					tes.addTranslation(0.5F, 0.25F, 0F);
					// GL11.glTranslatef(0.5f, 4 / -16f, 0);
					// GL11.glRotatef(180, 0, 1, 0);

				}
			}
		}

		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glPopMatrix();
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

	protected void drawLiquid(IIcon fluidIcon, Fluid fluid, Tessellator tes) {
		float iconMaxU = fluidIcon.getInterpolatedU(9);
		float iconMinU = fluidIcon.getInterpolatedU(7);
		float iconMaxV = fluidIcon.getInterpolatedV(12);
		float iconMinV = fluidIcon.getInterpolatedV(4);

		// Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
		tes.startDrawingQuads();

		tes.addVertexWithUV(7, 12, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(9, 12, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(9, 4, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(7, 4, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(10);
		iconMinU = fluidIcon.getInterpolatedU(9);
		iconMaxV = fluidIcon.getInterpolatedV(11);
		iconMinV = fluidIcon.getInterpolatedV(6);

		tes.addVertexWithUV(9, 11, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(10, 11, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(10, 6, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(9, 6, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(7);
		iconMinU = fluidIcon.getInterpolatedU(6);
		iconMaxV = fluidIcon.getInterpolatedV(11);
		iconMinV = fluidIcon.getInterpolatedV(6);

		tes.addVertexWithUV(6, 11, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(7, 11, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(7, 6, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(6, 6, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(6);
		iconMinU = fluidIcon.getInterpolatedU(5);
		iconMaxV = fluidIcon.getInterpolatedV(10);
		iconMinV = fluidIcon.getInterpolatedV(8);

		tes.addVertexWithUV(5, 10, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(6, 10, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(6, 8, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(5, 8, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(11);
		iconMinU = fluidIcon.getInterpolatedU(10);
		iconMaxV = fluidIcon.getInterpolatedV(10);
		iconMinV = fluidIcon.getInterpolatedV(8);

		tes.addVertexWithUV(10, 10, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(11, 10, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(11, 8, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(10, 8, 0, iconMinU, iconMinV);

		tes.draw();
	}

	protected void drawLiquidIn3D(IIcon fluidIcon, Fluid fluid, Tessellator tes, float normal, double offset,
			boolean reverse) {
		float iconMaxU = fluidIcon.getInterpolatedU(9);
		float iconMinU = fluidIcon.getInterpolatedU(7);
		float iconMaxV = fluidIcon.getInterpolatedV(12);
		float iconMinV = fluidIcon.getInterpolatedV(4);

		double pixel = 0.0625D;
		// Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
		tes.startDrawingQuads();

		tes.setNormal(0.0F, 0.0F, normal);
		if (reverse) {
			tes.addVertexWithUV(7 * pixel, 12 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(9 * pixel, 12 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(9 * pixel, 4 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(7 * pixel, 4 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(10);
			iconMinU = fluidIcon.getInterpolatedU(9);
			iconMaxV = fluidIcon.getInterpolatedV(10);
			iconMinV = fluidIcon.getInterpolatedV(5);

			tes.addVertexWithUV(9 * pixel, 10 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(10 * pixel, 10 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(10 * pixel, 5 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(9 * pixel, 5 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(7);
			iconMinU = fluidIcon.getInterpolatedU(6);
			iconMaxV = fluidIcon.getInterpolatedV(10);
			iconMinV = fluidIcon.getInterpolatedV(5);

			tes.addVertexWithUV(6 * pixel, 10 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(7 * pixel, 10 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(7 * pixel, 5 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(6 * pixel, 5 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(6);
			iconMinU = fluidIcon.getInterpolatedU(5);
			iconMaxV = fluidIcon.getInterpolatedV(8);
			iconMinV = fluidIcon.getInterpolatedV(6);

			tes.addVertexWithUV(5 * pixel, 8 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(6 * pixel, 8 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(6 * pixel, 6 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(5 * pixel, 6 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(11);
			iconMinU = fluidIcon.getInterpolatedU(10);
			iconMaxV = fluidIcon.getInterpolatedV(8);
			iconMinV = fluidIcon.getInterpolatedV(6);

			tes.addVertexWithUV(10 * pixel, 8 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(11 * pixel, 8 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(11 * pixel, 6 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(10 * pixel, 6 * pixel, offset, iconMinU, iconMinV);
		} else {
			tes.addVertexWithUV(7 * pixel, 4 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(9 * pixel, 4 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(9 * pixel, 12 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(7 * pixel, 12 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(10);
			iconMinU = fluidIcon.getInterpolatedU(9);
			iconMaxV = fluidIcon.getInterpolatedV(10);
			iconMinV = fluidIcon.getInterpolatedV(5);

			tes.addVertexWithUV(9 * pixel, 5 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(10 * pixel, 5 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(10 * pixel, 10 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(9 * pixel, 10 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(7);
			iconMinU = fluidIcon.getInterpolatedU(6);
			iconMaxV = fluidIcon.getInterpolatedV(10);
			iconMinV = fluidIcon.getInterpolatedV(5);

			tes.addVertexWithUV(6 * pixel, 5 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(7 * pixel, 5 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(7 * pixel, 10 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(6 * pixel, 10 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(6);
			iconMinU = fluidIcon.getInterpolatedU(5);
			iconMaxV = fluidIcon.getInterpolatedV(8);
			iconMinV = fluidIcon.getInterpolatedV(6);

			tes.addVertexWithUV(5 * pixel, 6 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(6 * pixel, 6 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(6 * pixel, 8 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(5 * pixel, 8 * pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(11);
			iconMinU = fluidIcon.getInterpolatedU(10);
			iconMaxV = fluidIcon.getInterpolatedV(8);
			iconMinV = fluidIcon.getInterpolatedV(6);

			tes.addVertexWithUV(10 * pixel, 6 * pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(11 * pixel, 6 * pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(11 * pixel, 8 * pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(10 * pixel, 8 * pixel, offset, iconMinU, iconMinV);
		}
		tes.draw();
	}
}
