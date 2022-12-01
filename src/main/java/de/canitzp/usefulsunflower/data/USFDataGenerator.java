package de.canitzp.usefulsunflower.data;

import de.canitzp.usefulsunflower.UsefulSunflower;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = UsefulSunflower.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class USFDataGenerator {

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeClient()) {
            generator.addProvider(new USFBlockstateProvider(generator, helper));
            generator.addProvider(new USFItemModelGenerator(generator, helper));
        }
        if(event.includeServer()){
            generator.addProvider(new USFRecipeProvider(generator));
            USFBlockTagsProvider usfblocktagsprovider = new USFBlockTagsProvider(generator, helper);
            generator.addProvider(usfblocktagsprovider);
            generator.addProvider(new USFItemTagsProvider(generator, usfblocktagsprovider, helper));
            generator.addProvider(new USFLootTableProvider(generator));
        }
    }

}
