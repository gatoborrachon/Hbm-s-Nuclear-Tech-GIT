package com.hbm.packet;

import com.hbm.interfaces.IFluidPipe;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.conductor.TileEntityPylonRedWire;
import com.hbm.tileentity.machine.TileEntityMachineRadar;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class TEFFPipeDestructorPacket implements IMessage {

	int x;
	int y;
	int z;
	
	public TEFFPipeDestructorPacket() {

	}

	public TEFFPipeDestructorPacket(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<TEFFPipeDestructorPacket, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(TEFFPipeDestructorPacket m, MessageContext ctx) {
			TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(m.x, m.y, m.z);
			try {
				if (te != null && te instanceof IFluidPipe) {
					IFluidPipe pipe = (IFluidPipe) te;
					pipe.breakBlock();
				}
			} catch (Exception e) {
			}
			return null;
		}
	}
}
