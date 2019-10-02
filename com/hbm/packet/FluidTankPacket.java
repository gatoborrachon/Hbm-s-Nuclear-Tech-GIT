package com.hbm.packet;

import com.hbm.interfaces.ITankPacketAcceptor;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidTankPacket implements IMessage {

	int x;
	int y;
	int z;
	FluidTank[] tanks;
	NBTTagCompound[] tags;
	int length;
	
	public FluidTankPacket() {

	}

	public FluidTankPacket(int x, int y, int z, FluidTank[] tanks) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.tanks = tanks;
		this.length = tanks.length;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		length = buf.readInt();
		tags = new NBTTagCompound[length];
		for(int i = 0; i < length; i++){
			int amount = buf.readInt();
			int id = buf.readInt();
			NBTTagCompound tag = new NBTTagCompound();
			if(id == -1 || FluidRegistry.getFluid(id) == null){
				tag.setString("Empty", "");
			} else {
				new FluidStack(FluidRegistry.getFluid(id), amount).writeToNBT(tag);
			}
			tags[i] = tag;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(length);
		for(int i = 0; i < length ; i++){
			buf.writeInt(tanks[i].getFluidAmount());
			buf.writeInt(tanks[i].getFluid() == null ? -1 : FluidRegistry.getFluidID(tanks[i].getFluid().getFluid()));
		}
	}

	public static class Handler implements IMessageHandler<FluidTankPacket, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(FluidTankPacket m, MessageContext ctx) {
			TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(m.x, m.y, m.z);
				if (te != null && te instanceof ITankPacketAcceptor) {
					((ITankPacketAcceptor)te).recievePacket(m.tags);
				}

			return null;
		}
	}
}
