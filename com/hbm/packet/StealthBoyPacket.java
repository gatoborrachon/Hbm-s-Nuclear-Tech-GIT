package com.hbm.packet;

import com.hbm.tileentity.machine.TileEntityBlastDoor;
import com.hbm.tileentity.machine.TileEntityMachineRadar;
import com.hbm.tileentity.machine.TileEntityVaultDoor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class StealthBoyPacket implements IMessage {
	boolean isActive;
	int id;
	
	public StealthBoyPacket()
	{
		
	}
	
	public StealthBoyPacket(boolean active, EntityPlayer player) {
		isActive = active;
		id = player.getEntityId();
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		isActive = buf.readBoolean();
		
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(isActive);
		buf.writeInt(id);
		
	}

	public static class Handler implements IMessageHandler<StealthBoyPacket, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(StealthBoyPacket m, MessageContext ctx) {

			Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(m.id);
			if(entity instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer)entity;
				player.getEntityData().setBoolean("StealthActivated", m.isActive);
				
			}
			return null;
		}
	}
}
