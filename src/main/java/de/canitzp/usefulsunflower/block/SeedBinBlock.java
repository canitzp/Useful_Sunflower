package de.canitzp.usefulsunflower.block;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.block.entity.SeedBinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeedBinBlock extends BaseEntityBlock {

    public SeedBinBlock() {
        super(Properties.of(Material.STONE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SeedBinBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, USFRegistry.USFBlockEntityTypes.SEED_CONTAINER.get(), SeedBinBlockEntity::serverTick);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if(level.isClientSide()){
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if(level.getBlockEntity(pos) instanceof SeedBinBlockEntity seedContainer){
            return seedContainer.onClick(player, hand, stack, state);
        }

        return InteractionResult.PASS;
    }
}
