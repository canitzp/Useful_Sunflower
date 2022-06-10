package de.canitzp.usefulsunflower.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

    @Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
    public void getShapeMixin(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir){
        if(state.is(Blocks.SUNFLOWER)){
            double offsetOffset = 4D;
            double height = state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER ? 16D : 12D;
            Vec3 offset = state.getOffset(level, pos);
            cir.setReturnValue(Block.box( 8D + (offset.x * 16D - offsetOffset), 0D, 8D + (offset.z * 16D - offsetOffset), 8D + (offset.x * 16D + offsetOffset), height, 8D + (offset.z * 16D + offsetOffset)));
        }
    }

}
