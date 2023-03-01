package de.canitzp.usefulsunflower;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.canitzp.usefulsunflower.client.OverlayRenderer;
import de.canitzp.usefulsunflower.cap.CapabilitySeedContainer;
import de.canitzp.usefulsunflower.item.SeedPouchItem;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipeBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@Mod(UsefulSunflower.MODID)
public class UsefulSunflower {

    public static final String MODID = "usefulsunflower";

    private static final Logger LOGGER = LogManager.getLogger(UsefulSunflower.MODID);

    public UsefulSunflower(){
        LOGGER.info("[Useful Sunflower] Loading...");

        LOGGER.info("[Useful Sunflower] Registering capabilities...");
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(CapabilitySeedContainer::register);

        LOGGER.info("[Useful Sunflower] Registering items...");
        USFRegistry.USFItems.ITEMS.register(modEventBus);

        LOGGER.info("[Useful Sunflower] Registering blocks...");
        USFRegistry.USFBlocks.BLOCKS.register(modEventBus);

        LOGGER.info("[Useful Sunflower] Registering block items...");
        USFRegistry.USFBlockItems.BLOCK_ITEMS.register(modEventBus);

        LOGGER.info("[Useful Sunflower] Registering block entities...");
        USFRegistry.USFBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);

        LOGGER.info("[Useful Sunflower] Registering recipe serializers...");
        ForgeRegistries.RECIPE_SERIALIZERS.register(SqueezerRecipeBuilder.Serializer.INSTANCE);

        LOGGER.info("[Useful Sunflower] Loading complete.");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event){
            ItemBlockRenderTypes.setRenderLayer(USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get(), RenderType.cutoutMipped());

            ItemProperties.register(USFRegistry.USFItems.SEED_POUCH.get(), new ResourceLocation(MODID, "state"), (stack, level, entity, i) -> {
                if(SeedPouchItem.getStoredSeeds(stack) > 0){
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void renderGameOverlay(RenderGameOverlayEvent.Post event){
            if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT){
                HitResult hitResult = Minecraft.getInstance().hitResult;
                if(hitResult instanceof BlockHitResult blockHitResult){
                    OverlayRenderer.renderOverlay(Minecraft.getInstance().level.getBlockEntity(blockHitResult.getBlockPos()), event.getMatrixStack());
                }
            }
        }
    }

    public static JsonElement stackToJson(@Nonnull ItemStack stack){
        JsonObject resultJson = new JsonObject();
        resultJson.addProperty("item", stack.getItem().getRegistryName().toString());
        if (stack.getCount() != 1) {
            resultJson.addProperty("count", stack.getCount());
        }
        if (stack.getTag() != null) {
            resultJson.addProperty("nbt", stack.getTag().toString());
        }
        return resultJson;
    }

}
