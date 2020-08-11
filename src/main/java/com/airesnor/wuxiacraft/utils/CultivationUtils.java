package com.airesnor.wuxiacraft.utils;

import com.airesnor.wuxiacraft.WuxiaCraft;
import com.airesnor.wuxiacraft.capabilities.*;
import com.airesnor.wuxiacraft.cultivation.*;
import com.airesnor.wuxiacraft.cultivation.elements.Element;
import com.airesnor.wuxiacraft.cultivation.skills.ISkillCap;
import com.airesnor.wuxiacraft.cultivation.skills.SkillCap;
import com.airesnor.wuxiacraft.cultivation.skills.Skills;
import com.airesnor.wuxiacraft.cultivation.techniques.CultTech;
import com.airesnor.wuxiacraft.cultivation.techniques.ICultTech;
import com.airesnor.wuxiacraft.entities.effects.EntityLevelUpHalo;
import com.airesnor.wuxiacraft.entities.mobs.EntityCultivator;
import com.airesnor.wuxiacraft.networking.NetworkWrapper;
import com.airesnor.wuxiacraft.networking.UnifiedCapabilitySyncMessage;
import com.airesnor.wuxiacraft.world.dimensions.WuxiaDimensions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CultivationUtils {

	private static final int CONGRATS_MESSAGE_COUNT = 5;

	@Nonnull
	public static ICultivation getCultivationFromEntity(EntityLivingBase entityIn) {
		ICultivation cultivation = null;
		if (entityIn instanceof EntityPlayer) {
			//noinspection ConstantConditions
			cultivation = entityIn.getCapability(CultivationProvider.CULTIVATION_CAP, null);
		} else if (entityIn instanceof EntityCultivator) {
			cultivation = ((EntityCultivator) entityIn).getCultivation();
		}
		if (cultivation == null) {
			cultivation = new Cultivation();
		}
		return cultivation;
	}

	@Nonnull
	public static ICultTech getCultTechFromEntity(EntityLivingBase entityIn) {
		ICultTech cultTech = null;
		if (entityIn instanceof EntityPlayer) {
			//noinspection ConstantConditions
			cultTech = entityIn.getCapability(CultTechProvider.CULT_TECH_CAPABILITY, null);
		}
		if (cultTech == null) {
			cultTech = new CultTech();
		}
		return cultTech;
	}

	@Nonnull
	public static ISkillCap getSkillCapFromEntity(EntityLivingBase entityIn) {
		ISkillCap skillCap = null;
		if (entityIn instanceof EntityPlayer) {
			//noinspection ConstantConditions
			skillCap = entityIn.getCapability(SkillsProvider.SKILL_CAP_CAPABILITY, null);
		} else if (entityIn instanceof EntityCultivator) {
			skillCap = ((EntityCultivator) entityIn).getSkillCap();
		}
		if (skillCap == null) {
			skillCap = new SkillCap();
		}
		return skillCap;
	}

	/*@Nonnull
	public static IFoundation getFoundationFromEntity(EntityLivingBase entityIn) {
		IFoundation foundation = null;
		if (entityIn instanceof EntityPlayer)
			//noinspection ConstantConditions
			foundation = entityIn.getCapability(FoundationProvider.FOUNDATION_CAPABILITY, null);
		if (foundation == null) foundation = new Foundation();
		return foundation;
	}*/

	@Nonnull
	public static ISealing getSealingFromEntity(EntityLivingBase entityIn) {
		ISealing sealing = null;
		if (entityIn instanceof EntityPlayer) {
			//noinspection ConstantConditions
			sealing = entityIn.getCapability(SealingProvider.SEALING_CAPABILITY, null);
		}
		if (sealing == null) {
			sealing = new Sealing();
		}
		return sealing;
	}

	@Nonnull
	public static IBarrier getBarrierFromEntity(EntityLivingBase entityIn) {
		IBarrier barrier = null;
		if (entityIn instanceof EntityPlayer) {
			//noinspection ConstantConditions
			barrier = entityIn.getCapability(BarrierProvider.BARRIER_CAPABILITY, null);
		}
		if (barrier == null) {
			barrier = new Barrier();
		}
		return barrier;
	}

	public static double getMaxEnergy(EntityLivingBase entityIn) {
		double energy = getCultivationFromEntity(entityIn).getMaxEnergy();
		energy *= (1 + getCultTechFromEntity(entityIn).getOverallModifiers().maxEnergy);
		return energy;
	}

	public static void cultivatorAddProgress(EntityLivingBase player, Cultivation.System system, double amount, boolean techniques, boolean allowBreakThrough) {
		ICultivation cultivation = getCultivationFromEntity(player);
		ICultTech cultTech = getCultTechFromEntity(player);
		if(cultTech.getTechniqueBySystem(system) != null) {
			amount *= cultTech.getTechniqueBySystem(system).getCultivationSpeed(cultivation.getSystemModifier(system));
		}
		double enlightenment = 1;
		PotionEffect effect = player.getActivePotionEffect(Skills.ENLIGHTENMENT);
		if (effect != null) {
			enlightenment += 9 * effect.getAmplifier();
		}
		amount *= enlightenment;
		if (techniques) {
			cultTech.progress(amount, system);
		}
		//get world extra modifier
		List<Pair<Element, DimensionType>> elementsDim = new ArrayList<>();
		elementsDim.add(Pair.of(Element.FIRE, WuxiaDimensions.FIRE));
		elementsDim.add(Pair.of(Element.EARTH, WuxiaDimensions.EARTH));
		elementsDim.add(Pair.of(Element.METAL, WuxiaDimensions.METAL));
		elementsDim.add(Pair.of(Element.WATER, WuxiaDimensions.WATER));
		elementsDim.add(Pair.of(Element.WOOD, WuxiaDimensions.WOOD));
		for (Pair<Element, DimensionType> pair : elementsDim) {
			if (player.world.provider.getDimension() == pair.getValue().getId()) {
				if (cultTech.hasElement(pair.getKey())) {
					amount *= 1.75;
				}
				for (Element element : pair.getKey().getCounters()) {
					if (cultTech.hasElement(element)) amount *= 0.75;
				}
			}
		}
		if (!cultivation.getSuppress()) {
			try {
				boolean leveled = cultivation.addSystemProgress(amount, system, allowBreakThrough);
				cultivation.addSystemFoundation(amount * 0.05, system); //5% extra to foundations
				if (cultivation.getBodyLevel() == BaseSystemLevel.DEFAULT_BODY_LEVEL) {
					cultivation.addBodyProgress(amount * 0.3, allowBreakThrough);
				}
				if (cultivation.getDivineLevel() == BaseSystemLevel.DEFAULT_DIVINE_LEVEL) {
					cultivation.addDivineProgress(amount * 0.3, allowBreakThrough);
				}
				if (cultivation.getEssenceLevel() == BaseSystemLevel.DEFAULT_ESSENCE_LEVEL) {
					cultivation.addEssenceProgress(amount * 0.3, allowBreakThrough);
				}
				if(leveled && !player.world.isRemote) {
					EntityLevelUpHalo halo = new EntityLevelUpHalo(player.world, player.posX, player.posY + 1, player.posZ);
					WorldUtils.spawnEntity((WorldServer) player.world, halo);
				}
			} catch (Cultivation.RequiresTribulation tribulation) {
				callTribulation(player, tribulation.tribulationStrength, tribulation.system, tribulation.level, tribulation.subLevel);
			}
		} else {
			cultivation.addSystemFoundation(amount, system);
		}
	}

	//This will call one lightning bolt at a time, or else all bolts would be called at the same time
	private static class BoltsScheduler extends Thread {

		private final EntityLivingBase player;
		private final double tribulationStrength;
		private BaseSystemLevel targetLevel;
		private int targetSubLevel;
		private Cultivation.System system;
		private boolean customTribulation;

		public BoltsScheduler(EntityLivingBase player, double tribulationStrength, Cultivation.System system, BaseSystemLevel targetLevel, int targetSubLevel) {
			this.player = player;
			this.tribulationStrength = tribulationStrength;
			this.system = system;
			this.targetLevel = targetLevel;
			this.targetSubLevel = targetSubLevel;
		}

		public BoltsScheduler(EntityLivingBase player, double tribulationStrength, boolean customTribulation) {
			this.player = player;
			this.tribulationStrength = tribulationStrength;
			this.customTribulation = customTribulation;
		}

		@Override
		public void run() {
			if (player.world instanceof WorldServer) {
				WorldServer world = (WorldServer) player.world;
				ICultivation cultivation = getCultivationFromEntity(player);

				double resistance = cultivation.getBodyModifier() * 0.6 + cultivation.getEssenceModifier() * 0.8 + cultivation.getDivineModifier() * 0.4; //new foundation system is already a way to resist tribulation
				int multiplier = world.getGameRules().hasRule("tribulationMultiplier") ? world.getGameRules().getInt("tribulationMultiplier") : 18; // even harder for those that weren't on the script
				double strength = this.tribulationStrength * multiplier;
				final int bolts = MathUtils.clamp(1 + (int) (Math.round(resistance / strength)), 1, 12);
				float damage = (float) Math.max(2, strength - resistance);
				for (int i = 0; i < bolts; i++) {
					boolean survived = player.isEntityAlive();
					if (!survived) return;
					world.addScheduledTask(() -> {
						EntityLightningBolt lightningBolt = new EntityLightningBolt(world, player.posX, player.posY + 1.0, player.posZ, true); // effect only won't cause damage
						world.addWeatherEffect(lightningBolt);
						player.attackEntityFrom(DamageSource.LIGHTNING_BOLT.setDamageIsAbsolute().setDamageBypassesArmor(), damage / bolts);
					});
					try {
						sleep(750);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				int msgN = player.world.rand.nextInt(CONGRATS_MESSAGE_COUNT);
				world.addScheduledTask(() -> {
					boolean survived = player.isEntityAlive();
					if (survived && !this.customTribulation) {
						switch (this.system) {
							case BODY:
								cultivation.setBodyProgress(cultivation.getBodyProgress() -
										cultivation.getBodyLevel().getProgressBySubLevel(cultivation
												.getBodySubLevel()));
								cultivation.setBodyLevel(this.targetLevel);
								cultivation.setBodySubLevel(this.targetSubLevel);
								cultivation.addBodyFoundation(this.tribulationStrength * (0.03 + 0.7 * this.player.getRNG().nextDouble()));
								break;
							case DIVINE:
								cultivation.setDivineProgress(cultivation.getDivineProgress() -
										cultivation.getDivineLevel().getProgressBySubLevel(cultivation
												.getDivineSubLevel()));
								cultivation.setDivineLevel(this.targetLevel);
								cultivation.setDivineSubLevel(this.targetSubLevel);
								cultivation.addDivineFoundation(this.tribulationStrength * (0.03 + 0.7 * this.player.getRNG().nextDouble()));
								break;
							case ESSENCE:
								cultivation.setEssenceFoundation(cultivation.getEssenceProgress() -
										cultivation.getEssenceLevel().getProgressBySubLevel(cultivation
												.getEssenceSubLevel()));
								cultivation.setEssenceLevel(this.targetLevel);
								cultivation.setEssenceSubLevel(this.targetSubLevel);
								cultivation.addEssenceFoundation(this.tribulationStrength * (0.03 + 0.7 * this.player.getRNG().nextDouble()));
								break;
						}
						((EntityPlayer) player).sendStatusMessage(new TextComponentString(TranslateUtils.translateKey("wuxiacraft.level_message.congrats_" + msgN) + " " + targetLevel.getLevelName(targetSubLevel)), false);
						NetworkWrapper.INSTANCE.sendTo(new UnifiedCapabilitySyncMessage(cultivation, getCultTechFromEntity(player), getSkillCapFromEntity(player), false), (EntityPlayerMP) player);
					}
				});
			}
		}
	}

	public static void callTribulation(@Nonnull EntityLivingBase player, double tribulationStrength, Cultivation.System system, BaseSystemLevel level, int subLevel) {
		if (!player.world.isRemote) {
			BoltsScheduler boltsScheduler = new BoltsScheduler(player, tribulationStrength, system, level, subLevel);
			boltsScheduler.start();
		}
	}

	public static void callCustomTribulation(@Nonnull EntityLivingBase player, double tribulationStrength, boolean customTribulation) {
		if (!player.world.isRemote) {
			BoltsScheduler boltsScheduler = new BoltsScheduler(player, tribulationStrength, customTribulation);
			boltsScheduler.start();
		}
	}

	private static class CustomThunder extends Thread {

		private final EntityLivingBase player;
		private final double strength;
		private final double reward;

		public CustomThunder(EntityLivingBase player, double strength, double reward) {
			this.player = player;
			this.strength = strength;
			this.reward = reward;
		}

		@Override
		public void run() {
			if (player.world instanceof WorldServer) {
				WorldServer world = (WorldServer) player.world;
				world.addScheduledTask(() -> {
					EntityLightningBolt lightningBolt = new EntityLightningBolt(world, player.posX, player.posY + 1.0, player.posZ, true); // effect only won't cause damage
					world.addWeatherEffect(lightningBolt);
					player.attackEntityFrom(DamageSource.LIGHTNING_BOLT.setDamageIsAbsolute().setDamageBypassesArmor(), (float)this.strength);
				});
				try {
					sleep(750);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				world.addScheduledTask(() -> {
					if (player.isEntityAlive()) {
						ICultivation cultivation = CultivationUtils.getCultivationFromEntity(player);
						try {
							cultivation.addBodyProgress(this.reward, false);
						} catch (Cultivation.RequiresTribulation tribulation) {
							WuxiaCraft.logger.error("Something is wrong with " + player.getName() + " cultivation technique...");
						}
					}
				});
			}
		}
	}

	public static void callCustomThunder(EntityLivingBase player, double strength, double reward) {
		if(!player.world.isRemote) {
			new CustomThunder(player, strength, reward).start();
		}
	}

}
