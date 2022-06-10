package de.canitzp.usefulsunflower.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.UsefulSunflower;
import de.canitzp.usefulsunflower.block.BlockSqueezer;
import de.canitzp.usefulsunflower.block.TileOverlay;
import de.canitzp.usefulsunflower.block.TileSqueezer;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class JEISqueezerRecipeCategory implements IRecipeCategory<SqueezerRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(UsefulSunflower.MODID, "squeezer");
    private final IGuiHelper guiHelper;
    private final Component localizedName = new TranslatableComponent("jei.usefulsunflower.category.squeezer");
    private final IDrawable background;
    private final IDrawable icon;

    public JEISqueezerRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(TileOverlay.TEXTURE, 4, 4, 138, 29);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, USFRegistry.USFBlockItems.SQUEEZER.get().getDefaultInstance());
    }

    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull Class<? extends SqueezerRecipe> getRecipeClass() {
        return SqueezerRecipe.class;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(@NotNull SqueezerRecipe recipe, @NotNull IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, recipe.ingredient());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, @NotNull SqueezerRecipe recipe, @NotNull IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(TileSqueezer.SLOT_INPUT_INGREDIENT, true, 1, 11);
        guiItemStacks.init(TileSqueezer.SLOT_OUTPUT_RESULT, false, 120, 11);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(SqueezerRecipe recipe, PoseStack stack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        font.draw(stack, new TranslatableComponent("Usage: ").append(new TranslatableComponent(String.valueOf(recipe.seedsNecessary()))), 0, 0, 0xFF808080);
    }
}
