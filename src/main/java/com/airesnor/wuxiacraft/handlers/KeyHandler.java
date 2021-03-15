package com.airesnor.wuxiacraft.handlers;

import com.airesnor.wuxiacraft.WuxiaCraft;
import com.airesnor.wuxiacraft.config.WuxiaCraftConfig;
import com.airesnor.wuxiacraft.cultivation.IBarrier;
import com.airesnor.wuxiacraft.cultivation.ICultivation;
import com.airesnor.wuxiacraft.cultivation.skills.ISkillCap;
import com.airesnor.wuxiacraft.cultivation.skills.Skills;
import com.airesnor.wuxiacraft.cultivation.techniques.ICultTech;
import com.airesnor.wuxiacraft.items.ItemHerb;
import com.airesnor.wuxiacraft.networking.*;
import com.airesnor.wuxiacraft.proxy.ClientProxy;
import com.airesnor.wuxiacraft.utils.CultivationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber
public class KeyHandler {

	public static boolean isCultivating = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onKeyPress(InputEvent.KeyInputEvent event) {
		KeyBinding[] keyBindings = ClientProxy.keyBindings;
		EntityPlayer player = Minecraft.getMinecraft().player;

		if (keyBindings[0].isPressed()) {
			WuxiaCraftConfig.speedHandicap = Math.min(100, WuxiaCraftConfig.speedHandicap + 5);
			WuxiaCraftConfig.syncFromFields();
			WuxiaCraftConfig.syncCultivationFromConfigToClient();
			NetworkWrapper.INSTANCE.sendToServer(new SpeedHandicapMessage(WuxiaCraftConfig.speedHandicap, WuxiaCraftConfig.maxSpeed, WuxiaCraftConfig.blockBreakLimit, WuxiaCraftConfig.jumpLimit, WuxiaCraftConfig.stepAssistLimit, player.getUniqueID()));
		}
		if (keyBindings[1].isPressed()) {
			WuxiaCraftConfig.speedHandicap = Math.max(0, WuxiaCraftConfig.speedHandicap - 5);
			WuxiaCraftConfig.syncFromFields();
			WuxiaCraftConfig.syncCultivationFromConfigToClient();
			NetworkWrapper.INSTANCE.sendToServer(new SpeedHandicapMessage(WuxiaCraftConfig.speedHandicap, WuxiaCraftConfig.maxSpeed, WuxiaCraftConfig.blockBreakLimit, WuxiaCraftConfig.jumpLimit, WuxiaCraftConfig.stepAssistLimit, player.getUniqueID()));
		}
		if (keyBindings[2].isPressed()) {
			BlockPos pos = player.getPosition();
			player.openGui(WuxiaCraft.instance, GuiHandler.CULTIVATION_GUI_ID, player.world, pos.getX(), pos.getY(), pos.getZ());
			//NetworkWrapper.INSTANCE.sendToServer(new RequestCultGuiMessage(true));
		}
		if (Keyboard.getEventKey() == keyBindings[3].getKeyCode() && Keyboard.getEventKeyState()) {
			ISkillCap skillCap = CultivationUtils.getSkillCapFromEntity(player);
			skillCap.setCasting(true);
		}
		if (Keyboard.getEventKey() == keyBindings[3].getKeyCode() && !Keyboard.getEventKeyState()) {
			ISkillCap skillCap = CultivationUtils.getSkillCapFromEntity(player);
			skillCap.setCasting(false);
			skillCap.setDoneCasting(true);
		}
		if (keyBindings[4].isPressed()) {
			BlockPos pos = player.getPosition();
			player.openGui(WuxiaCraft.instance, GuiHandler.SKILLS_GUI_ID, player.world, pos.getX(), pos.getY(), pos.getZ());
		}
		if (keyBindings[17].isPressed()) {
			WuxiaCraftConfig.maxSpeed = WuxiaCraftConfig.maxSpeed * -1;
			WuxiaCraftConfig.syncFromFields();
			WuxiaCraftConfig.syncCultivationFromConfigToClient();
			NetworkWrapper.INSTANCE.sendToServer(new SpeedHandicapMessage(WuxiaCraftConfig.speedHandicap, WuxiaCraftConfig.maxSpeed, WuxiaCraftConfig.blockBreakLimit, WuxiaCraftConfig.jumpLimit, WuxiaCraftConfig.stepAssistLimit, player.getUniqueID()));
		}
		if (keyBindings[18].isPressed()) {
			IBarrier barrier = CultivationUtils.getBarrierFromEntity(player);
			if (barrier.isBarrierActive()) {
				barrier.setBarrierActive(false);
				barrier.setBarrierRegenActive(true);
				NetworkWrapper.INSTANCE.sendToServer(new BarrierMessage(barrier, player.getUniqueID()));
				if (!barrier.isBarrierActive()) {
					TextComponentString text = new TextComponentString("Barrier has been deactivated!");
					text.getStyle().setColor(TextFormatting.AQUA);
					player.sendMessage(text);
				}
			} else {
				if (!barrier.isBarrierBroken()) {
					barrier.setBarrierActive(true);
					barrier.setBarrierRegenActive(true);
					NetworkWrapper.INSTANCE.sendToServer(new BarrierMessage(barrier, player.getUniqueID()));
				}
				if (barrier.isBarrierActive()) {
					TextComponentString text = new TextComponentString("Barrier has been activated!");
					text.getStyle().setColor(TextFormatting.AQUA);
					player.sendMessage(text);
				} else {
					TextComponentString text = new TextComponentString("Barrier has been broken and can not be activated!");
					text.getStyle().setColor(TextFormatting.RED);
					player.sendMessage(text);
				}
			}
		}
		if (keyBindings[19].isKeyDown() && keyBindings[5].isKeyDown()) {
			ICultivation cultivation = CultivationUtils.getCultivationFromEntity(Minecraft.getMinecraft().player);
			cultivation.setSelectedSystem(cultivation.getSelectedSystem().next());
			RendererHandler.showingSelector = true;
			RendererHandler.showingCooldown = 60;
		} else if (keyBindings[5].isKeyDown()) {
			ISkillCap skillCap = CultivationUtils.getSkillCapFromEntity(player);
			int next = skillCap.getActiveSkill() + 1;
			NetworkWrapper.INSTANCE.sendToServer(new SelectSkillMessage(next, player.getUniqueID()));
			SelectSkillMessage.Handler.selectSkill(skillCap, next);
		}
		if (keyBindings[19].isKeyDown() && keyBindings[6].isKeyDown()) {
			ICultivation cultivation = CultivationUtils.getCultivationFromEntity(Minecraft.getMinecraft().player);
			cultivation.setSelectedSystem(cultivation.getSelectedSystem().previous());
			RendererHandler.showingSelector = true;
			RendererHandler.showingCooldown = 60;
		} else if (keyBindings[6].isKeyDown()) {
			ISkillCap skillCap = CultivationUtils.getSkillCapFromEntity(player);
			int next = skillCap.getActiveSkill() - 1;
			NetworkWrapper.INSTANCE.sendToServer(new SelectSkillMessage(next, player.getUniqueID()));
			SelectSkillMessage.Handler.selectSkill(skillCap, next);
		}
		if (Keyboard.getEventKey() == keyBindings[19].getKeyCode() && Keyboard.getEventKeyState()) {
			isCultivating = true;
		}
		if (Keyboard.getEventKey() == keyBindings[19].getKeyCode() && !Keyboard.getEventKeyState()) {
			isCultivating = false;
		}
		if (keyBindings[20].isKeyDown()) {
			ICultivation cultivation = CultivationUtils.getCultivationFromEntity(Minecraft.getMinecraft().player);
			ICultTech cultTech = CultivationUtils.getCultTechFromEntity(Minecraft.getMinecraft().player);
			switch (cultivation.getSelectedSystem()) {
				case BODY:
					if (cultTech.getBodyTechnique() != null)
						Skills.CULTIVATE_BODY.activate(Minecraft.getMinecraft().player);
					break;
				case DIVINE:
					if (cultTech.getDivineTechnique() != null)
						Skills.CULTIVATE_DIVINE.activate(Minecraft.getMinecraft().player);
					break;
				case ESSENCE:
					if (cultTech.getEssenceTechnique() != null)
						Skills.CULTIVATE_ESSENCE.activate(Minecraft.getMinecraft().player);
					break;
			}
		}
		for (int i = 0; i < 10; i++) {
			if (keyBindings[7 + i].isPressed()) {
				ISkillCap skillCap = CultivationUtils.getSkillCapFromEntity(player);
				NetworkWrapper.INSTANCE.sendToServer(new SelectSkillMessage(i, player.getUniqueID()));
				SelectSkillMessage.Handler.selectSkill(skillCap, i);
			}
		}

	}

}
