package com.hbm.main;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.mob.EntityHunterChopper;
import com.hbm.entity.projectile.EntityChopperMine;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.handler.BulletConfiguration;
import com.hbm.handler.GunConfiguration;
import com.hbm.interfaces.IHoldableWeapon;
import com.hbm.items.ModItems;
import com.hbm.items.tool.ItemGeigerCounter;
import com.hbm.items.weapon.ItemGunBase;
import com.hbm.lib.Library;
import com.hbm.lib.RefStrings;
import com.hbm.packet.GunButtonPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.render.misc.RenderAccessoryUtility;
import com.hbm.render.misc.RenderScreenOverlay;
import com.hbm.render.misc.RenderScreenOverlay.Crosshair;
import com.hbm.render.model.ModelCloak;
import com.hbm.saveddata.RadEntitySavedData;
import com.hbm.saveddata.RadiationSavedData;
import com.hbm.sound.MovingSoundChopper;
import com.hbm.sound.MovingSoundChopperMine;
import com.hbm.sound.MovingSoundCrashing;
import com.hbm.sound.MovingSoundPlayerLoop;
import com.hbm.sound.MovingSoundXVL1456;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.hbm.sound.MovingSoundPlayerLoop.EnumHbmSound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;



public class ModEventHandlerClient {
	
	IIcon[] fluidIcons = new IIcon[72];
	
	@SubscribeEvent
	public void onOverlayRender(RenderGameOverlayEvent.Pre event) {
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if(player.getUniqueID().toString().equals("c874fd4e-5841-42e4-8f77-70efd5881bc1"))
			if(player.ticksExisted > 5 * 60 * 20)
				Minecraft.getMinecraft().entityRenderer.debugViewDirection = 5;
		
		if(event.type == ElementType.HOTBAR && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemGunBase) {
			
			ItemGunBase gun = ((ItemGunBase)player.getHeldItem().getItem());
			GunConfiguration gcfg = gun.mainConfig;
			BulletConfiguration bcfg = BulletConfigSyncingUtil.pullConfig(gun.mainConfig.config.get(ItemGunBase.getMagType(player.getHeldItem())));
			
			Item ammo = bcfg.ammo;
			int count = ItemGunBase.getMag(player.getHeldItem());
			int max = gcfg.ammoCap;
			
			if(gcfg.reloadType == gcfg.RELOAD_NONE) {
				ammo = ItemGunBase.getBeltType(player, player.getHeldItem());
				count = ItemGunBase.getBeltSize(player, ammo);
				max = -1;
			}
			
			int dura = ItemGunBase.getItemWear(player.getHeldItem()) * 50 / gcfg.durability;
			
			RenderScreenOverlay.renderAmmo(event.resolution, Minecraft.getMinecraft().ingameGUI, ammo, count, max, dura);
			//RenderScreenOverlay.renderRadCounter(event.resolution, 0, Minecraft.getMinecraft().ingameGUI);
		}
		
		if(event.type == ElementType.HOTBAR) {
			
			if(player.inventory.hasItem(ModItems.geiger_counter)) {

				float rads = 0;
				float abs = 0;

				RadEntitySavedData data = RadEntitySavedData.getData(player.worldObj);
				rads = data.getRadFromEntity(player);
				
				RenderScreenOverlay.renderRadCounter(event.resolution, rads, Minecraft.getMinecraft().ingameGUI);
			}
		}
		
		if(event.type == ElementType.CROSSHAIRS && player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IHoldableWeapon) {
			event.setCanceled(true);
			
			if(!(player.getHeldItem().getItem() instanceof ItemGunBase && ((ItemGunBase)player.getHeldItem().getItem()).mainConfig.hasSights && player.isSneaking()))
				RenderScreenOverlay.renderCustomCrosshairs(event.resolution, Minecraft.getMinecraft().ingameGUI, ((IHoldableWeapon)player.getHeldItem().getItem()).getCrosshair());
			
		}
	}
	
