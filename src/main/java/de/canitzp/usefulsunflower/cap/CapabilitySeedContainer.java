package de.canitzp.usefulsunflower.cap;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilitySeedContainer {

    public static final Capability<ISeedContainer> SEED_CONTAINER = CapabilityManager.get(new CapabilityToken<>(){});

    public static void register(RegisterCapabilitiesEvent event){
        event.register(ISeedContainer.class);
    }
}
