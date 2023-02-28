package de.canitzp.usefulsunflower.block;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.cap.CapabilitySeedContainer;
import de.canitzp.usefulsunflower.cap.ISeedContainer;
import de.canitzp.usefulsunflower.cap.SimpleSeedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TileSeedContainer extends BlockEntity {

    private boolean needsToSync = false;

    public TileSeedContainer(BlockPos pos, BlockState state) {
        super(USFRegistry.USFBlockEntityTypes.SEED_CONTAINER.get(), pos, state);
    }

    public SimpleSeedContainer seedContainer = new SimpleSeedContainer(4_000_000);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilitySeedContainer.SEED_CONTAINER){
            return CapabilitySeedContainer.SEED_CONTAINER.orEmpty(cap, LazyOptional.of(() -> this.seedContainer));
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.seedContainer.deserializeNBT(tag.getCompound(ISeedContainer.NBT_ROOT_KEY));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(ISeedContainer.NBT_ROOT_KEY, this.seedContainer.serializeNBT());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(){
        return this.saveWithFullMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
        this.load(pkt.getTag());
    }

    public void sync(){
        for (Player player : level.players()) {
            if(player instanceof ServerPlayer){
                if(player.distanceToSqr(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ() + 0.5D) <= 64 && !this.isRemoved() && level.getBlockEntity(this.getBlockPos()) == this){
                    ((ServerPlayer) player).connection.send(this.getUpdatePacket());
                }
            }
        }
    }

    public InteractionResult onClick(Player player, InteractionHand hand, ItemStack stack, BlockState state) {
        if(!stack.isEmpty()){
            stack.getCapability(CapabilitySeedContainer.SEED_CONTAINER).ifPresent(otherSeedContainer -> {
                if(otherSeedContainer.getSeedsInsideContainer() > 0){
                    // transfer all seeds from hold item to container (as long as container not full)
                    System.out.println("Into: " + otherSeedContainer.getSeedsInsideContainer());
                    otherSeedContainer.takeSeedsFromContainer(this.seedContainer.putSeedsIntoContainer(otherSeedContainer.takeSeedsFromContainer(Integer.MAX_VALUE, true), false), false);
                } else {
                    // transfer seeds from container to hold item
                    System.out.println("Outo: " + this.seedContainer.getSeedsInsideContainer());
                    this.seedContainer.takeSeedsFromContainer(otherSeedContainer.putSeedsIntoContainer(this.seedContainer.takeSeedsFromContainer(Integer.MAX_VALUE, true), false), false);
                }
                TileSeedContainer.this.sync();
            });
        }
        System.out.println("Content: " + this.seedContainer.getSeedsInsideContainer());
        return InteractionResult.SUCCESS;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, @NotNull TileSeedContainer tile) {
        if(tile.needsToSync && level.getGameTime() % 10 == 1){
            tile.sync();
            tile.needsToSync = false;
        }
        if(tile.seedContainer.getSeedsInsideContainer() <= 0){
            return; // Do nothing if I don't have any stored seeds
        }
        int seedsToPush = tile.seedContainer.getSeedsInsideContainer();
        List<ISeedContainer> foundSeedContainer = new ArrayList<>();
        for(Direction direction : Direction.values()){
            BlockPos positionToCheck = pos.relative(direction);
            BlockEntity blockEntityAtCheckPosition = level.getBlockEntity(positionToCheck);
            if(blockEntityAtCheckPosition == null){
                continue;
            }
            Optional<ISeedContainer> optionalSeedContainer = blockEntityAtCheckPosition.getCapability(CapabilitySeedContainer.SEED_CONTAINER, direction.getOpposite()).resolve();
            if(optionalSeedContainer.isEmpty()){
                continue;
            }
            ISeedContainer otherSeedContainer = optionalSeedContainer.get();
            if(!otherSeedContainer.canPut()){
                continue;
            }
            foundSeedContainer.add(otherSeedContainer);
        }

        if(foundSeedContainer.isEmpty()){
            return; // don't continue if no taker is found
        }

        int seedsPerSeedContainer = Math.max(1, (int) (seedsToPush / (foundSeedContainer.size() * 1.0F)));
        foundSeedContainer.forEach(otherSeedContainer -> {
            tile.seedContainer.takeSeedsFromContainer(otherSeedContainer.putSeedsIntoContainer(seedsPerSeedContainer, false), false);
        });
        tile.needsToSync = true;
    }
}
