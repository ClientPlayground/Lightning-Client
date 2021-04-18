package net.minecraft.server.integrated;

import net.minecraft.server.*;
import net.minecraft.client.multiplayer.*;
import org.apache.logging.log4j.*;
import net.minecraft.server.management.*;
import net.minecraft.world.demo.*;
import net.minecraft.command.*;
import net.minecraft.world.storage.*;
import java.io.*;
import net.minecraft.world.*;
import net.minecraft.crash.*;
import net.minecraft.client.*;
import net.minecraft.profiler.*;
import net.minecraft.util.*;
import java.net.*;
import com.google.common.collect.*;
import net.minecraft.entity.player.*;
import java.util.*;
import com.google.common.util.concurrent.*;
import java.util.concurrent.*;

public class IntegratedServer extends MinecraftServer
{
    private static final Logger logger;
    private final Minecraft mc;
    private final WorldSettings theWorldSettings;
    private boolean isGamePaused;
    private boolean isPublic;
    private ThreadLanServerPing lanServerPing;
    
    static {
        logger = LogManager.getLogger();
    }
    
    public IntegratedServer(final Minecraft mcIn) {
        super(mcIn.getProxy(), new File(mcIn.mcDataDir, IntegratedServer.USER_CACHE_FILE.getName()));
        this.mc = mcIn;
        this.theWorldSettings = null;
    }
    
