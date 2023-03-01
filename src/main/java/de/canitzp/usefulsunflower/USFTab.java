package de.canitzp.usefulsunflower;

import de.canitzp.usefulsunflower.item.SeedPouchItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class USFTab extends CreativeModeTab {

    public static final USFTab INSTANCE = new USFTab();

    public USFTab() {
        super(UsefulSunflower.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(USFRegistry.USFItems.SEED_POUCH.get());
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> stacks) {
        ItemStack infiniteSeedPouch = USFRegistry.USFItems.SEED_POUCH.get().getDefaultInstance();
        SeedPouchItem.setInfinite(infiniteSeedPouch);
        stacks.add(infiniteSeedPouch);

        super.fillItemList(stacks);
    }
}
