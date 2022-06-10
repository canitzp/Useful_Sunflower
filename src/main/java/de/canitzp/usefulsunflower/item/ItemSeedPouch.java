package de.canitzp.usefulsunflower.item;

import de.canitzp.usefulsunflower.cap.ISeedContainer;
import de.canitzp.usefulsunflower.cap.StackSeedContainer;
import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.block.BlockGrowingSunflower;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ItemSeedPouch extends Item {

    public static final int SEED_CAPACITY = 100_000;

    public static int getStoredSeeds(ItemStack stack){
        return stack.getOrCreateTagElement(ISeedContainer.NBT_ROOT_KEY).getInt(ISeedContainer.NBT_STORED_SEEDS_KEY);
    }

    public static void setStoredSeeds(ItemStack stack, int seeds){
        stack.getOrCreateTagElement(ISeedContainer.NBT_ROOT_KEY).putInt(ISeedContainer.NBT_STORED_SEEDS_KEY, seeds);
    }

    public ItemSeedPouch() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> text, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, text, flag);

        text.add(new TranslatableComponent("item.usefulsunflower.seed_pouch.desc.general").withStyle(ChatFormatting.GRAY));
        text.add(new TranslatableComponent("item.usefulsunflower.seed_pouch.desc.seeds_contained", getStoredSeeds(stack)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().isClientSide()){
            return InteractionResult.SUCCESS;
        }
        if(context.getPlayer() != null){
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            int storedSeeds = ItemSeedPouch.getStoredSeeds(context.getItemInHand());
            if(state.is(Blocks.SUNFLOWER) && state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER){
                // plant to seeds anr optionally greenery
                if(context.getPlayer().mayInteract(context.getLevel(), context.getClickedPos()) && context.getPlayer().mayInteract(context.getLevel(), context.getClickedPos().below())){
                    this.onPlantClick(context, storedSeeds);
                    return InteractionResult.SUCCESS;
                }
            } else if(storedSeeds > 0 && USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get().placeAt(context, context.getLevel(), context.getClickedPos(), context.getClickedFace())){
                // seed pouch plant sunflower
                if(!context.getPlayer().isCreative()){
                    ItemSeedPouch.setStoredSeeds(context.getItemInHand(), storedSeeds - 1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }

    private void onPlantClick(UseOnContext context, int storedSeeds){
        if(storedSeeds < SEED_CAPACITY){
            setStoredSeeds(context.getItemInHand(), storedSeeds + Mth.nextInt(context.getLevel().random, 100, 2000));
            int sunflowerAge = context.getPlayer().isCrouching() ? 0 : Mth.nextInt(new Random(), 2, 3);
            context.getLevel().setBlockAndUpdate(context.getClickedPos().below(), USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get().defaultBlockState().setValue(BlockGrowingSunflower.AGE, sunflowerAge));
            context.getLevel().removeBlock(context.getClickedPos(), false);
            if(context.getPlayer().isCrouching()){
                Block.popResource(context.getLevel(), context.getClickedPos(), Items.APPLE.getDefaultInstance());
                Block.popResource(context.getLevel(), context.getClickedPos(), Items.SUNFLOWER.getDefaultInstance());
            }
        } else {
            context.getPlayer().sendMessage(new TextComponent("Seed Pouch is full!"), context.getPlayer().getUUID());
        }
    }

    @Override
    public boolean canAttackBlock(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        return !state.is(Blocks.SUNFLOWER);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new StackSeedContainer(stack, SEED_CAPACITY);
    }
}