	@SubscribeEvent
	public void preRenderEvent(RenderPlayerEvent.Pre event) {
		
		//event.setCanceled(true);
		RenderPlayer renderer = event.renderer;
		AbstractClientPlayer player = (AbstractClientPlayer)event.entityPlayer;
		
		ResourceLocation cloak = RenderAccessoryUtility.getCloakFromPlayer(player);
		
		//GL11.glRotated(180, 1, 0, 0);
		
		if(cloak != null)
			player.func_152121_a(Type.CAPE, cloak);
		
		if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IHoldableWeapon) {
			renderer.modelBipedMain.aimedBow = true;
			renderer.modelArmor.aimedBow = true;
			renderer.modelArmorChestplate.aimedBow = true;
		}
	}

	@SubscribeEvent
	public void clickHandler(MouseEvent event) {
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemGunBase) {
			
			if(event.button == 0)
				event.setCanceled(true);
			
			ItemGunBase item = (ItemGunBase)player.getHeldItem().getItem();
			
			if(event.button == 0 && !item.m1 && !item.m2) {
				item.m1 = true;
				PacketDispatcher.wrapper.sendToServer(new GunButtonPacket(true, (byte) 0));
				//System.out.println("M1");
			}
			else if(event.button == 1 && !item.m2 && !item.m1) {
				item.m2 = true;
				PacketDispatcher.wrapper.sendToServer(new GunButtonPacket(true, (byte) 1));
				//System.out.println("M2");
			}
		}

	}

	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent17 e) {
		ResourceLocation r = e.sound.getPositionedSoundLocation();

		WorldClient wc = Minecraft.getMinecraft().theWorld;
		
		//Alright, alright, I give the fuck up, you've wasted my time enough with this bullshit. You win.
		//A winner is you.
		//Conglaturations.
		//Fuck you.

		if(r.toString().equals("hbm:misc.nullTau") && Library.getClosestPlayerForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2) != null)
		{
			EntityPlayer ent = Library.getClosestPlayerForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2);
			
			if(MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundTauLoop) == null) {
				MovingSoundPlayerLoop.globalSoundList.add(new MovingSoundXVL1456(new ResourceLocation("hbm:weapon.tauChargeLoop2"), ent, EnumHbmSound.soundTauLoop));
				MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundTauLoop).setPitch(0.5F);
			} else {
				if(MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundTauLoop).getPitch() < 1.5F)
				MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundTauLoop).setPitch(MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundTauLoop).getPitch() + 0.01F);
			}
		}
		
		if(r.toString().equals("hbm:misc.nullChopper") && Library.getClosestChopperForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2) != null)
		{
			EntityHunterChopper ent = Library.getClosestChopperForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2);
			
			if(MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundChopperLoop) == null) {
				MovingSoundPlayerLoop.globalSoundList.add(new MovingSoundChopper(new ResourceLocation("hbm:entity.chopperFlyingLoop"), ent, EnumHbmSound.soundChopperLoop));
				MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundChopperLoop).setVolume(10.0F);
			}
		}
		
		if(r.toString().equals("hbm:misc.nullCrashing") && Library.getClosestChopperForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2) != null)
		{
			EntityHunterChopper ent = Library.getClosestChopperForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2);
			
			if(MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundCrashingLoop) == null) {
				MovingSoundPlayerLoop.globalSoundList.add(new MovingSoundCrashing(new ResourceLocation("hbm:entity.chopperCrashingLoop"), ent, EnumHbmSound.soundCrashingLoop));
				MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundCrashingLoop).setVolume(10.0F);
			}
		}
		
		if(r.toString().equals("hbm:misc.nullMine") && Library.getClosestMineForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2) != null)
		{
			EntityChopperMine ent = Library.getClosestMineForSound(wc, e.sound.getXPosF(), e.sound.getYPosF(), e.sound.getZPosF(), 2);
			
			if(MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundMineLoop) == null) {
				MovingSoundPlayerLoop.globalSoundList.add(new MovingSoundChopperMine(new ResourceLocation("hbm:entity.chopperMineLoop"), ent, EnumHbmSound.soundMineLoop));
				MovingSoundPlayerLoop.getSoundByPlayer(ent, EnumHbmSound.soundMineLoop).setVolume(10.0F);
			}
		}

		for(MovingSoundPlayerLoop sounds : MovingSoundPlayerLoop.globalSoundList)
		{
			if(!sounds.init || sounds.isDonePlaying()) {
				sounds.init = true;
				sounds.setDone(false);
				Minecraft.getMinecraft().getSoundHandler().playSound(sounds);
			}
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerIcons(TextureStitchEvent.Pre event) {
		if(event.map.getTextureType() == 0){
			fluidIcons[0] = event.map.registerIcon("hbm:forgefluid/steam_still");
			fluidIcons[1] = event.map.registerIcon("hbm:forgefluid/steam_flowing");
			fluidIcons[2] = event.map.registerIcon("hbm:forgefluid/hotsteam_still");
			fluidIcons[3] = event.map.registerIcon("hbm:forgefluid/hotsteam_flowing");
			fluidIcons[4] = event.map.registerIcon("hbm:forgefluid/superhotsteam_still");
			fluidIcons[5] = event.map.registerIcon("hbm:forgefluid/superhotsteam_flowing");
			fluidIcons[6] = event.map.registerIcon("hbm:forgefluid/coolant_still");
			fluidIcons[7] = event.map.registerIcon("hbm:forgefluid/coolant_flowing");
			
			fluidIcons[8] = event.map.registerIcon("hbm:forgefluid/deuterium_still");
			fluidIcons[9] = event.map.registerIcon("hbm:forgefluid/deuterium_flowing");
			fluidIcons[10] = event.map.registerIcon("hbm:forgefluid/tritium_still");
			fluidIcons[11] = event.map.registerIcon("hbm:forgefluid/tritium_flowing");
			
			fluidIcons[12] = event.map.registerIcon("hbm:forgefluid/oil_still");
			fluidIcons[13] = event.map.registerIcon("hbm:forgefluid/oil_flowing");
			fluidIcons[14] = event.map.registerIcon("hbm:forgefluid/hotoil_still");
			fluidIcons[15] = event.map.registerIcon("hbm:forgefluid/hotoil_flowing");
			
			fluidIcons[16] = event.map.registerIcon("hbm:forgefluid/heavyoil_still");
			fluidIcons[17] = event.map.registerIcon("hbm:forgefluid/heavyoil_flowing");
			fluidIcons[18] = event.map.registerIcon("hbm:forgefluid/bitumen_still");
			fluidIcons[19] = event.map.registerIcon("hbm:forgefluid/bitumen_flowing");
			fluidIcons[20] = event.map.registerIcon("hbm:forgefluid/smear_still");
			fluidIcons[21] = event.map.registerIcon("hbm:forgefluid/smear_flowing");
			fluidIcons[22] = event.map.registerIcon("hbm:forgefluid/heatingoil_still");
			fluidIcons[23] = event.map.registerIcon("hbm:forgefluid/heatingoil_flowing");
			
			fluidIcons[24] = event.map.registerIcon("hbm:forgefluid/reclaimed_still");
			fluidIcons[25] = event.map.registerIcon("hbm:forgefluid/reclaimed_flowing");
			fluidIcons[26] = event.map.registerIcon("hbm:forgefluid/petroil_still");
			fluidIcons[27] = event.map.registerIcon("hbm:forgefluid/petroil_flowing");
			
			fluidIcons[28] = event.map.registerIcon("hbm:forgefluid/lubricant_still");
			fluidIcons[29] = event.map.registerIcon("hbm:forgefluid/lubricant_flowing");
			
			fluidIcons[30] = event.map.registerIcon("hbm:forgefluid/napatha_still");
			fluidIcons[31] = event.map.registerIcon("hbm:forgefluid/napatha_flowing");
			fluidIcons[32] = event.map.registerIcon("hbm:forgefluid/diesel_still");
			fluidIcons[33] = event.map.registerIcon("hbm:forgefluid/diesel_flowing");
			
			fluidIcons[34] = event.map.registerIcon("hbm:forgefluid/lightoil_still");
			fluidIcons[35] = event.map.registerIcon("hbm:forgefluid/lightoil_flowing");
			fluidIcons[36] = event.map.registerIcon("hbm:forgefluid/kerosene_still");
			fluidIcons[37] = event.map.registerIcon("hbm:forgefluid/kerosene_flowing");
			
			fluidIcons[38] = event.map.registerIcon("hbm:forgefluid/gas_still");
			fluidIcons[39] = event.map.registerIcon("hbm:forgefluid/gas_flowing");
			fluidIcons[40] = event.map.registerIcon("hbm:forgefluid/petroleum_still");
			fluidIcons[41] = event.map.registerIcon("hbm:forgefluid/petroleum_flowing");
			
			fluidIcons[42] = event.map.registerIcon("hbm:forgefluid/biogas_still");
			fluidIcons[43] = event.map.registerIcon("hbm:forgefluid/biogas_flowing");
			fluidIcons[44] = event.map.registerIcon("hbm:forgefluid/biofuel_still");
			fluidIcons[45] = event.map.registerIcon("hbm:forgefluid/biofuel_flowing");
			
			fluidIcons[46] = event.map.registerIcon("hbm:forgefluid/nitan_still");
			fluidIcons[47] = event.map.registerIcon("hbm:forgefluid/nitan_flowing");
			
			fluidIcons[48] = event.map.registerIcon("hbm:forgefluid/uf6_still");
			fluidIcons[49] = event.map.registerIcon("hbm:forgefluid/uf6_flowing");
			fluidIcons[50] = event.map.registerIcon("hbm:forgefluid/puf6_still");
			fluidIcons[51] = event.map.registerIcon("hbm:forgefluid/puf6_flowing");
			fluidIcons[52] = event.map.registerIcon("hbm:forgefluid/sas3_still");
			fluidIcons[53] = event.map.registerIcon("hbm:forgefluid/sas3_flowing");
			
			fluidIcons[54] = event.map.registerIcon("hbm:forgefluid/amat_still");
			fluidIcons[55] = event.map.registerIcon("hbm:forgefluid/amat_flowing");
			fluidIcons[56] = event.map.registerIcon("hbm:forgefluid/aschrab_still");
			fluidIcons[57] = event.map.registerIcon("hbm:forgefluid/aschrab_flowing");
			
			fluidIcons[58] = event.map.registerIcon("hbm:forgefluid/acid_still");
			fluidIcons[59] = event.map.registerIcon("hbm:forgefluid/acid_flowing");
			fluidIcons[60] = event.map.registerIcon("hbm:forgefluid/watz_still");
			fluidIcons[61] = event.map.registerIcon("hbm:forgefluid/watz_flowing");
			fluidIcons[62] = event.map.registerIcon("hbm:forgefluid/cryogel_still");
			fluidIcons[63] = event.map.registerIcon("hbm:forgefluid/cryogel_flowing");
			
			fluidIcons[64] = event.map.registerIcon("hbm:forgefluid/hydrogen_still");
			fluidIcons[65] = event.map.registerIcon("hbm:forgefluid/hydrogen_flowing");
			fluidIcons[66] = event.map.registerIcon("hbm:forgefluid/oxygen_still");
			fluidIcons[67] = event.map.registerIcon("hbm:forgefluid/oxygen_flowing");
			fluidIcons[68] = event.map.registerIcon("hbm:forgefluid/xenon_still");
			fluidIcons[69] = event.map.registerIcon("hbm:forgefluid/xenon_flowing");
			fluidIcons[70] = event.map.registerIcon("hbm:forgefluid/balefire_still");
			fluidIcons[71] = event.map.registerIcon("hbm:forgefluid/balefire_flowing");
		}
		

	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initializeIcons(TextureStitchEvent.Post event) {
		if(event.map.getTextureType() == 0){
			ModForgeFluids.steam.setIcons(fluidIcons[0], fluidIcons[1]);
			ModForgeFluids.hotsteam.setIcons(fluidIcons[2], fluidIcons[3]);
			ModForgeFluids.superhotsteam.setIcons(fluidIcons[4], fluidIcons[5]);
			ModForgeFluids.coolant.setIcons(fluidIcons[6], fluidIcons[7]);
			
			ModForgeFluids.deuterium.setIcons(fluidIcons[8], fluidIcons[9]);
			ModForgeFluids.tritium.setIcons(fluidIcons[10], fluidIcons[11]);
			
			ModForgeFluids.oil.setIcons(fluidIcons[12], fluidIcons[13]);
			ModForgeFluids.hotoil.setIcons(fluidIcons[14], fluidIcons[15]);
			
			ModForgeFluids.heavyoil.setIcons(fluidIcons[16], fluidIcons[17]);
			ModForgeFluids.bitumen.setIcons(fluidIcons[18], fluidIcons[19]);
			ModForgeFluids.smear.setIcons(fluidIcons[20], fluidIcons[21]);
			ModForgeFluids.heatingoil.setIcons(fluidIcons[22], fluidIcons[23]);
			
			ModForgeFluids.reclaimed.setIcons(fluidIcons[24], fluidIcons[25]);
			ModForgeFluids.petroil.setIcons(fluidIcons[26], fluidIcons[27]);
			
			ModForgeFluids.lubricant.setIcons(fluidIcons[28], fluidIcons[29]);
			
			ModForgeFluids.napatha.setIcons(fluidIcons[30], fluidIcons[31]);
			ModForgeFluids.diesel.setIcons(fluidIcons[32], fluidIcons[33]);
			
			ModForgeFluids.lightoil.setIcons(fluidIcons[34], fluidIcons[35]);
			ModForgeFluids.kerosene.setIcons(fluidIcons[36], fluidIcons[37]);
			
			ModForgeFluids.gas.setIcons(fluidIcons[38], fluidIcons[39]);
			ModForgeFluids.petroleum.setIcons(fluidIcons[40], fluidIcons[41]);
			
			ModForgeFluids.biogas.setIcons(fluidIcons[42], fluidIcons[43]);
			ModForgeFluids.biofuel.setIcons(fluidIcons[44], fluidIcons[45]);
			
			ModForgeFluids.nitan.setIcons(fluidIcons[46], fluidIcons[47]);
			
			ModForgeFluids.uf6.setIcons(fluidIcons[48], fluidIcons[49]);
			ModForgeFluids.puf6.setIcons(fluidIcons[50], fluidIcons[51]);
			ModForgeFluids.sas3.setIcons(fluidIcons[52], fluidIcons[53]);
			
			ModForgeFluids.amat.setIcons(fluidIcons[54], fluidIcons[55]);
			ModForgeFluids.aschrab.setIcons(fluidIcons[56], fluidIcons[57]);
			
			ModForgeFluids.acid.setIcons(fluidIcons[58], fluidIcons[59]);
			ModForgeFluids.watz.setIcons(fluidIcons[60], fluidIcons[61]);
			ModForgeFluids.cryogel.setIcons(fluidIcons[62], fluidIcons[63]);
			
			ModForgeFluids.hydrogen.setIcons(fluidIcons[64], fluidIcons[65]);
			ModForgeFluids.oxygen.setIcons(fluidIcons[66], fluidIcons[67]);
			ModForgeFluids.xenon.setIcons(fluidIcons[68], fluidIcons[69]);
			ModForgeFluids.balefire.setIcons(fluidIcons[70], fluidIcons[71]);
		}
		

	}
	
}
