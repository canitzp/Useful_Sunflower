package de.canitzp.usefulsunflower.item;

import de.canitzp.usefulsunflower.USFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ItemMugWithCoffee extends Item {

    private final State state;

    public ItemMugWithCoffee(State state, Properties properties) {
        super(properties);
        this.state = state;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if(level.isClientSide()){
            return;
        }

        // make cold coffee hot in very hot dimensions (eg: nether)
        if(this.state != State.COLD){
            return;
        }
        if(entity instanceof Player player){
            if(player.getLevel().dimensionType().ultraWarm()){
                if(stack.getCount() > 1){
                    stack.shrink(1);
                    player.addItem(USFRegistry.USFItems.MUG_WITH_HOT_COFFEE.get().getDefaultInstance());
                } else {
                    player.getInventory().setItem(slotId, USFRegistry.USFItems.MUG_WITH_HOT_COFFEE.get().getDefaultInstance());
                }
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if(this.state != State.POWDER){
            return super.use(level, player, hand);
        }

        ItemStack heldStack = player.getItemInHand(hand);
        if(level.isClientSide()){
            return InteractionResultHolder.success(heldStack);
        }

        BlockHitResult playerPOVHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.WATER);
        if(playerPOVHitResult.getType() != HitResult.Type.BLOCK){
            return super.use(level, player, hand);
        }

        BlockPos hittedBlockPos = playerPOVHitResult.getBlockPos();
        if(!player.mayInteract(level, hittedBlockPos)){
            return super.use(level, player, hand);
        }
        if(!level.getFluidState(hittedBlockPos).is(FluidTags.WATER)){
            return super.use(level, player, hand);
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, hittedBlockPos);

        ItemStack filledMugStack = USFRegistry.USFItems.MUG_WITH_COLD_COFFEE.get().getDefaultInstance();
        if(heldStack.getCount() > 1){
            heldStack.shrink(1);
            player.addItem(filledMugStack);
        }
        return InteractionResultHolder.consume(heldStack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack stackAfterEating = super.finishUsingItem(stack, level, entity);
        if(this.state != State.POWDER){
            if(entity instanceof Player player){
                if(!player.getAbilities().instabuild){
                    player.addItem(USFRegistry.USFItems.MUG.get().getDefaultInstance());
                }
            }
        }
        return stackAfterEating;
    }

    public enum State {
        POWDER,
        COLD,
        HOT
    }
}
