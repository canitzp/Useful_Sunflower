package de.canitzp.usefulsunflower.integration.jei;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.UsefulSunflower;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class USFJEIPlugin implements IModPlugin {

    public static final RecipeType<SqueezerRecipe> SQUEEZER = RecipeType.create(UsefulSunflower.MODID, "squeezer", SqueezerRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(UsefulSunflower.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new JEISqueezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<SqueezerRecipe> squeezerRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(SqueezerRecipe.TYPE);
        registration.addRecipes(SQUEEZER, squeezerRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(USFRegistry.USFBlockItems.SQUEEZER.get().getDefaultInstance(), SQUEEZER);
    }
}
