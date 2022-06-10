package de.canitzp.usefulsunflower.block;

import de.canitzp.usefulsunflower.cap.CapabilitySeedContainer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerSqueezer extends SimpleContainer implements WorldlyContainer {

    public ContainerSqueezer() {
        super(3);
    }

    @Override
    public boolean canPlaceItem(int slotId, ItemStack stack) {
        return switch (slotId) {
            case TileSqueezer.SLOT_INPUT_SEED_CONTAINER -> stack.getCapability(CapabilitySeedContainer.SEED_CONTAINER).isPresent();
            case TileSqueezer.SLOT_INPUT_INGREDIENT -> true;
            case TileSqueezer.SLOT_OUTPUT_RESULT -> false;
            default -> super.canPlaceItem(slotId, stack);
        };
    }

    @Override
    public int @NotNull [] getSlotsForFace(@Nullable Direction side) {
        return new int[]{TileSqueezer.SLOT_INPUT_SEED_CONTAINER, TileSqueezer.SLOT_INPUT_INGREDIENT, TileSqueezer.SLOT_OUTPUT_RESULT};
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotId, @NotNull ItemStack stack, @Nullable Direction side) {
        return this.canPlaceItem(slotId, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slotId, @NotNull ItemStack stack, @NotNull Direction side) {
        return slotId == TileSqueezer.SLOT_OUTPUT_RESULT && side == Direction.DOWN;
    }

    @Override
    public void fromTag(@NotNull ListTag listTag) {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.setItem(i, ItemStack.EMPTY);
        }

        for(int k = 0; k < listTag.size(); ++k) {
            CompoundTag compoundtag = listTag.getCompound(k);
            int j = compoundtag.getInt("Slot");
            if (j >= 0 && j < this.getContainerSize()) {
                this.setItem(j, ItemStack.of(compoundtag));
            }
        }

    }

    @Override
    public @NotNull ListTag createTag() {
        ListTag listTag = new ListTag();

        for(int i = 0; i < this.getContainerSize(); ++i) {
            ItemStack itemstack = this.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                itemstack.save(compoundtag);
                listTag.add(compoundtag);
            }
        }

        return listTag;
    }
}
