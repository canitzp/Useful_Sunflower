package de.canitzp.usefulsunflower.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.usefulsunflower.UsefulSunflower;
import de.canitzp.usefulsunflower.block.entity.SqueezerBlockEntity;
import de.canitzp.usefulsunflower.cap.CapabilitySeedContainer;
import de.canitzp.usefulsunflower.cap.ISeedContainer;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class OverlayRenderer {

    public static final ResourceLocation TEXTURE = new ResourceLocation(UsefulSunflower.MODID, "textures/overlay.png");
    public static final int SQUEEZER_TEXTURE_WIDTH = 146;
    public static final int SQUEEZER_TEXTURE_HEIGHT = 38;

    public static void renderOverlay(BlockEntity tile, PoseStack matrix){
        if(tile == null){
            return;
        }
        if(tile instanceof SqueezerBlockEntity){
            OverlayRenderer.renderSqueezer(((SqueezerBlockEntity) tile), matrix);
        } else {
            tile.getCapability(CapabilitySeedContainer.SEED_CONTAINER).ifPresent(seedContainer -> {
                OverlayRenderer.renderSeedContainer(seedContainer, matrix);
            });
        }
    }

    private static void renderSqueezer(SqueezerBlockEntity tile, PoseStack matrix){
        matrix.pushPose();
        RenderSystem.enableBlend();

        SqueezerRecipe recipe = tile.getLoadedRecipe();
        float xPos = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2F + 2F;
        float yPos = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2F + 2F;

        // general background
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, .5F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        Screen.blit(matrix, (int) xPos, (int) yPos, 0, 0, 0, SQUEEZER_TEXTURE_WIDTH, SQUEEZER_TEXTURE_HEIGHT, 256, 256);

        // dynamic arrow
        int arrowWidth = Math.round(99 * (tile.clicksUntilConversion / 9F));
        Screen.blit(matrix, (int) (xPos + 24), (int) yPos + 25, 0, 99 - arrowWidth, SQUEEZER_TEXTURE_HEIGHT, arrowWidth, 8, 256, 256);

        // display remaining seeds
        Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("block.usefulsunflower.squeezer.overlay.seeds_remaining", tile.seedContainer.getSeedsInsideContainer()), xPos + 5, yPos + 5, 0x80000000);

        // display ingredient
        if(!tile.inv.getItem(SqueezerBlockEntity.SLOT_INPUT_INGREDIENT).isEmpty()){
            Minecraft.getInstance().getItemRenderer().renderGuiItem(tile.inv.getItem(SqueezerBlockEntity.SLOT_INPUT_INGREDIENT), (int) xPos + 6, (int) yPos + 16);
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font , tile.inv.getItem(SqueezerBlockEntity.SLOT_INPUT_INGREDIENT), (int) xPos + 6, (int) yPos + 16);
        }

        // currently creating and needed seeds
        if(recipe != null){
            Minecraft.getInstance().font.draw(matrix, "Usage: " + recipe.seedsNecessary(), xPos + 24, yPos + 16, 0x80000000);

            Minecraft.getInstance().getItemRenderer().renderGuiItem(recipe.getResultItem(), (int) xPos + 125, (int) yPos + 16);
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font , recipe.getResultItem(), (int) xPos + 125, (int) yPos + 16);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrix.popPose();
    }

    public static void renderSeedContainer(ISeedContainer seedContainer, PoseStack matrix){
        matrix.pushPose();
        RenderSystem.enableBlend();

        float xPos = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2F + 2F;
        float yPos = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2F + 2F;

        Minecraft.getInstance().font.draw(matrix, new TranslatableComponent("overlay.usefulsunflower.seed_container_stored_seeds", seedContainer.getSeedsInsideContainer(), seedContainer.getSeedContainerSize()), xPos + 1, yPos + 1, 0x80FFFFFF);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        matrix.popPose();
    }

}
