package de.canitzp.usefulsunflower.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqueezerSidedInvWrapper extends SidedInvWrapper {

    private final SqueezerBlockEntity squeezer;

    public SqueezerSidedInvWrapper(SqueezerBlockEntity squeezer, WorldlyContainer inv, @Nullable Direction side) {
        super(inv, side);
        this.squeezer = squeezer;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack itemStack = super.insertItem(slot, stack, simulate);
        if (this.squeezer.inputItem(false)) {
            this.squeezer.sync();
        }
        return itemStack;
    }

    public static SqueezerSidedInvWrapper[] createForAllSides(SqueezerBlockEntity squeezer, WorldlyContainer inv){
        SqueezerSidedInvWrapper[] ary = new SqueezerSidedInvWrapper[Direction.values().length];
        for (Direction side : Direction.values()) {
            ary[side.ordinal()] = new SqueezerSidedInvWrapper(squeezer, inv, side);
        }
        return ary;
    }
}
