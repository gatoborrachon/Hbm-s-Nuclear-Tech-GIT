package com.hbm.packet;

import java.util.Map.Entry;

import com.hbm.interfaces.IFluidPipe;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class TEFluidTypePacketTest implements IMessage {

	int x;
	int y;
	int z;
	Fluid type;

	public TEFluidTypePacketTest()
	{
		
	}

	public TEFluidTypePacketTest(int x, int y, int z, Fluid type)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		type = FluidRegistry.getFluid(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
        for (Entry<Fluid, Integer> entry : FluidRegistry.getRegisteredFluidIDsByFluid().entrySet()){
        {
            if(FluidRegistry.getFluid(entry.getValue()) == type) {
            	buf.writeInt(entry.getValue());
            	break;
            }
        }
	}

}
	public static class Handler implements IMessageHandler<TEFluidTypePacketTest, IMessage> {
		
		@Override
		public IMessage onMessage(TEFluidTypePacketTest m, MessageContext ctx) {
			try {
				TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(m.x, m.y, m.z);

				if (te != null && te instanceof IFluidPipe) {
					
					IFluidPipe duct = (IFluidPipe) te;
					if(!m.type.equals(duct.getType()))
						duct.setTypeTrue(m.type);
				}
				return null;
			} catch(Exception ex) {
				return null;
			}
		}
	}
}