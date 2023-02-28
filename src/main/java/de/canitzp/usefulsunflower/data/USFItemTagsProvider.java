package de.canitzp.usefulsunflower.data;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.UsefulSunflower;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;


public class USFItemTagsProvider extends ItemTagsProvider {


    public USFItemTagsProvider(DataGenerator gen, BlockTagsProvider blockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(gen, blockTagsProvider, UsefulSunflower.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(Tags.Items.DYES_GREEN).add(USFRegistry.USFItems.SUNFLOWER_HEAD_EMPTY.get());
        this.tag(Tags.Items.SLIMEBALLS).add(USFRegistry.USFItems.SUNFLOWER_OIL.get());
    }
}
