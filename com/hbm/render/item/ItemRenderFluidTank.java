package com.hbm.render.item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.hbm.render.misc.RenderItemStack;

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
import net.minecraftforge.fluids.FluidContainerRegistry;

@SideOnly(Side.CLIENT)
public class ItemRenderFluidTank implements IItemRenderer {

	RenderItem renderItem = new RenderItem();

	public ItemRenderFluidTank() {

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
		if(helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal()){
			
		return true;
		}
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
					GL11.glDepthMask(false);
					float iconMaxU = fluidIcon.getInterpolatedU(9);
					float iconMinU = fluidIcon.getInterpolatedU(7);
					float iconMaxV = fluidIcon.getInterpolatedV(11);
					float iconMinV = fluidIcon.getInterpolatedV(5);

					// Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
					bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
					tes.startDrawingQuads();

					tes.addVertexWithUV(7, 11, 0, iconMinU, iconMaxV);
					tes.addVertexWithUV(9, 11, 0, iconMaxU, iconMaxV);
					tes.addVertexWithUV(9, 5, 0, iconMaxU, iconMinV);
					tes.addVertexWithUV(7, 5, 0, iconMinU, iconMinV);

					tes.draw();
					GL11.glDepthMask(true);
				}
			} else {
				if (type == ItemRenderType.ENTITY) {
					tes.addTranslation(-0.5F, -0.25F, 0F);
					//GL11.glTranslatef(0.5f, 4 / -16f, 0);
					//GL11.glRotatef(180, 0, 1, 0);
				}

				ItemRenderer.renderItemIn2D(tes, iconMaxU2, iconMinV2, iconMinU2, iconMaxV2, itemIcon.getIconWidth(),
						itemIcon.getIconHeight(), 0.0625F);
				if (fluidIcon != null) {
					float iconMaxU = fluidIcon.getInterpolatedU(9);
					float iconMinU = fluidIcon.getInterpolatedU(7);
					float iconMaxV = fluidIcon.getInterpolatedV(11);
					float iconMinV = fluidIcon.getInterpolatedV(5);

					bindTexture(Minecraft.getMinecraft().getTextureManager(), fluid.getSpriteNumber());
					 tes.startDrawingQuads();
				        tes.setNormal(0.0F, 0.0F, 0.5F);
				        tes.addVertexWithUV((7.0*0.0625), (5.0*0.0625), 0.0D, (double)iconMaxU, (double)iconMaxV);
				        tes.addVertexWithUV((9.0*0.0625), (5.0*0.0625), 0.0D, (double)iconMinU, (double)iconMaxV);
				        tes.addVertexWithUV((9.0*0.0625), (11.0*0.0625), 0.0D, (double)iconMinU, (double)iconMinV);
				        tes.addVertexWithUV((7.0*0.0625), (11.0*0.0625), 0.0D, (double)iconMaxU, (double)iconMinV);
				        tes.draw();
				        tes.startDrawingQuads();
				        tes.setNormal(0.0F, 0.0F, -.05F);
				        tes.addVertexWithUV((7.0*0.0625), (11.0*0.0625), (double)(0.0F - 0.0625), (double)iconMaxU, (double)iconMinV);
				        tes.addVertexWithUV((9.0*0.0625), (11.0*0.0625), (double)(0.0F - 0.0625), (double)iconMinU, (double)iconMinV);
				        tes.addVertexWithUV((9.0*0.0625), (5.0*0.0625), (double)(0.0F - 0.0625), (double)iconMinU, (double)iconMaxV);
				        tes.addVertexWithUV((7.0*0.0625), (5.0*0.0625), (double)(0.0F - 0.0625), (double)iconMaxU, (double)iconMaxV);
				        tes.draw();
					
				}
				if (type == ItemRenderType.ENTITY) {
					tes.addTranslation(0.5F, 0.25F, 0F);
					//GL11.glTranslatef(0.5f, 4 / -16f, 0);
					//GL11.glRotatef(180, 0, 1, 0);
					
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
