package com.hbm.items.tool;

import java.util.List;
import java.util.Map.Entry;

import com.hbm.items.ModItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemFluidTank extends Item {

	IIcon overlayIcon;

	public ItemFluidTank() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for(Entry<Fluid, Integer> entry : FluidRegistry.getRegisteredFluidIDsByFluid().entrySet()){
			list.add(new ItemStack(item, 1, entry.getValue()));
		}
	}

	public String getItemStackDisplayName(ItemStack stack) {
		String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
		String s1 = null;// ("" + StatCollector.translateToLocal(FluidType.getEnum(stack.getItemDamage()).getUnlocalizedName()))
				//.trim();
		if (FluidContainerRegistry.isContainer(stack)) {
			s1 = ("" + StatCollector.translateToLocal(
					FluidContainerRegistry.getFluidForFilledItem(stack).getFluid().getUnlocalizedName())).trim();
		}

		if (s1 != null) {
			s = s + " " + s1;
		}

		return s;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);

		if (this == ModItems.fluid_tank_full)
			this.overlayIcon = register.registerIcon("hbm:fluid_tank_overlay");
		if (this == ModItems.fluid_barrel_full)
			this.overlayIcon = register.registerIcon("hbm:fluid_barrel_overlay");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_) {
		return p_77618_2_ == 1 ? this.overlayIcon : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int p_82790_2_) {
		if (p_82790_2_ == 0) {
			return 16777215;
		} else {
			int j;// = FluidType.getEnum(stack.getItemDamage()).getMSAColor();
			j = 16777215;
			if (FluidContainerRegistry.isContainer(stack)) {

				j = FluidContainerRegistry.getFluidForFilledItem(stack).getFluid().getColor();

			}

			if (j < 0) {
				j = 16777215;
			}

			return j;
		}
	}

}
