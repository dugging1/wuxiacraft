package com.airesnor.wuxiacraft.networking;

import com.airesnor.wuxiacraft.cultivation.CultivationLevel;
import com.airesnor.wuxiacraft.cultivation.ICultivation;
import com.airesnor.wuxiacraft.utils.CultivationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

public class AskCultivationLevelMessageHandler implements IMessageHandler<AskCultivationLevelMessage, RespondCultivationLevelMessage> {
	@Override
	public RespondCultivationLevelMessage onMessage(AskCultivationLevelMessage message, MessageContext ctx) {
		if (ctx.side == Side.SERVER) {
			final WorldServer world = ctx.getServerHandler().player.getServerWorld();
			world.addScheduledTask(() -> {
				EntityPlayer player = world.getPlayerEntityByName(message.askerName);
				if(player != null) {
					List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, player.getEntityBoundingBox().grow(64, 32, 64));
					for (Entity entity : entities) {
						if (entity instanceof EntityPlayer) {
							ICultivation cultivation = CultivationUtils.getCultivationFromEntity((EntityLivingBase) entity);
							CultivationLevel level = cultivation.getCurrentLevel();
							int subLevel = cultivation.getCurrentSubLevel();
							if (level.greaterThan(message.askerLevel)) {
								level = message.askerLevel.getNextLevel();
								subLevel = -1;
							}
							RespondCultivationLevelMessage respondCultivationLevelMessage = new RespondCultivationLevelMessage(level, subLevel, entity.getName());
							NetworkWrapper.INSTANCE.sendTo(respondCultivationLevelMessage, (EntityPlayerMP) player);
						}
					}
				}
			});
		}
		return null;
	}
}