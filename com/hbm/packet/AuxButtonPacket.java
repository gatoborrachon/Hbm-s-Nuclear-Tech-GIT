package com.hbm.packet;

import com.hbm.lib.HBMSoundHandler;
import com.hbm.tileentity.bomb.TileEntityRailgun;
import com.hbm.tileentity.machine.TileEntityMachineReactorLarge;
import com.hbm.tileentity.machine.TileEntityMachineReactorSmall;
import com.hbm.tileentity.machine.TileEntityReactorControl;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AuxButtonPacket implements IMessage {

	int x;
	int y;
	int z;
	int value;
	int id;

	public AuxButtonPacket()
	{
		
	}

	public AuxButtonPacket(int x, int y, int z, int value, int id)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.value = value;
		this.id = id;
	}
	
	public AuxButtonPacket(BlockPos pos, int value, int id){
		this(pos.getX(), pos.getY(), pos.getZ(), value, id);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		value = buf.readInt();
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(value);
		buf.writeInt(id);
	}

	public static class Handler implements IMessageHandler<AuxButtonPacket, IMessage> {

		@Override
		public IMessage onMessage(AuxButtonPacket m, MessageContext ctx) {
			ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
				EntityPlayer p = ctx.getServerHandler().player;
				BlockPos pos = new BlockPos(m.x, m.y, m.z);
				//try {
					TileEntity te = p.world.getTileEntity(pos);
					
					if (te instanceof TileEntityMachineReactorSmall) {
						TileEntityMachineReactorSmall reactor = (TileEntityMachineReactorSmall)te;
						
						if(m.id == 0)
							reactor.retracting = m.value == 1;
						if(m.id == 1) {
							reactor.compress(m.value);
						}
						reactor.markDirty();
					}
					/*if (te instanceof TileEntityRadioRec) {
						TileEntityRadioRec radio = (TileEntityRadioRec)te;
						
						if(m.id == 0) {
							radio.isOn = (m.value == 1);
						}
						
						if(m.id == 1) {
							radio.freq = ((double)m.value) / 10D;
						}
					}
					
					if (te instanceof TileEntityForceField) {
						TileEntityForceField field = (TileEntityForceField)te;
						
						field.isOn = !field.isOn;
					}
					
					*/if (te instanceof TileEntityReactorControl) {
						TileEntityReactorControl control = (TileEntityReactorControl)te;
						
						if(m.id == 1)
							control.auto = m.value == 1;
						
					}
					TileEntity reac = p.world.getTileEntity(new BlockPos(m.x, m.y, m.z));
					if (reac instanceof TileEntityMachineReactorLarge) {
						TileEntityMachineReactorLarge reactor = (TileEntityMachineReactorLarge)reac;
						
						if(m.id == 0) {
							reactor.rods = m.value;
						}
						
						if(m.id == 1) {
							reactor.compress(m.value);
						}
					}
					
					/*if (te instanceof TileEntityMachineMissileAssembly) {
						TileEntityMachineMissileAssembly assembly = (TileEntityMachineMissileAssembly)te;
						
						assembly.construct();
					}
					
					if (te instanceof TileEntityLaunchTable) {
						TileEntityLaunchTable launcher = (TileEntityLaunchTable)te;
						
						launcher.padSize = PartSize.values()[m.value];
					}*/
					
					if (te instanceof TileEntityRailgun) {
						TileEntityRailgun gun = (TileEntityRailgun)te;
						
						if(m.id == 0) {
							if(gun.setAngles(false)) {
								p.world.playSound(null, m.x, m.y, m.z, HBMSoundHandler.buttonYes, SoundCategory.BLOCKS, 1.0F, 1.0F);
								p.world.playSound(null, m.x, m.y, m.z, HBMSoundHandler.railgunOrientation, SoundCategory.BLOCKS, 1.0F, 1.0F);
								PacketDispatcher.wrapper.sendToAll(new RailgunCallbackPacket(m.x, m.y, m.z, gun.pitch, gun.yaw));
							} else {
								System.out.println("re");
								System.out.println(HBMSoundHandler.buttonNo);
								p.world.playSound(null, m.x, m.y, m.z, HBMSoundHandler.buttonNo, SoundCategory.BLOCKS, 1.0F, 1.0F);
							}
						}
						
						if(m.id == 1) {
							if(gun.canFire()) {
								gun.fireDelay = TileEntityRailgun.cooldownDurationTicks;
								PacketDispatcher.wrapper.sendToAll(new RailgunFirePacket(m.x, m.y, m.z));
								p.world.playSound(null, m.x, m.y, m.z, HBMSoundHandler.buttonYes, SoundCategory.BLOCKS, 1.0F, 1.0F);
								p.world.playSound(null, m.x, m.y, m.z, HBMSoundHandler.railgunCharge, SoundCategory.BLOCKS, 10.0F, 1.0F);
							} else {
								p.world.playSound(null, m.x, m.y, m.z, HBMSoundHandler.buttonNo, SoundCategory.BLOCKS, 1.0F, 1.0F);
							}
						}
					}
					
				//} catch (Exception x) { }
			});
			
			
			return null;
		}
	}

}
