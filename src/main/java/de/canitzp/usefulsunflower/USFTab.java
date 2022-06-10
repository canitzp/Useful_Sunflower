package de.canitzp.usefulsunflower;

import de.canitzp.usefulsunflower.item.ItemSeedPouch;
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


}
