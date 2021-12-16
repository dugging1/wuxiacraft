package wuxiacraft;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import wuxiacraft.init.WuxiaItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroupCollection {
    
    public static final ItemGroup TECHNIQUE_GROUP = new ItemGroup("techniqueItemGroup") {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack createIcon() {
            return new ItemStack(() -> WuxiaItems.TECHNIQUE_SCROLLS.get(0).get());
        }
    };

    public static final ItemGroup RECOVERY_STONES = new ItemGroup("recoveryStonesItemGroup") {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack createIcon() {
            return new ItemStack(() -> WuxiaItems.RECOVER_STONES.get(0).get());
        }
    };
}
