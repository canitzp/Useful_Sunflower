package de.canitzp.usefulsunflower.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class SimpleSeedContainer implements ISeedContainer, INBTSerializable<CompoundTag> {

    private int capacity;
    private int stored;

    public SimpleSeedContainer(int capacity, int stored) {
        this.capacity = capacity;
        this.stored = stored;
    }

    public SimpleSeedContainer(int capacity) {
        this(capacity, 0);
    }

    @Override
    public int getSeedContainerSize() {
        return this.capacity;
    }

    @Override
    public int getSeedsInsideContainer() {
        return this.stored;
    }

    @Override
    public void setSeedsInsideContainer(int amount) {
        this.stored = amount;
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(NBT_STORED_SEEDS_KEY, this.stored);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.stored = nbt.getInt(NBT_STORED_SEEDS_KEY);
    }
}
