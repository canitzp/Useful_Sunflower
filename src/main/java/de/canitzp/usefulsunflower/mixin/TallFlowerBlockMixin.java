package de.canitzp.usefulsunflower.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TallFlowerBlock.class)
public class TallFlowerBlockMixin {

    @Inject(method = "isValidBonemealTarget", at = @At("HEAD"), cancellable = true)
    private void isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean p_57306_, CallbackInfoReturnable<Boolean> cir){
        if(state.is(Blocks.SUNFLOWER)){
            cir.setReturnValue(false);
        }
    }

}
