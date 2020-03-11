package com.airesnor.wuxiacraft.capabilities;

import com.airesnor.wuxiacraft.WuxiaCraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilitiesHandler {

	private final ResourceLocation CULTIVATION_CAP = new ResourceLocation(WuxiaCraft.MODID, "cultivation");
	private final ResourceLocation CULT_TECH_CAP = new ResourceLocation(WuxiaCraft.MODID, "culttech");
	private final ResourceLocation SKILLS_CAP = new ResourceLocation(WuxiaCraft.MODID, "skillscap");

	@SubscribeEvent
	public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof EntityPlayer)) return;

		event.addCapability(CULTIVATION_CAP, new CultivationProvider());
		event.addCapability(CULT_TECH_CAP, new CultTechProvider());
		event.addCapability(SKILLS_CAP, new SkillsProvider());
	}

}
