package de.canitzp.usefulsunflower.block;

import de.canitzp.usefulsunflower.USFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockGrowingSunflower extends BushBlock implements BonemealableBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final double[] SIZE_BY_AGE = new double[]{1D, 5D, 9D, 13D};

    public BlockGrowingSunflower() {
        super(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
        this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
        Vec3 offset = state.getOffset(level, pos);
        return Block.box( 8D + (offset.x * 16D - 4D), 0D, 8D + (offset.z * 16D - 4D), 8D + (offset.x * 16D + 4D), SIZE_BY_AGE[state.getValue(AGE)], 8D + (offset.z * 16D + 4D));
    }

    @Override
    public boolean mayPlaceOn(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.FARMLAND);
    }

    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull Random rnd) {
        if (!level.isAreaLoaded(pos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (level.getRawBrightness(pos, 0) >= 9) {
            if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, rnd.nextInt((int)(25.0F) + 1) == 0)) {
                int i = state.getValue(AGE);
                if (i < 3) {
                    level.setBlock(pos, state.setValue(AGE, i + 1), 2);
                } else {
                    this.growUp(level, pos);
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
            }
        }
    }

    public void growUp(@NotNull ServerLevel level, @NotNull BlockPos pos){
        if(level.getBlockState(pos.above()).isAir()){
            DoublePlantBlock.placeAt(level, Blocks.SUNFLOWER.defaultBlockState(), pos, 3);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return Items.SUNFLOWER.getDefaultInstance();
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state, boolean p_50900_) {
        return state.getValue(AGE) <= 3;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull Random rnd, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull Random rnd, @NotNull BlockPos pos, BlockState state) {
        int i = Math.min(3, state.getValue(AGE) + Mth.nextInt(rnd, 1, 2));
        BlockState blockstate = state.setValue(AGE, i);
        level.setBlock(pos, blockstate, 2);
        if (i == 3) {
            this.growUp(level, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.CROP;
    }

    @Override
    public @NotNull OffsetType getOffsetType() {
        return Blocks.SUNFLOWER.getOffsetType();
    }

    public boolean placeAt(UseOnContext context, Level level, BlockPos pos, Direction facing){
        BlockPos replacementPosition;
        if (level.getBlockState(pos).canBeReplaced(new BlockPlaceContext(context))) {
            replacementPosition = pos;
        } else {
            replacementPosition = pos.relative(facing);
        }
        BlockState stateToReplace = level.getBlockState(replacementPosition);
        if(stateToReplace.is(this)){
            return false;
        }
        if(!stateToReplace.canBeReplaced(new BlockPlaceContext(context))){
            return false;
        }
        if(!this.mayPlaceOn(level.getBlockState(replacementPosition.below()), level, replacementPosition.below())){
            return false;
        }
        level.setBlockAndUpdate(replacementPosition, USFRegistry.USFBlocks.GROWING_SUNFLOWER_STEM.get().defaultBlockState());
        return true;
    }

}
