package de.canitzp.usefulsunflower.recipe;

import com.google.gson.JsonObject;
import de.canitzp.usefulsunflower.UsefulSunflower;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SqueezerRecipeBuilder implements RecipeBuilder {
    private final ItemStack result;
    private final int seedsNecessary;
    private final ItemStack ingredient;

    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private @Nullable String group;

    public SqueezerRecipeBuilder(ItemStack result, int seedsNecessary, ItemStack ingredient) {
        this.result = result;
        this.seedsNecessary = seedsNecessary;
        this.ingredient = ingredient;
    }

    public static SqueezerRecipeBuilder create(ItemStack result, int seedsNecessary, ItemStack ingredient){
        return new SqueezerRecipeBuilder(result, seedsNecessary, ingredient);
    }

    @Override
    public @NotNull RecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance criterionTriggerInstance) {
        this.advancement.addCriterion(name, criterionTriggerInstance);
        return this;
    }

    @Override
    public @NotNull RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation recipeId) {
        // ensure validity
        this.advancement
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        consumer.accept(new SqueezerRecipeBuilder.Result(recipeId, this.result, this.seedsNecessary, this.ingredient, this.group == null ? "" : this.group, this.advancement, new ResourceLocation(recipeId.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + recipeId.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack result;
        private final int seedsNecessary;
        private final ItemStack ingredient;
        private final String group;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation id, ItemStack result, int seedsNecessary, ItemStack ingredient, String group, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.seedsNecessary = seedsNecessary;
            this.ingredient = ingredient;
            this.group = group;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }

            json.addProperty("seeds_necessary", this.seedsNecessary);

            json.add("ingredient", UsefulSunflower.stackToJson(this.ingredient));

            json.add("result", UsefulSunflower.stackToJson(this.result));
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>  implements RecipeSerializer<SqueezerRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        public Serializer() {
            this.setRegistryName(UsefulSunflower.MODID, "squeezer");
        }

        @Override
        public @NotNull SqueezerRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            int seedsNecessary = GsonHelper.getAsInt(json, "seeds_necessary", 10_000);
            ItemStack ingredient = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new SqueezerRecipe(recipeId, group, result, seedsNecessary, ingredient);
        }

        @Nullable
        @Override
        public SqueezerRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buf) {
            String group = buf.readUtf();
            int seedsNecessary = buf.readVarInt();
            ItemStack ingredient = buf.readItem();
            ItemStack result = buf.readItem();
            return new SqueezerRecipe(recipeId, group, result, seedsNecessary, ingredient);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull SqueezerRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeVarInt(recipe.seedsNecessary());
            buf.writeItem(recipe.ingredient());
            buf.writeItem(recipe.getResultItem());
        }
    }
}
