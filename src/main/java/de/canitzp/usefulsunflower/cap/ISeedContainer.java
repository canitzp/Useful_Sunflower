package de.canitzp.usefulsunflower.cap;

public interface ISeedContainer {

    public static String NBT_ROOT_KEY = "seed_container";
    public static String NBT_STORED_SEEDS_KEY = "stored";

    int getSeedContainerSize();

    int getSeedsInsideContainer();

    void setSeedsInsideContainer(int amount);

    int takeSeedsFromContainer(int amount, boolean simulate);

    int putSeedsIntoContainer(int amount, boolean simulate);

    boolean canTake();

    boolean canPut();

}
