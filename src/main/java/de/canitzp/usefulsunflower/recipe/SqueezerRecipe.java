package de.canitzp.usefulsunflower.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record SqueezerRecipe(ResourceLocation recipeId, String group, ItemStack result,
                             int seedsNecessary, ItemStack ingredient) implements Recipe<Container> {

    public static final RecipeType<SqueezerRecipe> TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return "squeezer";
        }
    };

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return this.result.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SqueezerRecipeBuilder.Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }
}
