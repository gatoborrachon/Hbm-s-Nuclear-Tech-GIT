package com.hbm.render.item;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
@SideOnly(Side.CLIENT)
public class ItemRenderFluidBarrel implements IItemRenderer {
	RenderItem renderItem = new RenderItem();

	public ItemRenderFluidBarrel() {

	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (FluidContainerRegistry.isFilledContainer(item)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();

		Tessellator tes = Tessellator.instance;
		if (FluidContainerRegistry.isFilledContainer(item)) {
			Fluid fluid = FluidContainerRegistry.getFluidForFilledItem(item).getFluid();
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
					float iconMaxU = fluidIcon.getInterpolatedU(9);
					float iconMinU = fluidIcon.getInterpolatedU(7);
					float iconMaxV = fluidIcon.getInterpolatedV(13);
					float iconMinV = fluidIcon.getInterpolatedV(7);
					
					float pix1MaxU = fluidIcon.getInterpolatedU(6);
					float pix1MinU = fluidIcon.getInterpolatedU(6);
					float pix1MaxV = fluidIcon.getInterpolatedV(4);
					float pix1MinV = fluidIcon.getInterpolatedV(4);
					
					float pix2MaxU = fluidIcon.getInterpolatedU(9);
					float pix2MinU = fluidIcon.getInterpolatedU(7);
					float pix2MaxV = fluidIcon.getInterpolatedV(13);
					float pix2MinV = fluidIcon.getInterpolatedV(7);

					// Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
					bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
					tes.startDrawingQuads();

					tes.addVertexWithUV(7, 13, 0, iconMinU, iconMaxV);
					tes.addVertexWithUV(9, 13, 0, iconMaxU, iconMaxV);
					tes.addVertexWithUV(9, 7, 0, iconMaxU, iconMinV);
					tes.addVertexWithUV(7, 7, 0, iconMinU, iconMinV);
					
					tes.addVertexWithUV(9, 5, 0, pix1MinU, pix1MaxV);
					tes.addVertexWithUV(10, 5, 0, pix1MaxU, pix1MaxV);
					tes.addVertexWithUV(10, 4, 0, pix1MaxU, pix1MinV);
					tes.addVertexWithUV(9, 4, 0, pix1MinU, pix1MinV);
					
					tes.addVertexWithUV(8, 4, 0, pix2MinU, pix2MaxV);
					tes.addVertexWithUV(9, 4, 0, pix2MaxU, pix2MaxV);
					tes.addVertexWithUV(9, 3, 0, pix2MaxU, pix2MinV);
					tes.addVertexWithUV(8, 3, 0, pix2MinU, pix2MinV);

					tes.draw();

				}
			} else {

				ItemRenderer.renderItemIn2D(tes, iconMaxU2, iconMinV2, iconMinU2, iconMaxV2, itemIcon.getIconWidth(),
						itemIcon.getIconHeight(), 0.0625F);
				if (fluidIcon != null) {
					float iconMaxU = fluidIcon.getInterpolatedU(9);
					float iconMinU = fluidIcon.getInterpolatedU(7);
					float iconMaxV = fluidIcon.getInterpolatedV(11);
					float iconMinV = fluidIcon.getInterpolatedV(5);

					bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
				
					
				}
			}
		}
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

}
