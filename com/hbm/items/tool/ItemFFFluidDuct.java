package com.hbm.items.tool;

import java.util.List;
import java.util.Map.Entry;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.conductor.TileEntityFFFluidDuct;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemFFFluidDuct extends Item {
	
	IIcon overlayIcon;

    public ItemFFFluidDuct()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list)
    {
        for(Entry<Fluid, Integer> set : FluidRegistry.getRegisteredFluidIDsByFluid().entrySet()){
        	list.add(new ItemStack(item, 1, set.getValue()));
        }
    }

    /*public void onCreated(ItemStack stack, World world, EntityPlayer player) {
    	
    	if(stack != null)
    		player.inventory.addItemStackToInventory(new ItemStack(ModItems.fluid_identifier, 1, stack.getItemDamage()));
    }*/

    public String getItemStackDisplayName(ItemStack stack)
    {
        String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String s1 = ("" + StatCollector.translateToLocal(FluidRegistry.getFluid(stack.getItemDamage()).getUnlocalizedName())).trim();

        if (s1 != null)
        {
            s = s + " " + s1;
        }

        return s;
    }

    @Override
	@SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
        super.registerIcons(p_94581_1_);
        this.overlayIcon = p_94581_1_.registerIcon("hbm:duct_overlay");
    }
    
    @Override
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_)
    {
        return p_77618_2_ == 1 ? this.overlayIcon : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int p_82790_2_)
    {
        if (p_82790_2_ == 0)
        {
            return 16777215;
        }
        else
        {
        	int j = -1; // We really don't care

            if (j < 0)
            {
                j = 16777215;
            }

            return j;
        }
    }
    
	public static Fluid getType(ItemStack stack) {
		if(stack != null && stack.getItem() instanceof ItemFFFluidDuct)
			return FluidRegistry.getFluid(stack.getItemDamage());
		else
			return null;
	}
    
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int i, float f0, float f1, float f2)
    {
        if (world.getBlock(x, y, z) != Blocks.snow_layer)
        {
            if (i == 0)
            {
                --y;
            }

            if (i == 1)
            {
                ++y;
            }

            if (i == 2)
            {
                --z;
            }

            if (i == 3)
            {
                ++z;
            }

            if (i == 4)
            {
                --x;
            }

            if (i == 5)
            {
                ++x;
            }

            if (!world.isAirBlock(x, y, z))
            {
                return false;
            }
        }

        if (!player.canPlayerEdit(x, y, z, i, stack))
        {
            return false;
        }
        else
        {
            --stack.stackSize;
            world.setBlock(x, y, z, ModBlocks.fluid_duct);
            
            if(world.getTileEntity(x, y, z) instanceof TileEntityFFFluidDuct) {
            	((TileEntityFFFluidDuct)world.getTileEntity(x, y, z)).setType(FluidRegistry.getFluid(stack.getItemDamage()));;
            }
            
            world.playSoundEffect(x, y, z, "hbm:block.pipePlaced", 1.0F, 0.65F + world.rand.nextFloat() * 0.2F);

            return true;
        }
    }

}
