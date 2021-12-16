package wuxiacraft.item;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import wuxiacraft.WuxiaCraft;
import wuxiacraft.cultivation.Cultivation;
import wuxiacraft.cultivation.CultivationLevel;
import wuxiacraft.cultivation.ICultivation;
import wuxiacraft.cultivation.SystemStats;
import net.minecraft.stats.Stats;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ResourceStoneItem extends Item {

    protected CultivationLevel.System system;
    protected double recoveredAmount;
    protected double damageCoeff;

    public ResourceStoneItem(Properties prop, CultivationLevel.System system, double recoveredAmount, double damageCoeff) {
        super(prop);
        this.system = system;
        this.recoveredAmount = recoveredAmount;
        this.damageCoeff = damageCoeff;
    }

    public ResourceStoneItem(Properties prop, CultivationLevel.System system, double recoveredAmount) {
        this(prop, system, recoveredAmount, 0.2);
    }

    @Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ICultivation cultivation = Cultivation.get(playerIn);
        double maxEnergy;
        switch(system) {
            case BODY: maxEnergy = cultivation.getMaxBodyEnergy(); break;
            case DIVINE: maxEnergy = cultivation.getMaxDivineEnergy(); break;
            case ESSENCE: maxEnergy = cultivation.getMaxDivineEnergy(); break;
            default: throw new IllegalStateException("Resource stone recovers energy for an unknown system: "+system.toString());
        }
		SystemStats stats = cultivation.getStatsBySystem(system);
        
        double extraRecover = Math.max(0, recoveredAmount - (maxEnergy-stats.getEnergy()));
        stats.setEnergy(Math.min(maxEnergy, stats.getEnergy() + recoveredAmount));
        applyDamage(playerIn, cultivation, extraRecover*damageCoeff);

		playerIn.getHeldItem(handIn).shrink(1);
		return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
	}

    protected void applyDamage(PlayerEntity player, ICultivation culti, double dmg) {
        double oldHP = culti.getHP();
        culti.setHP(Math.max(0, culti.getHP() - dmg));

        // this is for vanilla statistics i guess!
		player.getCombatTracker().trackDamage(DamageSource.GENERIC, (float)oldHP, (float)dmg);
		player.addStat(Stats.DAMAGE_TAKEN, (int)dmg);
		player.addExhaustion((float)dmg);
		//
		if (culti.getHP() <= 0) {
			player.setHealth(-1); //it really kills a player
		}
    }
    
}
