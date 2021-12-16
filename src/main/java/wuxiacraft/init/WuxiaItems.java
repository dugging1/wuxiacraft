package wuxiacraft.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import wuxiacraft.ItemGroupCollection;
import wuxiacraft.WuxiaCraft;
import wuxiacraft.cultivation.CultivationLevel;
import wuxiacraft.cultivation.technique.Technique;
import wuxiacraft.item.ResourceStoneItem;
import wuxiacraft.item.TechniqueScrollItem;

import java.util.LinkedList;
import java.util.List;

public class WuxiaItems {

	public static final DeferredRegister<Item> WUXIA_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WuxiaCraft.MOD_ID);

	public static final List<RegistryObject<Item>> TECHNIQUE_SCROLLS = new LinkedList<>();

	public static final List<RegistryObject<Item>> RECOVER_STONES = new LinkedList<>();


	public static void register() {
		WUXIA_ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		for (Technique tech : WuxiaTechniques.TECHNIQUES) {
			TECHNIQUE_SCROLLS.add(WUXIA_ITEMS.register(tech.getName() + "_item",
					() -> new TechniqueScrollItem(new Item.Properties().maxStackSize(1).group(ItemGroupCollection.TECHNIQUE_GROUP), tech)));
		}

		RECOVER_STONES.add(WUXIA_ITEMS.register("weak_life_stone",
			() -> new ResourceStoneItem(new Item.Properties().maxStackSize(64).group(ItemGroupCollection.RECOVERY_STONES), CultivationLevel.System.BODY, 10)));
	}
}
