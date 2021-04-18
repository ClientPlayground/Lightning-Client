package net.minecraft.world.demo;

import net.minecraft.world.*;
import net.minecraft.server.*;
import net.minecraft.world.storage.*;
import net.minecraft.profiler.*;

public class DemoWorldServer extends WorldServer
{
    private static final long demoWorldSeed;
    public static final WorldSettings demoWorldSettings;
    
    static {
        demoWorldSeed = "North Carolina".hashCode();
        demoWorldSettings = new WorldSettings(DemoWorldServer.demoWorldSeed, WorldSettings.GameType.SURVIVAL, true, false, WorldType.DEFAULT).enableBonusChest();
    }
    
    public DemoWorldServer(final MinecraftServer server, final ISaveHandler saveHandlerIn, final WorldInfo worldInfoIn, final int dimensionId, final Profiler profilerIn) {
        super(server, saveHandlerIn, worldInfoIn, dimensionId, profilerIn);
        this.worldInfo.populateFromWorldSettings(DemoWorldServer.demoWorldSettings);
    }
}
