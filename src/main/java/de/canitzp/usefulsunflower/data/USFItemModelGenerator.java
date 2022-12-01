package de.canitzp.usefulsunflower.data;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.UsefulSunflower;
import de.canitzp.usefulsunflower.block.BlockSqueezer;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class USFItemModelGenerator extends ItemModelProvider {

    public USFItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, UsefulSunflower.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(USFRegistry.USFItems.SUNFLOWER_STEM.get());
        singleTexture(USFRegistry.USFItems.SUNFLOWER_HEAD_EMPTY.get());
        singleTexture(USFRegistry.USFItems.SUNFLOWER_OIL.get());
        singleTexture(USFRegistry.USFItems.ROASTED_SUNFLOWER_SEEDS.get());
        singleTexture(USFRegistry.USFItems.SUNFLOWER_FLOUR.get());
        singleTexture(USFRegistry.USFItems.SUNFLOWER_BREAD.get());
        singleTexture(USFRegistry.USFItems.MUG.get());
        singleTexture(USFRegistry.USFItems.MUG_WITH_COFFEE_POWDER.get());
        singleTexture(USFRegistry.USFItems.MUG_WITH_COLD_COFFEE.get());
        singleTexture(USFRegistry.USFItems.MUG_WITH_HOT_COFFEE.get());

        this.withExistingParent("seed_pouch", "item/generated").override()
                .predicate(new ResourceLocation(UsefulSunflower.MODID, "state"), 0)
                .model(this.withExistingParent("seed_pouch_empty", "item/generated")
                        .texture("layer0", modLoc("items/seed_pouch_empty")))
                .end()
                .override()
                .predicate(new ResourceLocation(UsefulSunflower.MODID, "state"), 1)
                .model(this.withExistingParent("seed_pouch_full", "item/generated")
                        .texture("layer0", modLoc("items/seed_pouch_full")))
                .end();

        block(USFRegistry.USFBlocks.SQUEEZER.get());
    }

    private void singleTexture(Item item){
        singleTexture(item.getRegistryName().getPath(), mcLoc("item/handheld"), "layer0", modLoc("items/" + item.getRegistryName().getPath()));
    }

    private void block(Block block){
        this.withExistingParent(block.getRegistryName().getPath(), new ResourceLocation(UsefulSunflower.MODID, "block/" + block.getRegistryName().getPath()));
    }
}
