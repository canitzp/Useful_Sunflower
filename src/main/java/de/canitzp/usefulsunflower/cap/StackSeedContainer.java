package de.canitzp.usefulsunflower.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StackSeedContainer implements ICapabilityProvider, ISeedContainer {

    private final ItemStack stack;
    private final int seedCapacity;

    public StackSeedContainer(ItemStack stack, int seedCapacity) {
        this.stack = stack;
        this.seedCapacity = seedCapacity;
    }

    private CompoundTag tag(){
        return this.stack.getOrCreateTagElement(NBT_ROOT_KEY);
    }

    @Override
    public int getSeedContainerSize() {
        return this.seedCapacity;
    }

    @Override
    public int getSeedsInsideContainer() {
        return this.tag().getInt(NBT_STORED_SEEDS_KEY);
    }

    @Override
    public void setSeedsInsideContainer(int amount) {
        this.tag().putInt(NBT_STORED_SEEDS_KEY, amount);
    }

    @Override
    public int takeSeedsFromContainer(int amount, boolean simulate) {
        if (!canTake()) {
            return 0;
        }

        int seedsExtracted = Math.min(this.getSeedsInsideContainer(), amount);
        if (!simulate) {
            this.setSeedsInsideContainer(this.getSeedsInsideContainer() - seedsExtracted);
        }
        return seedsExtracted;
    }

    @Override
    public int putSeedsIntoContainer(int amount, boolean simulate) {
        if (!canPut()) {
            return 0;
        }
        int seedsReceived = Math.min(this.getSeedContainerSize(), this.getSeedContainerSize() - this.getSeedsInsideContainer());
        if (!simulate){
            this.setSeedsInsideContainer(this.getSeedsInsideContainer() + seedsReceived);
        }
        return seedsReceived;
    }

    @Override
    public boolean canTake() {
        return true;
    }

    @Override
    public boolean canPut() {
        return true;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CapabilitySeedContainer.SEED_CONTAINER.orEmpty(cap, LazyOptional.of(() -> this));
    }
}
