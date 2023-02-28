package de.canitzp.usefulsunflower.data;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.canitzp.usefulsunflower.USFRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class USFLootTableProvider extends LootTableProvider {

    public USFLootTableProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return Lists.newArrayList(Pair.of(USFBlockLoot::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationContext) {
        map.forEach((id, table) -> LootTables.validate(validationContext, id, table));
    }

    public static class USFBlockLoot extends BlockLoot {
        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            consumer.accept(USFRegistry.USFBlocks.SQUEEZER.get().getLootTable(), createSingleItemTable(USFRegistry.USFBlocks.SQUEEZER.get()));
        }
    }
}
