package com.hbm.packet;

import com.hbm.tileentity.machine.TileEntityMachineTurbine;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidTypePacketTest implements IMessage {

	int x;
	int y;
	int z;
	Fluid[] fluids;
	int length;
	
	public FluidTypePacketTest() {

	}

	public FluidTypePacketTest(int x, int y, int z, Fluid[] fluids) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.fluids = fluids;
		this.length = fluids.length;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		length = buf.readInt();
		fluids = new Fluid[length];
		for(int i = 0; i < length; i++){
			int test = buf.readInt();
			fluids[i] = FluidRegistry.getFluid(test);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(length);
		
		for(int i = 0; i < length ; i++){
			buf.writeInt(FluidRegistry.getFluidID(fluids[i]));
		}
	}

	public static class Handler implements IMessageHandler<FluidTypePacketTest, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(FluidTypePacketTest m, MessageContext ctx) {
			TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(m.x, m.y, m.z);
				if (te != null && te instanceof TileEntityMachineTurbine) {
					((TileEntityMachineTurbine)te).tankTypes[0] = m.fluids[0];
					((TileEntityMachineTurbine)te).tankTypes[1] = m.fluids[1];
				}

			return null;
		}
	}
}
