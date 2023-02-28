package de.canitzp.usefulsunflower.data;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class USFRecipeProvider extends RecipeProvider {

    public USFRecipeProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(USFRegistry.USFItems.SEED_POUCH.get())
                .define('s', Items.STRING)
                .define('l', Items.LEATHER)
                .pattern("sss")
                .pattern("l l")
                .pattern(" l ")
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(USFRegistry.USFItems.MUG.get(), 2)
                .define('c', Items.CLAY_BALL)
                .pattern("   ")
                .pattern("c c")
                .pattern(" c ")
                .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(USFRegistry.USFBlocks.SQUEEZER.get())
                .define('c', Tags.Items.COBBLESTONE)
                .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('p', Blocks.PISTON)
                .pattern("ccc")
                .pattern("cic")
                .pattern("cpc")
                .unlockedBy("has_piston", has(Blocks.PISTON))
                .save(consumer);

        // sunflower flour to bread
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(USFRegistry.USFItems.SUNFLOWER_FLOUR.get()), USFRegistry.USFItems.SUNFLOWER_BREAD.get(), 0.15F, 200)
                .unlockedBy("has_sunflower_flour", has(USFRegistry.USFItems.SUNFLOWER_FLOUR.get()))
                .save(consumer);

        SqueezerRecipeBuilder.create(USFRegistry.USFItems.SUNFLOWER_OIL.get().getDefaultInstance(), 7_500, Items.GLASS_BOTTLE.getDefaultInstance()).save(consumer);
        SqueezerRecipeBuilder.create(USFRegistry.USFItems.MUG_WITH_COFFEE_POWDER.get().getDefaultInstance(), 10_000, USFRegistry.USFItems.MUG.get().getDefaultInstance()).save(consumer);
        SqueezerRecipeBuilder.create(USFRegistry.USFItems.ROASTED_SUNFLOWER_SEEDS.get().getDefaultInstance(), 2_000, Items.BOWL.getDefaultInstance()).save(consumer);
        SqueezerRecipeBuilder.create(USFRegistry.USFItems.SUNFLOWER_FLOUR.get().getDefaultInstance(), 10_000, new ItemStack(Items.PAPER, 3)).save(consumer);
        SqueezerRecipeBuilder.create(Items.MILK_BUCKET.getDefaultInstance(), 15_000, Items.WATER_BUCKET.getDefaultInstance());
    }
}
