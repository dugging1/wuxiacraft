package com.airesnor.wuxiacraft.networking;

import com.airesnor.wuxiacraft.WuxiaCraft;
import com.airesnor.wuxiacraft.capabilities.CultivationProvider;
import com.airesnor.wuxiacraft.cultivation.ICultivation;
import com.airesnor.wuxiacraft.handlers.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CultivationMessageHandler implements IMessageHandler {
	@Override
	public IMessage onMessage(IMessage message, MessageContext ctx) {
		if(ctx.side == Side.CLIENT) {
			if(message instanceof CultivationMessage) {
				Minecraft.getMinecraft().addScheduledTask(() -> {
					CultivationMessage cm = (CultivationMessage)message;
					EntityPlayer player = Minecraft.getMinecraft().player;
					ICultivation cultivation = player.getCapability(CultivationProvider.CULTIVATION_CAP,  null);
					if(cultivation != null) {
						cultivation.setCurrentLevel(cm.messageLevel);
						cultivation.setCurrentSubLevel(cm.messageSubLevel);
						cultivation.setProgress(cm.messageProgress);
						cultivation.setEnergy(cm.messageEnergy);
					} else {
						WuxiaCraft.logger.info("He ain't a cultivator. Weeird");
					}
				});
			}
		}

		return null;
	}
}
