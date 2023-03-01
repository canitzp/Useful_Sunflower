package de.canitzp.usefulsunflower;

import de.canitzp.usefulsunflower.block.*;
import de.canitzp.usefulsunflower.block.entity.SeedBinBlockEntity;
import de.canitzp.usefulsunflower.block.entity.SqueezerBlockEntity;
import de.canitzp.usefulsunflower.item.MugWithCoffeeItem;
import de.canitzp.usefulsunflower.item.SeedPouchItem;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static de.canitzp.usefulsunflower.UsefulSunflower.MODID;

public class USFRegistry {

    public static final FoodProperties FOOD_ROASTED_SUNFLOWER_SEEDS = new FoodProperties.Builder().nutrition(3).saturationMod(0.3F).build();
    public static final FoodProperties FOOD_COFFEE_COLD = new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build();
    public static final FoodProperties FOOD_COFFEE_HOT = new FoodProperties.Builder().nutrition(3).saturationMod(0.4F).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 90*20), 1F).build();

    public static class USFItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

        public static final RegistryObject<SeedPouchItem> SEED_POUCH = ITEMS.register("seed_pouch", SeedPouchItem::new);
        public static final RegistryObject<Item> SUNFLOWER_STEM = ITEMS.register("sunflower_stem", () -> new Item(new Item.Properties().tab(USFTab.INSTANCE)));
        public static final RegistryObject<Item> SUNFLOWER_HEAD_EMPTY = ITEMS.register("sunflower_head_empty", () -> new Item(new Item.Properties().tab(USFTab.INSTANCE)));
        public static final RegistryObject<Item> SUNFLOWER_OIL = ITEMS.register("sunflower_oil", () -> new Item(new Item.Properties().stacksTo(16).tab(USFTab.INSTANCE).craftRemainder(Items.GLASS_BOTTLE)));
        public static final RegistryObject<Item> ROASTED_SUNFLOWER_SEEDS = ITEMS.register("roasted_sunflower_seeds", () -> new Item(new Item.Properties().tab(USFTab.INSTANCE).food(USFRegistry.FOOD_ROASTED_SUNFLOWER_SEEDS)));
        public static final RegistryObject<Item> SUNFLOWER_FLOUR = ITEMS.register("sunflower_flour", () -> new Item(new Item.Properties().tab(USFTab.INSTANCE)));
        public static final RegistryObject<Item> SUNFLOWER_BREAD = ITEMS.register("sunflower_bread", () -> new Item(new Item.Properties().food(Foods.BREAD).tab(USFTab.INSTANCE)));
        public static final RegistryObject<Item> MUG = ITEMS.register("coffee_mug", () -> new Item(new Item.Properties().stacksTo(16).tab(USFTab.INSTANCE)));
        public static final RegistryObject<Item> MUG_WITH_COFFEE_POWDER = ITEMS.register("mug_coffee_powder", () -> new MugWithCoffeeItem(MugWithCoffeeItem.State.POWDER, new Item.Properties().stacksTo(16).tab(USFTab.INSTANCE)));
        public static final RegistryObject<Item> MUG_WITH_COLD_COFFEE = ITEMS.register("sunflower_coffee_cold", () -> new MugWithCoffeeItem(MugWithCoffeeItem.State.COLD, new Item.Properties().stacksTo(16).tab(USFTab.INSTANCE).food(FOOD_COFFEE_COLD)));
        public static final RegistryObject<Item> MUG_WITH_HOT_COFFEE = ITEMS.register("sunflower_coffee_hot", () -> new MugWithCoffeeItem(MugWithCoffeeItem.State.HOT, new Item.Properties().stacksTo(16).tab(USFTab.INSTANCE).food(FOOD_COFFEE_HOT)));
    }

    public static class USFBlockItems {
        public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

        public static final RegistryObject<BlockItem> SQUEEZER = BLOCK_ITEMS.register("squeezer", () -> new BlockItem(USFBlocks.SQUEEZER.get(), new Item.Properties().tab(USFTab.INSTANCE)));
        public static final RegistryObject<BlockItem> SEED_CONTAINER = BLOCK_ITEMS.register("seed_container", () -> new BlockItem(USFBlocks.SEED_CONTAINER.get(), new Item.Properties().tab(USFTab.INSTANCE)));
    }

    public static class USFBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

        public static final RegistryObject<GrowingSunflowerBlock> GROWING_SUNFLOWER_STEM = BLOCKS.register("growing_sunflower_stem", GrowingSunflowerBlock::new);
        public static final RegistryObject<SqueezerBlock> SQUEEZER = BLOCKS.register("squeezer", SqueezerBlock::new);

        public static final RegistryObject<SeedBinBlock> SEED_CONTAINER = BLOCKS.register("seed_container", SeedBinBlock::new);
    }

    public static class USFBlockEntityTypes {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

        public static final RegistryObject<BlockEntityType<SqueezerBlockEntity>> SQUEEZER = BLOCK_ENTITY_TYPES.register("squeezer", () -> BlockEntityType.Builder
                .of(SqueezerBlockEntity::new, USFBlocks.SQUEEZER.get())
                .build(Util.fetchChoiceType(References.BLOCK_ENTITY, "usefulsunflower:squeezer")));

        public static final RegistryObject<BlockEntityType<SeedBinBlockEntity>> SEED_CONTAINER = BLOCK_ENTITY_TYPES.register("seed_container", () -> BlockEntityType.Builder
                .of(SeedBinBlockEntity::new, USFBlocks.SEED_CONTAINER.get())
                .build(Util.fetchChoiceType(References.BLOCK_ENTITY, "usefulsunflower:seed_container")));
    }

}
