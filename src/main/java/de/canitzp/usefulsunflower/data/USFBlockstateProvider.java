package de.canitzp.usefulsunflower.data;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.UsefulSunflower;
import de.canitzp.usefulsunflower.block.GrowingSunflowerBlock;
import de.canitzp.usefulsunflower.block.SqueezerBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class USFBlockstateProvider extends BlockStateProvider {

    public USFBlockstateProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, UsefulSunflower.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        super.getVariantBuilder(USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get())
                .partialState().with(GrowingSunflowerBlock.AGE, 0).addModels(new ConfiguredModel(stem("growing_sunflower_stem0", 0, mcLoc("block/sunflower_bottom"))))
                .partialState().with(GrowingSunflowerBlock.AGE, 1).addModels(new ConfiguredModel(stem("growing_sunflower_stem1", 2, mcLoc("block/sunflower_bottom"))))
                .partialState().with(GrowingSunflowerBlock.AGE, 2).addModels(new ConfiguredModel(stem("growing_sunflower_stem2", 4, mcLoc("block/sunflower_bottom"))))
                .partialState().with(GrowingSunflowerBlock.AGE, 3).addModels(new ConfiguredModel(stem("growing_sunflower_stem3", 6, mcLoc("block/sunflower_bottom"))));

        super.getVariantBuilder(USFRegistry.USFBlocks.SQUEEZER.get())
                .partialState().with(SqueezerBlock.CYCLE, 0).addModels(new ConfiguredModel(cubeBottomTop("squeezer", 0)))
                .partialState().with(SqueezerBlock.CYCLE, 1).addModels(new ConfiguredModel(cubeBottomTop("squeezer", 1)))
                .partialState().with(SqueezerBlock.CYCLE, 2).addModels(new ConfiguredModel(cubeBottomTop("squeezer", 2)));
    }

    public BlockModelBuilder stem(String name, int stemGrowthLevel, ResourceLocation crop) {
        return models().singleTexture(name, mcLoc(ModelProvider.BLOCK_FOLDER + "/stem_growth" + stemGrowthLevel), "stem", crop);
    }

    public BlockModelBuilder cubeBottomTop(String name, int cycle){
        String c = cycle == 0 ? "" : "_cycle" + cycle;
        return models().cubeBottomTop(name + c,
                new ResourceLocation(UsefulSunflower.MODID, "blocks/" + name + c),
                new ResourceLocation(UsefulSunflower.MODID, "blocks/" + name + "_bottom"),
                new ResourceLocation(UsefulSunflower.MODID, "blocks/" + name + "_top"));
    }
}
