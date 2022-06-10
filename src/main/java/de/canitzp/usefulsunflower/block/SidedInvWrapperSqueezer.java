package de.canitzp.usefulsunflower.block;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SidedInvWrapperSqueezer extends SidedInvWrapper {

    private final TileSqueezer squeezer;

    public SidedInvWrapperSqueezer(TileSqueezer squeezer, WorldlyContainer inv, @Nullable Direction side) {
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

    public static SidedInvWrapperSqueezer[] createForAllSides(TileSqueezer squeezer, WorldlyContainer inv){
        SidedInvWrapperSqueezer[] ary = new SidedInvWrapperSqueezer[Direction.values().length];
        for (Direction side : Direction.values()) {
            ary[side.ordinal()] = new SidedInvWrapperSqueezer(squeezer, inv, side);
        }
        return ary;
    }
}
