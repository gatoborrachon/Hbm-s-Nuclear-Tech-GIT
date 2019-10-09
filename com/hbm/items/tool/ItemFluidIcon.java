package com.hbm.items.tool;

import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemFluidIcon extends Item {
	
	IIcon overlayIcon;

    public ItemFluidIcon()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list)
    {
        for(Entry<Fluid, Integer> entry : FluidRegistry.getRegisteredFluidIDsByFluid().entrySet()){
        	list.add(new ItemStack(item, 1, entry.getValue()));
        }
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool)
	{
		if(stack.hasTagCompound())
			if(stack.getTagCompound().getInteger("fill") > 0)
				list.add(stack.getTagCompound().getInteger("fill") + "mB");
	}
	
	public static ItemStack addQuantity(ItemStack stack, int i) {
		
		if(!stack.hasTagCompound())
			stack.stackTagCompound = new NBTTagCompound();
		
		stack.getTagCompound().setInteger("fill", i);
		
		return stack.copy();
	}

    public String getItemStackDisplayName(ItemStack stack)
    {
        String s;
        if(FluidRegistry.getFluid(stack.getItemDamage()) != null)
        	s = (I18n.format(FluidRegistry.getFluid(stack.getItemDamage()).getUnlocalizedName())).trim();
        else
        	s = null;

        if (s != null)
        {
            return s;
        }

        return "Unknown";
    }

    /*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public boolean requiresMultipleRenderPasses() {
	 * return true; }
	 * 
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public void registerIcons(IIconRegister
	 * p_94581_1_) { super.registerIcons(p_94581_1_);
	 * 
	 * this.overlayIcon =
	 * p_94581_1_.registerIcon("hbm:fluid_identifier_overlay"); }
	 * 
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT) public IIcon getIconFromDamageForRenderPass(int
	 * p_77618_1_, int p_77618_2_) { return p_77618_2_ == 1 ? this.overlayIcon :
	 * super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_); }
	 */

    public static Fluid getFluid(ItemStack stack){
    	if(stack != null && stack.getItem() instanceof ItemFluidIcon){
    		return FluidRegistry.getFluid(stack.getItemDamage());
    	} else {
    		return null;
    	}
    }
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int p_82790_2_) {
		int j = 16777215;
		return j;
	}

}
