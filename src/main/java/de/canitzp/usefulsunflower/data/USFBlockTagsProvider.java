package de.canitzp.usefulsunflower.data;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.UsefulSunflower;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;


public class USFBlockTagsProvider extends BlockTagsProvider {

    public USFBlockTagsProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, UsefulSunflower.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(USFRegistry.USFBlocks.SQUEEZER.get());

        this.tag(BlockTags.FLOWERS).add(USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get());
    }
}
