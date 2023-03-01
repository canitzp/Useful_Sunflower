package de.canitzp.usefulsunflower.block.entity;

import de.canitzp.usefulsunflower.USFRegistry;
import de.canitzp.usefulsunflower.block.SqueezerBlock;
import de.canitzp.usefulsunflower.container.SqueezerContainer;
import de.canitzp.usefulsunflower.cap.CapabilitySeedContainer;
import de.canitzp.usefulsunflower.cap.ISeedContainer;
import de.canitzp.usefulsunflower.cap.SimpleSeedContainer;
import de.canitzp.usefulsunflower.recipe.SqueezerRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqueezerBlockEntity extends BlockEntity {

    public static final int SLOT_INPUT_SEED_CONTAINER = 0;
    public static final int SLOT_INPUT_INGREDIENT = 1;
    public static final int SLOT_OUTPUT_RESULT = 2;

    public SqueezerContainer inv = new SqueezerContainer();
    public SimpleSeedContainer seedContainer = new SimpleSeedContainer(100_000);
    public SqueezerSidedInvWrapper[] wrapper = SqueezerSidedInvWrapper.createForAllSides(this, this.inv);
    public int clicksUntilConversion = 0;
    public boolean isPowered = false;
    public ResourceLocation recipeId;

    public SqueezerBlockEntity(BlockPos pos, BlockState state) {
        super(USFRegistry.USFBlockEntityTypes.SQUEEZER.get(), pos, state);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            SqueezerSidedInvWrapper wrapper = side != null ? this.wrapper[side.ordinal()] : new SqueezerSidedInvWrapper(this, this.inv, null);
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> wrapper));
        }
        if(cap == CapabilitySeedContainer.SEED_CONTAINER){
            return CapabilitySeedContainer.SEED_CONTAINER.orEmpty(cap, LazyOptional.of(() -> this.seedContainer));
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.inv.fromTag(tag.getList("inventory", Tag.TAG_COMPOUND));
        this.clicksUntilConversion = tag.getInt("clicks_until_conversion");
        this.seedContainer.deserializeNBT(tag.getCompound(ISeedContainer.NBT_ROOT_KEY));
        this.isPowered = tag.getBoolean("powered");
        if(tag.contains("recipe_id", Tag.TAG_STRING)){
            this.recipeId = new ResourceLocation(tag.getString("recipe_id"));
        } else {
            // reset recipe, in case it was set to null on server side
            this.recipeId = null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inv.createTag());
        tag.putInt("clicks_until_conversion", this.clicksUntilConversion);
        tag.put(ISeedContainer.NBT_ROOT_KEY, this.seedContainer.serializeNBT());
        tag.putBoolean("powered", this.isPowered);
        if(this.recipeId != null){
            tag.putString("recipe_id", this.recipeId.toString());
        }
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

    public SqueezerRecipe getLoadedRecipe(){
        return this.level.getRecipeManager()
                .getAllRecipesFor(SqueezerRecipe.TYPE)
                .stream()
                .filter(squeezerRecipe -> squeezerRecipe.recipeId().equals(this.recipeId))
                .findFirst()
                .orElse(null);
    }

    public void resetRecipe(){
        // reset pressing count
        this.clicksUntilConversion = 0;
        this.recipeId = null;
    }

    public void setRedstoneState(boolean powered){
        if(powered != this.isPowered){
            if(powered){
                if(this.recipeId == null){
                    this.workSetRecipe(false);
                }
                this.workTick(false);
            }
            this.isPowered = powered;
            sync();
        }
    }

    public InteractionResult onClick(Player player, InteractionHand hand, ItemStack stack, BlockState state){
        boolean shouldSync = false;
        boolean shouldWork = true;

        // when shift -> try to get recipe ingredient back and reset recipe
        if(player.isCrouching()){
            if(!this.inv.getItem(SLOT_INPUT_INGREDIENT).isEmpty()){
                // if player is creative and inventory is full -> don't waste output item
                if(!(player.isCreative() && player.getInventory().getFreeSlot() == -1)){
                    if (player.getInventory().add(this.inv.getItem(SLOT_INPUT_INGREDIENT))) {
                        this.resetRecipe();
                        sync();
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        // on player click with seed container -> try to put seeds into squeezer
        if (this.takeSeedsFromSeedContainer(stack)) {
            shouldSync = true;
            shouldWork = false;
        } else if(this.isRecipeInput(stack)) {
            if(this.inv.getItem(SLOT_INPUT_INGREDIENT).isEmpty()){
                this.inv.setItem(SLOT_INPUT_INGREDIENT, stack.copy());
                stack.setCount(0);
            } else if(ItemStack.isSameItemSameTags(this.inv.getItem(SLOT_INPUT_INGREDIENT), stack)){
                int countIncrease = Math.min(stack.getCount(), stack.getMaxStackSize() - this.inv.getItem(SLOT_INPUT_INGREDIENT).getCount());
                this.inv.getItem(SLOT_INPUT_INGREDIENT).grow(countIncrease);
                stack.shrink(countIncrease);
            }

            shouldSync = true;
            shouldWork = this.recipeId == null;
        }

        // move items from squeezer to inv
        if(!this.inv.getItem(SLOT_OUTPUT_RESULT).isEmpty()){
            // if player is creative and inventory is full -> don't waste output item
            if(!(player.isCreative() && player.getInventory().getFreeSlot() == -1)){
                if (player.getInventory().add(this.inv.getItem(SLOT_OUTPUT_RESULT))) {
                    shouldSync = true;
                    shouldWork = false; // click shouldn't count if player is just taking out the oil
                }
            }
        }

        shouldSync = this.inputItem(shouldSync);
        if(shouldWork){
            if(this.recipeId == null){
                shouldSync = this.workSetRecipe(shouldSync);
            } else {
                shouldSync = this.workTick(shouldSync);
            }
        }
        if(shouldSync) sync();

        return InteractionResult.SUCCESS;
    }

    public boolean takeSeedsFromSeedContainer(ItemStack stack){
        if(!stack.isEmpty() && stack.getCapability(CapabilitySeedContainer.SEED_CONTAINER).isPresent()){
            int seedsUntilFull = this.seedContainer.getSeedContainerSize() - this.seedContainer.getSeedsInsideContainer();
            ISeedContainer seedContainer = stack.getCapability(CapabilitySeedContainer.SEED_CONTAINER).resolve().get();
            this.seedContainer.setSeedsInsideContainer(this.seedContainer.getSeedsInsideContainer() + seedContainer.takeSeedsFromContainer(seedsUntilFull, false));

            return true;
        }
        return false;
    }

    public boolean inputItem(boolean shouldSync){
        // resolve items in slot 0 to seeds
        if (this.takeSeedsFromSeedContainer(this.inv.getItem(SLOT_INPUT_SEED_CONTAINER))) {
            return true;
        }
        return shouldSync;
    }

    // set new recipe, if none is set
    public boolean workSetRecipe(boolean shouldSync){
        if(this.recipeId == null){
            SqueezerRecipe recipe = this.level.getRecipeManager()
                    .getAllRecipesFor(SqueezerRecipe.TYPE)
                    .stream()
                    .filter(squeezerRecipe -> ItemStack.isSameItemSameTags(squeezerRecipe.ingredient(), this.inv.getItem(SLOT_INPUT_INGREDIENT)))
                    .filter(squeezerRecipe -> this.inv.getItem(SLOT_INPUT_INGREDIENT).getCount() >= squeezerRecipe.ingredient().getCount())
                    .findFirst()
                    .orElse(null);
            if(recipe != null){
                this.recipeId = recipe.recipeId();
                return true;
            }
        }
        return shouldSync;
    }

    // return should sync
    public boolean workTick(boolean shouldSync){
        SqueezerRecipe currentRecipe = this.getLoadedRecipe();
        if(currentRecipe == null){
            // don't proceed because no recipe is provided
            return shouldSync;
        }

        // check if work should be done (enough seeds)
        if(this.seedContainer.getSeedsInsideContainer() < currentRecipe.seedsNecessary()){
            return shouldSync;
        }

        // only count the pressing, if there is space in the output slot
        if(this.inv.getItem(SLOT_OUTPUT_RESULT).getCount() >= Math.min(this.inv.getMaxStackSize(), this.inv.getItem(SLOT_OUTPUT_RESULT).getMaxStackSize())){
            // don't convert, because there is no space
            return shouldSync;
        }

        this.clicksUntilConversion++;

        if(this.clicksUntilConversion % 2 == 0){
            this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(SqueezerBlock.CYCLE, 2));
        } else {
            this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(SqueezerBlock.CYCLE, 1));
        }
        if(this.clicksUntilConversion >= 10){
            // convert stored seeds to oil
            this.seedContainer.setSeedsInsideContainer(this.seedContainer.getSeedsInsideContainer() - currentRecipe.seedsNecessary());
            this.inv.getItem(SLOT_INPUT_INGREDIENT).shrink(currentRecipe.ingredient().getCount());
            this.resetRecipe();
            this.workSetRecipe(shouldSync); // return ignored, since it syncs always
            if(this.inv.getItem(SLOT_OUTPUT_RESULT).isEmpty()){
                this.inv.setItem(SLOT_OUTPUT_RESULT, currentRecipe.getResultItem());
            } else {
                this.inv.getItem(SLOT_OUTPUT_RESULT).grow(currentRecipe.getResultItem().getCount());
            }

            this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(SqueezerBlock.CYCLE, 0));
        }
        return true;
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

    public boolean isRecipeInput(ItemStack stack){
        return this.level.getRecipeManager()
                .getAllRecipesFor(SqueezerRecipe.TYPE)
                .stream()
                .anyMatch(squeezerRecipe -> {
                    return ItemStack.isSameItemSameTags(squeezerRecipe.ingredient(), stack) && stack.getCount() >= squeezerRecipe.ingredient().getCount();
                });
    }

}
