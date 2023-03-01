package de.canitzp.usefulsunflower.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.client.OverlayRenderer;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class JEISqueezerRecipeCategory implements IRecipeCategory<SqueezerRecipe> {

    private final IGuiHelper guiHelper;
    private final Component localizedName = new TranslatableComponent("jei.usefulsunflower.category.squeezer");
    private final IDrawable background;
    private final IDrawable icon;

    public JEISqueezerRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.background = guiHelper.createDrawable(OverlayRenderer.TEXTURE, 4, 4, 138, 29);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, USFRegistry.USFBlockItems.SQUEEZER.get().getDefaultInstance());
    }

    @Override
    public RecipeType<SqueezerRecipe> getRecipeType() {
        return USFJEIPlugin.SQUEEZER;
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
    public void setRecipe(IRecipeLayoutBuilder builder, SqueezerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 2, 12).addItemStack(recipe.ingredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 121, 12).addItemStack(recipe.result());
    }

    @Override
    public void draw(SqueezerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        font.draw(stack, new TranslatableComponent("Usage: ").append(new TranslatableComponent(String.valueOf(recipe.seedsNecessary()))), 0, 0, 0xFF808080);
    }

    @Deprecated
    @Override
    public ResourceLocation getUid() {
        return null;
    }

    @Deprecated
    @Override
    public Class<? extends SqueezerRecipe> getRecipeClass() {
        return null;
    }

}
