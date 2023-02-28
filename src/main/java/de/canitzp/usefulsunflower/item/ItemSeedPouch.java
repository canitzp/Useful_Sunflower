package de.canitzp.usefulsunflower.item;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.USFTab;
import de.canitzp.usefulsunflower.block.BlockGrowingSunflower;
import de.canitzp.usefulsunflower.cap.CapabilitySeedContainer;
import de.canitzp.usefulsunflower.cap.ISeedContainer;
import de.canitzp.usefulsunflower.cap.SimpleSeedContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ItemSeedPouch extends Item {

    public static final int SEED_CAPACITY = 100_000;

    public static int getStoredSeeds(ItemStack stack){
        return stack.getCapability(CapabilitySeedContainer.SEED_CONTAINER).map(ISeedContainer::getSeedsInsideContainer).orElse(0);
    }

    public static void setStoredSeeds(ItemStack stack, int seeds){
        stack.getCapability(CapabilitySeedContainer.SEED_CONTAINER).ifPresent(seedContainer -> seedContainer.setSeedsInsideContainer(seeds));
    }

    public static boolean isInfinite(ItemStack stack){
        return stack.hasTag() && stack.getTag().getBoolean("seed_pouch_infinite");
    }

    public static void setInfinite(ItemStack stack){
        stack.getOrCreateTag().putBoolean("seed_pouch_infinite", true);
    }

    public static void removeInfinite(ItemStack stack){
        if(stack.hasTag()){
            stack.getTag().remove("seed_pouch_infinite");
        }
    }

    public ItemSeedPouch() {
        super(new Properties().stacksTo(1).tab(USFTab.INSTANCE));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> text, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, text, flag);

        text.add(new TranslatableComponent("item.usefulsunflower.seed_pouch.desc.general").withStyle(ChatFormatting.GRAY));
        text.add(new TranslatableComponent("item.usefulsunflower.seed_pouch.desc.seeds_contained", getStoredSeeds(stack)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return ItemSeedPouch.isInfinite(stack) || super.isFoil(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().isClientSide()){
            return InteractionResult.SUCCESS;
        }
        if (context.getPlayer() == null) {
            return super.useOn(context);
        }
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
        return super.useOn(context);
    }

    private void onPlantClick(UseOnContext context, int storedSeeds) {
        if (storedSeeds >= SEED_CAPACITY) {
            context.getPlayer().sendMessage(new TextComponent("Seed Pouch is full!"), context.getPlayer().getUUID());
            return;
        }

        setStoredSeeds(context.getItemInHand(), storedSeeds + Mth.nextInt(context.getLevel().random, 100, 2000));
        context.getLevel().removeBlock(context.getClickedPos(), false);
        if (context.getPlayer().isCrouching()) {
            context.getLevel().removeBlock(context.getClickedPos().below(), false);
            Block.popResource(context.getLevel(), context.getClickedPos(), USFRegistry.USFItems.SUNFLOWER_HEAD_EMPTY.get().getDefaultInstance());
            Block.popResource(context.getLevel(), context.getClickedPos(), USFRegistry.USFItems.SUNFLOWER_STEM.get().getDefaultInstance());
        } else {
            int sunflowerAge = context.getPlayer().isCrouching() ? 0 : Mth.nextInt(new Random(), 2, 3);
            context.getLevel().setBlockAndUpdate(context.getClickedPos().below(), USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get().defaultBlockState().setValue(BlockGrowingSunflower.AGE, sunflowerAge));
        }
    }

    @Override
    public boolean canAttackBlock(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        return !state.is(Blocks.SUNFLOWER);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        SimpleSeedContainer seedContainer = new SimpleSeedContainer(SEED_CAPACITY);
        return new ICapabilitySerializable<CompoundTag>() {

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag subNbt = new CompoundTag();
                subNbt.put(ISeedContainer.NBT_ROOT_KEY, seedContainer.serializeNBT());
                return subNbt;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                seedContainer.deserializeNBT(nbt.getCompound(ISeedContainer.NBT_ROOT_KEY));
            }

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return CapabilitySeedContainer.SEED_CONTAINER.orEmpty(cap, LazyOptional.of(() -> seedContainer));
            }
        };
    }
}