    public IntegratedServer(final Minecraft mcIn, final String folderName, final String worldName, final WorldSettings settings) {
        super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, IntegratedServer.USER_CACHE_FILE.getName()));
        this.setServerOwner(mcIn.getSession().getUsername());
        this.setFolderName(folderName);
        this.setWorldName(worldName);
        this.setDemo(mcIn.isDemo());
        this.canCreateBonusChest(settings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigManager(new IntegratedPlayerList(this));
        this.mc = mcIn;
        this.theWorldSettings = (this.isDemo() ? DemoWorldServer.demoWorldSettings : settings);
    }
    
    @Override
    protected ServerCommandManager createNewCommandManager() {
        return new IntegratedServerCommandManager();
    }
    
    @Override
    protected void loadAllWorlds(final String p_71247_1_, final String p_71247_2_, final long seed, final WorldType type, final String p_71247_6_) {
        this.convertMapIfNeeded(p_71247_1_);
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        final ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(p_71247_1_, true);
        this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        if (worldinfo == null) {
            worldinfo = new WorldInfo(this.theWorldSettings, p_71247_2_);
        }
        else {
            worldinfo.setWorldName(p_71247_2_);
        }
        for (int i = 0; i < this.worldServers.length; ++i) {
            int j = 0;
            if (i == 1) {
                j = -1;
            }
            if (i == 2) {
                j = 1;
            }
            if (i == 0) {
                if (this.isDemo()) {
                    this.worldServers[i] = (WorldServer)new DemoWorldServer(this, isavehandler, worldinfo, j, this.theProfiler).init();
                }
                else {
                    this.worldServers[i] = (WorldServer)new WorldServer(this, isavehandler, worldinfo, j, this.theProfiler).init();
                }
                this.worldServers[i].initialize(this.theWorldSettings);
            }
            else {
                this.worldServers[i] = (WorldServer)new WorldServerMulti(this, isavehandler, j, this.worldServers[0], this.theProfiler).init();
            }
            this.worldServers[i].addWorldAccess(new WorldManager(this, this.worldServers[i]));
        }
        this.getConfigurationManager().setPlayerManager(this.worldServers);
        if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
        }
        this.initialWorldChunkLoad();
    }
    
    @Override
    protected boolean startServer() throws IOException {
        IntegratedServer.logger.info("Starting integrated minecraft server version 1.8.8");
        this.setOnlineMode(true);
        this.setCanSpawnAnimals(true);
        this.setCanSpawnNPCs(true);
        this.setAllowPvp(true);
        this.setAllowFlight(true);
        IntegratedServer.logger.info("Generating keypair");
        this.setKeyPair(CryptManager.generateKeyPair());
        this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.theWorldSettings.getSeed(), this.theWorldSettings.getTerrainType(), this.theWorldSettings.getWorldName());
        this.setMOTD(String.valueOf(this.getServerOwner()) + " - " + this.worldServers[0].getWorldInfo().getWorldName());
        return true;
    }
    
    @Override
    public void tick() {
        final boolean flag = this.isGamePaused;
        this.isGamePaused = (Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused());
        if (!flag && this.isGamePaused) {
            IntegratedServer.logger.info("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }
        if (this.isGamePaused) {
            synchronized (this.futureTaskQueue) {
                while (!this.futureTaskQueue.isEmpty()) {
                    Util.func_181617_a(this.futureTaskQueue.poll(), IntegratedServer.logger);
                }
                // monitorexit(this.futureTaskQueue)
                return;
            }
        }
        super.tick();
        if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
            IntegratedServer.logger.info("Changing view distance to {}, from {}", new Object[] { this.mc.gameSettings.renderDistanceChunks, this.getConfigurationManager().getViewDistance() });
            this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
        }
        if (this.mc.theWorld != null) {
            final WorldInfo worldinfo1 = this.worldServers[0].getWorldInfo();
            final WorldInfo worldinfo2 = this.mc.theWorld.getWorldInfo();
            if (!worldinfo1.isDifficultyLocked() && worldinfo2.getDifficulty() != worldinfo1.getDifficulty()) {
                IntegratedServer.logger.info("Changing difficulty to {}, from {}", new Object[] { worldinfo2.getDifficulty(), worldinfo1.getDifficulty() });
                this.setDifficultyForAllWorlds(worldinfo2.getDifficulty());
            }
            else if (worldinfo2.isDifficultyLocked() && !worldinfo1.isDifficultyLocked()) {
                IntegratedServer.logger.info("Locking difficulty to {}", new Object[] { worldinfo2.getDifficulty() });
                WorldServer[] worldServers;
                for (int length = (worldServers = this.worldServers).length, i = 0; i < length; ++i) {
                    final WorldServer worldserver = worldServers[i];
                    if (worldserver != null) {
                        worldserver.getWorldInfo().setDifficultyLocked(true);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean canStructuresSpawn() {
        return false;
    }
    
    @Override
    public WorldSettings.GameType getGameType() {
        return this.theWorldSettings.getGameType();
    }
    
    @Override
    public EnumDifficulty getDifficulty() {
        return this.mc.theWorld.getWorldInfo().getDifficulty();
    }
    
    @Override
    public boolean isHardcore() {
        return this.theWorldSettings.getHardcoreEnabled();
    }
    
    @Override
    public boolean func_181034_q() {
        return true;
    }
    
    @Override
    public boolean func_183002_r() {
        return true;
    }
    
    @Override
    public File getDataDirectory() {
        return this.mc.mcDataDir;
    }
    
    @Override
    public boolean isDedicatedServer() {
        return false;
    }
    
    @Override
    public boolean func_181035_ah() {
        return false;
    }
    
    @Override
    protected void finalTick(final CrashReport report) {
        this.mc.crashed(report);
    }
    
    @Override
    public CrashReport addServerInfoToCrashReport(CrashReport report) {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Integrated Server (map_client.txt)";
            }
        });
        report.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>() {
            @Override
            public String call() throws Exception {
                String s = ClientBrandRetriever.getClientModName();
                if (!s.equals("vanilla")) {
                    return "Definitely; Client brand changed to '" + s + "'";
                }
                s = IntegratedServer.this.getServerModName();
                return s.equals("vanilla") ? ((Minecraft.class.getSigners() == null) ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.") : ("Definitely; Server brand changed to '" + s + "'");
            }
        });
        return report;
    }
    
    @Override
    public void setDifficultyForAllWorlds(final EnumDifficulty difficulty) {
        super.setDifficultyForAllWorlds(difficulty);
        if (this.mc.theWorld != null) {
            this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
        }
    }
    
    @Override
    public void addServerStatsToSnooper(final PlayerUsageSnooper playerSnooper) {
        super.addServerStatsToSnooper(playerSnooper);
        playerSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
    }
    
    @Override
    public boolean isSnooperEnabled() {
        return Minecraft.getMinecraft().isSnooperEnabled();
    }
    
    @Override
    public String shareToLAN(final WorldSettings.GameType type, final boolean allowCheats) {
        try {
            int i = -1;
            try {
                i = HttpUtil.getSuitableLanPort();
            }
            catch (IOException ex) {}
            if (i <= 0) {
                i = 25564;
            }
            this.getNetworkSystem().addLanEndpoint(null, i);
            IntegratedServer.logger.info("Started on " + i);
            this.isPublic = true;
            (this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), new StringBuilder(String.valueOf(i)).toString())).start();
            this.getConfigurationManager().setGameType(type);
            this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
            return new StringBuilder(String.valueOf(i)).toString();
        }
        catch (IOException var6) {
            return null;
        }
    }
    
    @Override
    public void stopServer() {
        super.stopServer();
        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }
    
    @Override
    public void initiateShutdown() {
        Futures.getUnchecked((Future)this.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                for (final EntityPlayerMP entityplayermp : Lists.newArrayList((Iterable)IntegratedServer.this.getConfigurationManager().func_181057_v())) {
                    IntegratedServer.this.getConfigurationManager().playerLoggedOut(entityplayermp);
                }
            }
        }));
        super.initiateShutdown();
        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }
    
    public void setStaticInstance() {
        this.setInstance();
    }
    
    public boolean getPublic() {
        return this.isPublic;
    }
    
    @Override
    public void setGameType(final WorldSettings.GameType gameMode) {
        this.getConfigurationManager().setGameType(gameMode);
    }
    
    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }
    
    @Override
    public int getOpPermissionLevel() {
        return 4;
    }
}
