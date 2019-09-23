package com.hbm.render.item;

import org.lwjgl.opengl.GL11;

import com.hbm.items.tool.ItemFFFluidDuct;
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
public class ItemRenderFFFluidDuct implements IItemRenderer {

	RenderItem renderItem = new RenderItem();

	public ItemRenderFFFluidDuct() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (item.getItem() instanceof ItemFFFluidDuct) {
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
		if (item.getItem() instanceof ItemFFFluidDuct) {
			Fluid fluid = ItemFFFluidDuct.getType(item);
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

					bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());

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
		float iconMaxU;
		float iconMinU;
		float iconMaxV;
		float iconMinV;

		// Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
		tes.startDrawingQuads();

		iconMaxU = fluidIcon.getInterpolatedU(5);
		iconMinU = fluidIcon.getInterpolatedU(3);
		iconMaxV = fluidIcon.getInterpolatedV(5);
		iconMinV = fluidIcon.getInterpolatedV(3);
		
		tes.addVertexWithUV(3, 5, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(5, 5, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(5, 3, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(3, 3, 0, iconMinU, iconMinV);
		
		iconMaxU = fluidIcon.getInterpolatedU(7);
		iconMinU = fluidIcon.getInterpolatedU(5);
		iconMaxV = fluidIcon.getInterpolatedV(8);
		iconMinV = fluidIcon.getInterpolatedV(7);
		
		tes.addVertexWithUV(5, 8, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(7, 8, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(7, 7, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(5, 7, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(8);
		iconMinU = fluidIcon.getInterpolatedU(7);
		iconMaxV = fluidIcon.getInterpolatedV(7);
		iconMinV = fluidIcon.getInterpolatedV(5);

		tes.addVertexWithUV(7, 7, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(8, 7, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(8, 5, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(7, 5, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(13);
		iconMinU = fluidIcon.getInterpolatedU(11);
		iconMaxV = fluidIcon.getInterpolatedV(13);
		iconMinV = fluidIcon.getInterpolatedV(14);

		tes.addVertexWithUV(11, 14, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(13, 14, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(13, 13, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(11, 13, 0, iconMinU, iconMinV);

		iconMaxU = fluidIcon.getInterpolatedU(14);
		iconMinU = fluidIcon.getInterpolatedU(13);
		iconMaxV = fluidIcon.getInterpolatedV(11);
		iconMinV = fluidIcon.getInterpolatedV(13);

		tes.addVertexWithUV(13, 13, 0, iconMinU, iconMaxV);
		tes.addVertexWithUV(14, 13, 0, iconMaxU, iconMaxV);
		tes.addVertexWithUV(14, 11, 0, iconMaxU, iconMinV);
		tes.addVertexWithUV(13, 11, 0, iconMinU, iconMinV);

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
			iconMaxU = fluidIcon.getInterpolatedU(5);
			iconMinU = fluidIcon.getInterpolatedU(3);
			iconMaxV = fluidIcon.getInterpolatedV(5);
			iconMinV = fluidIcon.getInterpolatedV(3);
			
			tes.addVertexWithUV(13*pixel, 11*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(11*pixel, 11*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(11*pixel, 13*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(13*pixel, 13*pixel, offset, iconMinU, iconMinV);
			
			iconMaxU = fluidIcon.getInterpolatedU(7);
			iconMinU = fluidIcon.getInterpolatedU(5);
			iconMaxV = fluidIcon.getInterpolatedV(8);
			iconMinV = fluidIcon.getInterpolatedV(7);
			
			tes.addVertexWithUV(11*pixel, 8*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(9*pixel, 8*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(9*pixel, 9*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(11*pixel, 9*pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(8);
			iconMinU = fluidIcon.getInterpolatedU(7);
			iconMaxV = fluidIcon.getInterpolatedV(7);
			iconMinV = fluidIcon.getInterpolatedV(5);

			tes.addVertexWithUV(9*pixel, 9*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(8*pixel, 9*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(8*pixel, 11*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(9*pixel, 11*pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(13);
			iconMinU = fluidIcon.getInterpolatedU(11);
			iconMaxV = fluidIcon.getInterpolatedV(13);
			iconMinV = fluidIcon.getInterpolatedV(14);

			tes.addVertexWithUV(5*pixel, 2*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(3*pixel, 2*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(3*pixel, 3*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(5*pixel, 3*pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(14);
			iconMinU = fluidIcon.getInterpolatedU(13);
			iconMaxV = fluidIcon.getInterpolatedV(11);
			iconMinV = fluidIcon.getInterpolatedV(13);

			tes.addVertexWithUV(3*pixel, 3*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(2*pixel, 3*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(2*pixel, 5*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(3*pixel, 5*pixel, offset, iconMinU, iconMinV);
		} else {
			iconMaxU = fluidIcon.getInterpolatedU(5);
			iconMinU = fluidIcon.getInterpolatedU(3);
			iconMaxV = fluidIcon.getInterpolatedV(5);
			iconMinV = fluidIcon.getInterpolatedV(3);
			
			tes.addVertexWithUV(13*pixel, 13*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(11*pixel, 13*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(11*pixel, 11*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(13*pixel, 11*pixel, offset, iconMinU, iconMinV);
			
			iconMaxU = fluidIcon.getInterpolatedU(7);
			iconMinU = fluidIcon.getInterpolatedU(5);
			iconMaxV = fluidIcon.getInterpolatedV(8);
			iconMinV = fluidIcon.getInterpolatedV(7);
			
			tes.addVertexWithUV(11*pixel, 9*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(9*pixel, 9*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(9*pixel, 8*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(11*pixel, 8*pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(8);
			iconMinU = fluidIcon.getInterpolatedU(7);
			iconMaxV = fluidIcon.getInterpolatedV(7);
			iconMinV = fluidIcon.getInterpolatedV(5);

			tes.addVertexWithUV(9*pixel, 11*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(8*pixel, 11*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(8*pixel, 9*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(9*pixel, 9*pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(13);
			iconMinU = fluidIcon.getInterpolatedU(11);
			iconMaxV = fluidIcon.getInterpolatedV(13);
			iconMinV = fluidIcon.getInterpolatedV(14);

			tes.addVertexWithUV(5*pixel, 3*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(3*pixel, 3*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(3*pixel, 2*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(5*pixel, 2*pixel, offset, iconMinU, iconMinV);

			iconMaxU = fluidIcon.getInterpolatedU(14);
			iconMinU = fluidIcon.getInterpolatedU(13);
			iconMaxV = fluidIcon.getInterpolatedV(11);
			iconMinV = fluidIcon.getInterpolatedV(13);

			tes.addVertexWithUV(3*pixel, 5*pixel, offset, iconMinU, iconMaxV);
			tes.addVertexWithUV(2*pixel, 5*pixel, offset, iconMaxU, iconMaxV);
			tes.addVertexWithUV(2*pixel, 3*pixel, offset, iconMaxU, iconMinV);
			tes.addVertexWithUV(3*pixel, 3*pixel, offset, iconMinU, iconMinV);
		}
		tes.draw();
	}
}
