package net.minecraft.client.settings;

import com.google.gson.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.*;
import net.minecraft.world.*;
import org.apache.logging.log4j.*;
import java.lang.reflect.*;
import clientname.*;
import org.apache.commons.lang3.*;
import net.minecraft.client.resources.*;
import org.lwjgl.input.*;
import net.minecraft.client.renderer.texture.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.stream.*;
import java.io.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import com.google.common.collect.*;
import java.util.*;
import net.minecraft.util.*;

public class GameSettings
{
    private static final Logger logger;
    private static final Gson gson;
    private static final ParameterizedType typeListString;
    private static final String[] GUISCALES;
    private static final String[] PARTICLES;
    private static final String[] AMBIENT_OCCLUSIONS;
    private static final String[] STREAM_COMPRESSIONS;
    private static final String[] STREAM_CHAT_MODES;
    private static final String[] STREAM_CHAT_FILTER_MODES;
    private static final String[] STREAM_MIC_MODES;
    private static final String[] field_181149_aW;
    public float mouseSensitivity;
    public boolean invertMouse;
    public int renderDistanceChunks;
    public boolean viewBobbing;
    public boolean anaglyph;
    public boolean fboEnable;
    public int limitFramerate;
    public int clouds;
    public boolean fancyGraphics;
    public int ambientOcclusion;
    public List<String> resourcePacks;
    public List<String> field_183018_l;
    public EntityPlayer.EnumChatVisibility chatVisibility;
    public boolean chatColours;
    public boolean chatLinks;
    public boolean chatLinksPrompt;
    public float chatOpacity;
    public boolean snooperEnabled;
    public boolean fullScreen;
    public boolean enableVsync;
    public boolean useVbo;
    public boolean allowBlockAlternatives;
    public boolean reducedDebugInfo;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus;
    private final Set<EnumPlayerModelParts> setModelParts;
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips;
    public float chatScale;
    public float chatWidth;
    public float chatHeightUnfocused;
    public float chatHeightFocused;
    public boolean showInventoryAchievementHint;
    public int mipmapLevels;
    private Map<SoundCategory, Float> mapSoundLevels;
    public float streamBytesPerPixel;
    public float streamMicVolume;
    public float streamGameVolume;
    public float streamKbps;
    public float streamFps;
    public int streamCompression;
    public boolean streamSendMetadata;
    public String streamPreferredServer;
    public int streamChatEnabled;
    public int streamChatUserFilter;
    public int streamMicToggleBehavior;
    public boolean field_181150_U;
    public boolean field_181151_V;
    public KeyBinding keyBindForward;
    public KeyBinding keyBindLeft;
    public KeyBinding keyBindBack;
    public KeyBinding keyBindRight;
    public KeyBinding keyBindJump;
    public KeyBinding keyBindSneak;
    public KeyBinding keyBindSprint;
    public KeyBinding keyBindInventory;
    public KeyBinding keyBindUseItem;
    public KeyBinding keyBindDrop;
    public KeyBinding keyBindAttack;
    public KeyBinding keyBindPickBlock;
    public KeyBinding keyBindChat;
    public KeyBinding keyBindPlayerList;
    public KeyBinding keyBindCommand;
    public KeyBinding keyBindScreenshot;
    public KeyBinding keyBindTogglePerspective;
    public KeyBinding keyBindSmoothCamera;
    public KeyBinding keyBindFullscreen;
    public KeyBinding keyBindSpectatorOutlines;
    public KeyBinding keyBindStreamStartStop;
    public KeyBinding keyBindStreamPauseUnpause;
    public KeyBinding keyBindStreamCommercials;
    public KeyBinding keyBindStreamToggleMic;
    public KeyBinding[] keyBindsHotbar;
    public KeyBinding[] keyBindings;
    protected Minecraft mc;
    private File optionsFile;
    public EnumDifficulty difficulty;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public boolean field_181657_aC;
    public String lastServer;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public float fovSetting;
    public float gammaSetting;
    public float saturation;
    public int guiScale;
    public int particleSetting;
    public String language;
    public boolean forceUnicodeFont;
    public KeyBinding CLIENT_GUI_MOD_POS;
    public KeyBinding ZOOM;
    public KeyBinding CLIENT_PERSPECTIVE;
    
    static {
        logger = LogManager.getLogger();
        gson = new Gson();
        typeListString = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] { String.class };
            }
            
            @Override
            public Type getRawType() {
                return List.class;
            }
            
            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        GUISCALES = new String[] { "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large" };
        PARTICLES = new String[] { "options.particles.all", "options.particles.decreased", "options.particles.minimal" };
        AMBIENT_OCCLUSIONS = new String[] { "options.ao.off", "options.ao.min", "options.ao.max" };
        STREAM_COMPRESSIONS = new String[] { "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high" };
        STREAM_CHAT_MODES = new String[] { "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never" };
        STREAM_CHAT_FILTER_MODES = new String[] { "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods" };
        STREAM_MIC_MODES = new String[] { "options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk" };
        field_181149_aW = new String[] { "options.off", "options.graphics.fast", "options.graphics.fancy" };
    }
    
    public GameSettings(final Minecraft mcIn, final File p_i46326_2_) {
        this.mouseSensitivity = 0.5f;
        this.renderDistanceChunks = -1;
        this.viewBobbing = true;
        this.fboEnable = true;
        this.limitFramerate = 120;
        this.clouds = 2;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.resourcePacks = (List<String>)Lists.newArrayList();
        this.field_183018_l = (List<String>)Lists.newArrayList();
        this.chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
        this.chatColours = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.chatOpacity = 1.0f;
        this.snooperEnabled = true;
        this.enableVsync = true;
        this.useVbo = false;
        this.allowBlockAlternatives = true;
        this.reducedDebugInfo = false;
        this.pauseOnLostFocus = true;
        this.setModelParts = (Set<EnumPlayerModelParts>)Sets.newHashSet((Object[])EnumPlayerModelParts.values());
        this.heldItemTooltips = true;
        this.chatScale = 1.0f;
        this.chatWidth = 1.0f;
        this.chatHeightUnfocused = 0.44366196f;
        this.chatHeightFocused = 1.0f;
        this.showInventoryAchievementHint = true;
        this.mipmapLevels = 4;
        this.mapSoundLevels = (Map<SoundCategory, Float>)Maps.newEnumMap((Class)SoundCategory.class);
        this.streamBytesPerPixel = 0.5f;
        this.streamMicVolume = 1.0f;
        this.streamGameVolume = 1.0f;
        this.streamKbps = 0.5412844f;
        this.streamFps = 0.31690142f;
        this.streamCompression = 1;
        this.streamSendMetadata = true;
        this.streamPreferredServer = "";
        this.streamChatEnabled = 0;
        this.streamChatUserFilter = 0;
        this.streamMicToggleBehavior = 0;
        this.field_181150_U = true;
        this.field_181151_V = true;
        this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
        this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
        this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
        this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
        this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
        this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
        this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
        this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
        this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
        this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
        this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
        this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
        this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
        this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
        this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
        this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
        this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
        this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
        this.keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
        this.keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
        this.keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
        this.keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
        this.keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
        this.keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
        this.keyBindsHotbar = new KeyBinding[] { new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory") };
        this.CLIENT_GUI_MOD_POS = new KeyBinding("Mod Positioning", 25, Client.ClientName);
        this.ZOOM = new KeyBinding("Zoom", 46, Client.ClientName);
        this.CLIENT_PERSPECTIVE = new KeyBinding("Perspective Mod", 37, Client.ClientName);
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll((Object[])new KeyBinding[] { this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines }, (Object[])this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0f;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.mc = mcIn;
        this.optionsFile = new File(p_i46326_2_, "options.txt");
        if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
            Options.RENDER_DISTANCE.setValueMax(32.0f);
        }
        else {
            Options.RENDER_DISTANCE.setValueMax(16.0f);
        }
        this.renderDistanceChunks = (mcIn.isJava64bit() ? 12 : 8);
        this.loadOptions();
        this.addClientKeybinds();
    }
    
    public GameSettings() {
        this.mouseSensitivity = 0.5f;
        this.renderDistanceChunks = -1;
        this.viewBobbing = true;
        this.fboEnable = true;
        this.limitFramerate = 120;
        this.clouds = 2;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.resourcePacks = (List<String>)Lists.newArrayList();
        this.field_183018_l = (List<String>)Lists.newArrayList();
        this.chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
        this.chatColours = true;
        this.chatLinks = true;
        this.chatLinksPrompt = true;
        this.chatOpacity = 1.0f;
        this.snooperEnabled = true;
        this.enableVsync = true;
        this.useVbo = false;
        this.allowBlockAlternatives = true;
        this.reducedDebugInfo = false;
        this.pauseOnLostFocus = true;
        this.setModelParts = (Set<EnumPlayerModelParts>)Sets.newHashSet((Object[])EnumPlayerModelParts.values());
        this.heldItemTooltips = true;
        this.chatScale = 1.0f;
        this.chatWidth = 1.0f;
        this.chatHeightUnfocused = 0.44366196f;
        this.chatHeightFocused = 1.0f;
        this.showInventoryAchievementHint = true;
        this.mipmapLevels = 4;
        this.mapSoundLevels = (Map<SoundCategory, Float>)Maps.newEnumMap((Class)SoundCategory.class);
        this.streamBytesPerPixel = 0.5f;
        this.streamMicVolume = 1.0f;
        this.streamGameVolume = 1.0f;
        this.streamKbps = 0.5412844f;
        this.streamFps = 0.31690142f;
        this.streamCompression = 1;
        this.streamSendMetadata = true;
        this.streamPreferredServer = "";
        this.streamChatEnabled = 0;
        this.streamChatUserFilter = 0;
        this.streamMicToggleBehavior = 0;
        this.field_181150_U = true;
        this.field_181151_V = true;
        this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
        this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
        this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
        this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
        this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
        this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
        this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
        this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
        this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
        this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
        this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
        this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
        this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
        this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
        this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
        this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
        this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
        this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
        this.keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
        this.keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
        this.keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
        this.keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
        this.keyBindStreamCommercials = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
        this.keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
        this.keyBindsHotbar = new KeyBinding[] { new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory") };
        this.CLIENT_GUI_MOD_POS = new KeyBinding("Mod Positioning", 25, Client.ClientName);
        this.ZOOM = new KeyBinding("Zoom", 46, Client.ClientName);
        this.CLIENT_PERSPECTIVE = new KeyBinding("Perspective Mod", 37, Client.ClientName);
        this.keyBindings = (KeyBinding[])ArrayUtils.addAll((Object[])new KeyBinding[] { this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindStreamStartStop, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindFullscreen, this.keyBindSpectatorOutlines }, (Object[])this.keyBindsHotbar);
        this.difficulty = EnumDifficulty.NORMAL;
        this.lastServer = "";
        this.fovSetting = 70.0f;
        this.language = "en_US";
        this.forceUnicodeFont = false;
        this.addClientKeybinds();
    }
    
    private void addClientKeybinds() {
        this.keyBindings = (KeyBinding[])ArrayUtils.add((Object[])this.keyBindings, (Object)this.CLIENT_GUI_MOD_POS);
        this.keyBindings = (KeyBinding[])ArrayUtils.add((Object[])this.keyBindings, (Object)this.ZOOM);
        this.keyBindings = (KeyBinding[])ArrayUtils.add((Object[])this.keyBindings, (Object)this.CLIENT_PERSPECTIVE);
    }
    
    public static String getKeyDisplayString(final int p_74298_0_) {
        return (p_74298_0_ < 0) ? I18n.format("key.mouseButton", p_74298_0_ + 101) : ((p_74298_0_ < 256) ? Keyboard.getKeyName(p_74298_0_) : String.format("%c", (char)(p_74298_0_ - 256)).toUpperCase());
    }
    
    public static boolean isKeyDown(final KeyBinding p_100015_0_) {
        return p_100015_0_.getKeyCode() != 0 && ((p_100015_0_.getKeyCode() < 0) ? Mouse.isButtonDown(p_100015_0_.getKeyCode() + 100) : Keyboard.isKeyDown(p_100015_0_.getKeyCode()));
    }
    
    public void setOptionKeyBinding(final KeyBinding p_151440_1_, final int p_151440_2_) {
        p_151440_1_.setKeyCode(p_151440_2_);
        this.saveOptions();
    }
    
    public void setOptionFloatValue(final Options p_74304_1_, final float p_74304_2_) {
        if (p_74304_1_ == Options.SENSITIVITY) {
            this.mouseSensitivity = p_74304_2_;
        }
        if (p_74304_1_ == Options.FOV) {
            this.fovSetting = p_74304_2_;
        }
        if (p_74304_1_ == Options.GAMMA) {
            this.gammaSetting = p_74304_2_;
        }
        if (p_74304_1_ == Options.FRAMERATE_LIMIT) {
            this.limitFramerate = (int)p_74304_2_;
        }
        if (p_74304_1_ == Options.CHAT_OPACITY) {
            this.chatOpacity = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (p_74304_1_ == Options.CHAT_HEIGHT_FOCUSED) {
            this.chatHeightFocused = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (p_74304_1_ == Options.CHAT_HEIGHT_UNFOCUSED) {
            this.chatHeightUnfocused = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (p_74304_1_ == Options.CHAT_WIDTH) {
            this.chatWidth = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (p_74304_1_ == Options.CHAT_SCALE) {
            this.chatScale = p_74304_2_;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }
        if (p_74304_1_ == Options.MIPMAP_LEVELS) {
            final int i = this.mipmapLevels;
            this.mipmapLevels = (int)p_74304_2_;
            if (i != p_74304_2_) {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
                this.mc.scheduleResourcesRefresh();
            }
        }
        if (p_74304_1_ == Options.BLOCK_ALTERNATIVES) {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_74304_1_ == Options.RENDER_DISTANCE) {
            this.renderDistanceChunks = (int)p_74304_2_;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
        if (p_74304_1_ == Options.STREAM_BYTES_PER_PIXEL) {
            this.streamBytesPerPixel = p_74304_2_;
        }
        if (p_74304_1_ == Options.STREAM_VOLUME_MIC) {
            this.streamMicVolume = p_74304_2_;
            this.mc.getTwitchStream().updateStreamVolume();
        }
        if (p_74304_1_ == Options.STREAM_VOLUME_SYSTEM) {
            this.streamGameVolume = p_74304_2_;
            this.mc.getTwitchStream().updateStreamVolume();
        }
        if (p_74304_1_ == Options.STREAM_KBPS) {
            this.streamKbps = p_74304_2_;
        }
        if (p_74304_1_ == Options.STREAM_FPS) {
            this.streamFps = p_74304_2_;
        }
    }
    
    public void setOptionValue(final Options p_74306_1_, final int p_74306_2_) {
        if (p_74306_1_ == Options.INVERT_MOUSE) {
            this.invertMouse = !this.invertMouse;
        }
        if (p_74306_1_ == Options.GUI_SCALE) {
            this.guiScale = (this.guiScale + p_74306_2_ & 0x3);
        }
        if (p_74306_1_ == Options.PARTICLES) {
            this.particleSetting = (this.particleSetting + p_74306_2_) % 3;
        }
        if (p_74306_1_ == Options.VIEW_BOBBING) {
            this.viewBobbing = !this.viewBobbing;
        }
        if (p_74306_1_ == Options.RENDER_CLOUDS) {
            this.clouds = (this.clouds + p_74306_2_) % 3;
        }
        if (p_74306_1_ == Options.FORCE_UNICODE_FONT) {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }
        if (p_74306_1_ == Options.FBO_ENABLE) {
            this.fboEnable = !this.fboEnable;
        }
        if (p_74306_1_ == Options.ANAGLYPH) {
            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }
        if (p_74306_1_ == Options.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_74306_1_ == Options.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = (this.ambientOcclusion + p_74306_2_) % 3;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_74306_1_ == Options.CHAT_VISIBILITY) {
            this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + p_74306_2_) % 3);
        }
        if (p_74306_1_ == Options.STREAM_COMPRESSION) {
            this.streamCompression = (this.streamCompression + p_74306_2_) % 3;
        }
        if (p_74306_1_ == Options.STREAM_SEND_METADATA) {
            this.streamSendMetadata = !this.streamSendMetadata;
        }
        if (p_74306_1_ == Options.STREAM_CHAT_ENABLED) {
            this.streamChatEnabled = (this.streamChatEnabled + p_74306_2_) % 3;
        }
        if (p_74306_1_ == Options.STREAM_CHAT_USER_FILTER) {
            this.streamChatUserFilter = (this.streamChatUserFilter + p_74306_2_) % 3;
        }
        if (p_74306_1_ == Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            this.streamMicToggleBehavior = (this.streamMicToggleBehavior + p_74306_2_) % 2;
        }
        if (p_74306_1_ == Options.CHAT_COLOR) {
            this.chatColours = !this.chatColours;
        }
        if (p_74306_1_ == Options.CHAT_LINKS) {
            this.chatLinks = !this.chatLinks;
        }
        if (p_74306_1_ == Options.CHAT_LINKS_PROMPT) {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }
        if (p_74306_1_ == Options.SNOOPER_ENABLED) {
            this.snooperEnabled = !this.snooperEnabled;
        }
        if (p_74306_1_ == Options.TOUCHSCREEN) {
            this.touchscreen = !this.touchscreen;
        }
        if (p_74306_1_ == Options.USE_FULLSCREEN) {
            this.fullScreen = !this.fullScreen;
            if (this.mc.isFullScreen() != this.fullScreen) {
                this.mc.toggleFullscreen();
            }
        }
        if (p_74306_1_ == Options.ENABLE_VSYNC) {
            Display.setVSyncEnabled(this.enableVsync = !this.enableVsync);
        }
        if (p_74306_1_ == Options.USE_VBO) {
            this.useVbo = !this.useVbo;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_74306_1_ == Options.BLOCK_ALTERNATIVES) {
            this.allowBlockAlternatives = !this.allowBlockAlternatives;
            this.mc.renderGlobal.loadRenderers();
        }
        if (p_74306_1_ == Options.REDUCED_DEBUG_INFO) {
            this.reducedDebugInfo = !this.reducedDebugInfo;
        }
        if (p_74306_1_ == Options.ENTITY_SHADOWS) {
            this.field_181151_V = !this.field_181151_V;
        }
        this.saveOptions();
    }
    
    public float getOptionFloatValue(final Options p_74296_1_) {
        return (p_74296_1_ == Options.FOV) ? this.fovSetting : ((p_74296_1_ == Options.GAMMA) ? this.gammaSetting : ((p_74296_1_ == Options.SATURATION) ? this.saturation : ((p_74296_1_ == Options.SENSITIVITY) ? this.mouseSensitivity : ((p_74296_1_ == Options.CHAT_OPACITY) ? this.chatOpacity : ((p_74296_1_ == Options.CHAT_HEIGHT_FOCUSED) ? this.chatHeightFocused : ((p_74296_1_ == Options.CHAT_HEIGHT_UNFOCUSED) ? this.chatHeightUnfocused : ((p_74296_1_ == Options.CHAT_SCALE) ? this.chatScale : ((p_74296_1_ == Options.CHAT_WIDTH) ? this.chatWidth : ((p_74296_1_ == Options.FRAMERATE_LIMIT) ? ((float)this.limitFramerate) : ((p_74296_1_ == Options.MIPMAP_LEVELS) ? ((float)this.mipmapLevels) : ((p_74296_1_ == Options.RENDER_DISTANCE) ? ((float)this.renderDistanceChunks) : ((p_74296_1_ == Options.STREAM_BYTES_PER_PIXEL) ? this.streamBytesPerPixel : ((p_74296_1_ == Options.STREAM_VOLUME_MIC) ? this.streamMicVolume : ((p_74296_1_ == Options.STREAM_VOLUME_SYSTEM) ? this.streamGameVolume : ((p_74296_1_ == Options.STREAM_KBPS) ? this.streamKbps : ((p_74296_1_ == Options.STREAM_FPS) ? this.streamFps : 0.0f))))))))))))))));
    }
    
    public boolean getOptionOrdinalValue(final Options p_74308_1_) {
        switch (p_74308_1_) {
            case INVERT_MOUSE: {
                return this.invertMouse;
            }
            case VIEW_BOBBING: {
                return this.viewBobbing;
            }
            case ANAGLYPH: {
                return this.anaglyph;
            }
            case FBO_ENABLE: {
                return this.fboEnable;
            }
            case CHAT_COLOR: {
                return this.chatColours;
            }
            case CHAT_LINKS: {
                return this.chatLinks;
            }
            case CHAT_LINKS_PROMPT: {
                return this.chatLinksPrompt;
            }
            case SNOOPER_ENABLED: {
                return this.snooperEnabled;
            }
            case USE_FULLSCREEN: {
                return this.fullScreen;
            }
            case ENABLE_VSYNC: {
                return this.enableVsync;
            }
            case USE_VBO: {
                return this.useVbo;
            }
            case TOUCHSCREEN: {
                return this.touchscreen;
            }
            case STREAM_SEND_METADATA: {
                return this.streamSendMetadata;
            }
            case FORCE_UNICODE_FONT: {
                return this.forceUnicodeFont;
            }
            case BLOCK_ALTERNATIVES: {
                return this.allowBlockAlternatives;
            }
            case REDUCED_DEBUG_INFO: {
                return this.reducedDebugInfo;
            }
            case ENTITY_SHADOWS: {
                return this.field_181151_V;
            }
            default: {
                return false;
            }
        }
    }
    
    private static String getTranslation(final String[] p_74299_0_, int p_74299_1_) {
        if (p_74299_1_ < 0 || p_74299_1_ >= p_74299_0_.length) {
            p_74299_1_ = 0;
        }
        return I18n.format(p_74299_0_[p_74299_1_], new Object[0]);
    }
    
    public String getKeyBinding(final Options p_74297_1_) {
        final String s = String.valueOf(I18n.format(p_74297_1_.getEnumString(), new Object[0])) + ": ";
        if (p_74297_1_.getEnumFloat()) {
            final float f1 = this.getOptionFloatValue(p_74297_1_);
            final float f2 = p_74297_1_.normalizeValue(f1);
            return (p_74297_1_ == Options.SENSITIVITY) ? ((f2 == 0.0f) ? (String.valueOf(s) + I18n.format("options.sensitivity.min", new Object[0])) : ((f2 == 1.0f) ? (String.valueOf(s) + I18n.format("options.sensitivity.max", new Object[0])) : (String.valueOf(s) + (int)(f2 * 200.0f) + "%"))) : ((p_74297_1_ == Options.FOV) ? ((f1 == 70.0f) ? (String.valueOf(s) + I18n.format("options.fov.min", new Object[0])) : ((f1 == 110.0f) ? (String.valueOf(s) + I18n.format("options.fov.max", new Object[0])) : (String.valueOf(s) + (int)f1))) : ((p_74297_1_ == Options.FRAMERATE_LIMIT) ? ((f1 == p_74297_1_.valueMax) ? (String.valueOf(s) + I18n.format("options.framerateLimit.max", new Object[0])) : (String.valueOf(s) + (int)f1 + " fps")) : ((p_74297_1_ == Options.RENDER_CLOUDS) ? ((f1 == p_74297_1_.valueMin) ? (String.valueOf(s) + I18n.format("options.cloudHeight.min", new Object[0])) : (String.valueOf(s) + ((int)f1 + 128))) : ((p_74297_1_ == Options.GAMMA) ? ((f2 == 0.0f) ? (String.valueOf(s) + I18n.format("options.gamma.min", new Object[0])) : ((f2 == 1.0f) ? (String.valueOf(s) + I18n.format("options.gamma.max", new Object[0])) : (String.valueOf(s) + "+" + (int)(f2 * 100.0f) + "%"))) : ((p_74297_1_ == Options.SATURATION) ? (String.valueOf(s) + (int)(f2 * 400.0f) + "%") : ((p_74297_1_ == Options.CHAT_OPACITY) ? (String.valueOf(s) + (int)(f2 * 90.0f + 10.0f) + "%") : ((p_74297_1_ == Options.CHAT_HEIGHT_UNFOCUSED) ? (String.valueOf(s) + GuiNewChat.calculateChatboxHeight(f2) + "px") : ((p_74297_1_ == Options.CHAT_HEIGHT_FOCUSED) ? (String.valueOf(s) + GuiNewChat.calculateChatboxHeight(f2) + "px") : ((p_74297_1_ == Options.CHAT_WIDTH) ? (String.valueOf(s) + GuiNewChat.calculateChatboxWidth(f2) + "px") : ((p_74297_1_ == Options.RENDER_DISTANCE) ? (String.valueOf(s) + (int)f1 + " chunks") : ((p_74297_1_ == Options.MIPMAP_LEVELS) ? ((f1 == 0.0f) ? (String.valueOf(s) + I18n.format("options.off", new Object[0])) : (String.valueOf(s) + (int)f1)) : ((p_74297_1_ == Options.STREAM_FPS) ? (String.valueOf(s) + TwitchStream.formatStreamFps(f2) + " fps") : ((p_74297_1_ == Options.STREAM_KBPS) ? (String.valueOf(s) + TwitchStream.formatStreamKbps(f2) + " Kbps") : ((p_74297_1_ == Options.STREAM_BYTES_PER_PIXEL) ? (String.valueOf(s) + String.format("%.3f bpp", TwitchStream.formatStreamBps(f2))) : ((f2 == 0.0f) ? (String.valueOf(s) + I18n.format("options.off", new Object[0])) : (String.valueOf(s) + (int)(f2 * 100.0f) + "%"))))))))))))))));
        }
        if (p_74297_1_.getEnumBoolean()) {
            final boolean flag = this.getOptionOrdinalValue(p_74297_1_);
            return flag ? (String.valueOf(s) + I18n.format("options.on", new Object[0])) : (String.valueOf(s) + I18n.format("options.off", new Object[0]));
        }
        if (p_74297_1_ == Options.GUI_SCALE) {
            return String.valueOf(s) + getTranslation(GameSettings.GUISCALES, this.guiScale);
        }
        if (p_74297_1_ == Options.CHAT_VISIBILITY) {
            return String.valueOf(s) + I18n.format(this.chatVisibility.getResourceKey(), new Object[0]);
        }
        if (p_74297_1_ == Options.PARTICLES) {
            return String.valueOf(s) + getTranslation(GameSettings.PARTICLES, this.particleSetting);
        }
        if (p_74297_1_ == Options.AMBIENT_OCCLUSION) {
            return String.valueOf(s) + getTranslation(GameSettings.AMBIENT_OCCLUSIONS, this.ambientOcclusion);
        }
        if (p_74297_1_ == Options.STREAM_COMPRESSION) {
            return String.valueOf(s) + getTranslation(GameSettings.STREAM_COMPRESSIONS, this.streamCompression);
        }
        if (p_74297_1_ == Options.STREAM_CHAT_ENABLED) {
            return String.valueOf(s) + getTranslation(GameSettings.STREAM_CHAT_MODES, this.streamChatEnabled);
        }
        if (p_74297_1_ == Options.STREAM_CHAT_USER_FILTER) {
            return String.valueOf(s) + getTranslation(GameSettings.STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
        }
        if (p_74297_1_ == Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            return String.valueOf(s) + getTranslation(GameSettings.STREAM_MIC_MODES, this.streamMicToggleBehavior);
        }
        if (p_74297_1_ == Options.RENDER_CLOUDS) {
            return String.valueOf(s) + getTranslation(GameSettings.field_181149_aW, this.clouds);
        }
        if (p_74297_1_ != Options.GRAPHICS) {
            return s;
        }
        if (this.fancyGraphics) {
            return String.valueOf(s) + I18n.format("options.graphics.fancy", new Object[0]);
        }
        final String s2 = "options.graphics.fast";
        return String.valueOf(s) + I18n.format("options.graphics.fast", new Object[0]);
    }
    
    public void loadOptions() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            final BufferedReader bufferedreader = new BufferedReader(new FileReader(this.optionsFile));
            String s = "";
            this.mapSoundLevels.clear();
            while ((s = bufferedreader.readLine()) != null) {
                try {
                    final String[] astring = s.split(":");
                    if (astring[0].equals("mouseSensitivity")) {
                        this.mouseSensitivity = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("fov")) {
                        this.fovSetting = this.parseFloat(astring[1]) * 40.0f + 70.0f;
                    }
                    if (astring[0].equals("gamma")) {
                        this.gammaSetting = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("saturation")) {
                        this.saturation = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("invertYMouse")) {
                        this.invertMouse = astring[1].equals("true");
                    }
                    if (astring[0].equals("renderDistance")) {
                        this.renderDistanceChunks = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("guiScale")) {
                        this.guiScale = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("particles")) {
                        this.particleSetting = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("bobView")) {
                        this.viewBobbing = astring[1].equals("true");
                    }
                    if (astring[0].equals("anaglyph3d")) {
                        this.anaglyph = astring[1].equals("true");
                    }
                    if (astring[0].equals("maxFps")) {
                        this.limitFramerate = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("fboEnable")) {
                        this.fboEnable = astring[1].equals("true");
                    }
                    if (astring[0].equals("difficulty")) {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(astring[1]));
                    }
                    if (astring[0].equals("fancyGraphics")) {
                        this.fancyGraphics = astring[1].equals("true");
                    }
                    if (astring[0].equals("ao")) {
                        if (astring[1].equals("true")) {
                            this.ambientOcclusion = 2;
                        }
                        else if (astring[1].equals("false")) {
                            this.ambientOcclusion = 0;
                        }
                        else {
                            this.ambientOcclusion = Integer.parseInt(astring[1]);
                        }
                    }
                    if (astring[0].equals("renderClouds")) {
                        if (astring[1].equals("true")) {
                            this.clouds = 2;
                        }
                        else if (astring[1].equals("false")) {
                            this.clouds = 0;
                        }
                        else if (astring[1].equals("fast")) {
                            this.clouds = 1;
                        }
                    }
                    if (astring[0].equals("resourcePacks")) {
                        this.resourcePacks = (List<String>)GameSettings.gson.fromJson(s.substring(s.indexOf(58) + 1), (Type)GameSettings.typeListString);
                        if (this.resourcePacks == null) {
                            this.resourcePacks = (List<String>)Lists.newArrayList();
                        }
                    }
                    if (astring[0].equals("incompatibleResourcePacks")) {
                        this.field_183018_l = (List<String>)GameSettings.gson.fromJson(s.substring(s.indexOf(58) + 1), (Type)GameSettings.typeListString);
                        if (this.field_183018_l == null) {
                            this.field_183018_l = (List<String>)Lists.newArrayList();
                        }
                    }
                    if (astring[0].equals("lastServer") && astring.length >= 2) {
                        this.lastServer = s.substring(s.indexOf(58) + 1);
                    }
                    if (astring[0].equals("lang") && astring.length >= 2) {
                        this.language = astring[1];
                    }
                    if (astring[0].equals("chatVisibility")) {
                        this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(astring[1]));
                    }
                    if (astring[0].equals("chatColors")) {
                        this.chatColours = astring[1].equals("true");
                    }
                    if (astring[0].equals("chatLinks")) {
                        this.chatLinks = astring[1].equals("true");
                    }
                    if (astring[0].equals("chatLinksPrompt")) {
                        this.chatLinksPrompt = astring[1].equals("true");
                    }
                    if (astring[0].equals("chatOpacity")) {
                        this.chatOpacity = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("snooperEnabled")) {
                        this.snooperEnabled = astring[1].equals("true");
                    }
                    if (astring[0].equals("fullscreen")) {
                        this.fullScreen = astring[1].equals("true");
                    }
                    if (astring[0].equals("enableVsync")) {
                        this.enableVsync = astring[1].equals("true");
                    }
                    if (astring[0].equals("useVbo")) {
                        this.useVbo = astring[1].equals("true");
                    }
                    if (astring[0].equals("hideServerAddress")) {
                        this.hideServerAddress = astring[1].equals("true");
                    }
                    if (astring[0].equals("advancedItemTooltips")) {
                        this.advancedItemTooltips = astring[1].equals("true");
                    }
                    if (astring[0].equals("pauseOnLostFocus")) {
                        this.pauseOnLostFocus = astring[1].equals("true");
                    }
                    if (astring[0].equals("touchscreen")) {
                        this.touchscreen = astring[1].equals("true");
                    }
                    if (astring[0].equals("overrideHeight")) {
                        this.overrideHeight = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("overrideWidth")) {
                        this.overrideWidth = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("heldItemTooltips")) {
                        this.heldItemTooltips = astring[1].equals("true");
                    }
                    if (astring[0].equals("chatHeightFocused")) {
                        this.chatHeightFocused = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("chatHeightUnfocused")) {
                        this.chatHeightUnfocused = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("chatScale")) {
                        this.chatScale = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("chatWidth")) {
                        this.chatWidth = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("showInventoryAchievementHint")) {
                        this.showInventoryAchievementHint = astring[1].equals("true");
                    }
                    if (astring[0].equals("mipmapLevels")) {
                        this.mipmapLevels = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("streamBytesPerPixel")) {
                        this.streamBytesPerPixel = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("streamMicVolume")) {
                        this.streamMicVolume = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("streamSystemVolume")) {
                        this.streamGameVolume = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("streamKbps")) {
                        this.streamKbps = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("streamFps")) {
                        this.streamFps = this.parseFloat(astring[1]);
                    }
                    if (astring[0].equals("streamCompression")) {
                        this.streamCompression = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("streamSendMetadata")) {
                        this.streamSendMetadata = astring[1].equals("true");
                    }
                    if (astring[0].equals("streamPreferredServer") && astring.length >= 2) {
                        this.streamPreferredServer = s.substring(s.indexOf(58) + 1);
                    }
                    if (astring[0].equals("streamChatEnabled")) {
                        this.streamChatEnabled = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("streamChatUserFilter")) {
                        this.streamChatUserFilter = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("streamMicToggleBehavior")) {
                        this.streamMicToggleBehavior = Integer.parseInt(astring[1]);
                    }
                    if (astring[0].equals("forceUnicodeFont")) {
                        this.forceUnicodeFont = astring[1].equals("true");
                    }
                    if (astring[0].equals("allowBlockAlternatives")) {
                        this.allowBlockAlternatives = astring[1].equals("true");
                    }
                    if (astring[0].equals("reducedDebugInfo")) {
                        this.reducedDebugInfo = astring[1].equals("true");
                    }
                    if (astring[0].equals("useNativeTransport")) {
                        this.field_181150_U = astring[1].equals("true");
                    }
                    if (astring[0].equals("entityShadows")) {
                        this.field_181151_V = astring[1].equals("true");
                    }
                    KeyBinding[] keyBindings;
                    for (int length = (keyBindings = this.keyBindings).length, i = 0; i < length; ++i) {
                        final KeyBinding keybinding = keyBindings[i];
                        if (astring[0].equals("key_" + keybinding.getKeyDescription())) {
                            keybinding.setKeyCode(Integer.parseInt(astring[1]));
                        }
                    }
                    SoundCategory[] values;
                    for (int length2 = (values = SoundCategory.values()).length, j = 0; j < length2; ++j) {
                        final SoundCategory soundcategory = values[j];
                        if (astring[0].equals("soundCategory_" + soundcategory.getCategoryName())) {
                            this.mapSoundLevels.put(soundcategory, this.parseFloat(astring[1]));
                        }
                    }
                    EnumPlayerModelParts[] values2;
                    for (int length3 = (values2 = EnumPlayerModelParts.values()).length, k = 0; k < length3; ++k) {
                        final EnumPlayerModelParts enumplayermodelparts = values2[k];
                        if (astring[0].equals("modelPart_" + enumplayermodelparts.getPartName())) {
                            this.setModelPartEnabled(enumplayermodelparts, astring[1].equals("true"));
                        }
                    }
                }
                catch (Exception var8) {
                    GameSettings.logger.warn("Skipping bad option: " + s);
                }
            }
            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedreader.close();
        }
        catch (Exception exception) {
            GameSettings.logger.error("Failed to load options", (Throwable)exception);
        }
    }
    
    private float parseFloat(final String p_74305_1_) {
        return p_74305_1_.equals("true") ? 1.0f : (p_74305_1_.equals("false") ? 0.0f : Float.parseFloat(p_74305_1_));
    }
    
    public void saveOptions() {
        try {
            final PrintWriter printwriter = new PrintWriter(new FileWriter(this.optionsFile));
            printwriter.println("invertYMouse:" + this.invertMouse);
            printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
            printwriter.println("fov:" + (this.fovSetting - 70.0f) / 40.0f);
            printwriter.println("gamma:" + this.gammaSetting);
            printwriter.println("saturation:" + this.saturation);
            printwriter.println("renderDistance:" + this.renderDistanceChunks);
            printwriter.println("guiScale:" + this.guiScale);
            printwriter.println("particles:" + this.particleSetting);
            printwriter.println("bobView:" + this.viewBobbing);
            printwriter.println("anaglyph3d:" + this.anaglyph);
            printwriter.println("maxFps:" + this.limitFramerate);
            printwriter.println("fboEnable:" + this.fboEnable);
            printwriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printwriter.println("fancyGraphics:" + this.fancyGraphics);
            printwriter.println("ao:" + this.ambientOcclusion);
            switch (this.clouds) {
                case 0: {
                    printwriter.println("renderClouds:false");
                    break;
                }
                case 1: {
                    printwriter.println("renderClouds:fast");
                    break;
                }
                case 2: {
                    printwriter.println("renderClouds:true");
                    break;
                }
            }
            printwriter.println("resourcePacks:" + GameSettings.gson.toJson((Object)this.resourcePacks));
            printwriter.println("incompatibleResourcePacks:" + GameSettings.gson.toJson((Object)this.field_183018_l));
            printwriter.println("lastServer:" + this.lastServer);
            printwriter.println("lang:" + this.language);
            printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
            printwriter.println("chatColors:" + this.chatColours);
            printwriter.println("chatLinks:" + this.chatLinks);
            printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
            printwriter.println("chatOpacity:" + this.chatOpacity);
            printwriter.println("snooperEnabled:" + this.snooperEnabled);
            printwriter.println("fullscreen:" + this.fullScreen);
            printwriter.println("enableVsync:" + this.enableVsync);
            printwriter.println("useVbo:" + this.useVbo);
            printwriter.println("hideServerAddress:" + this.hideServerAddress);
            printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printwriter.println("touchscreen:" + this.touchscreen);
            printwriter.println("overrideWidth:" + this.overrideWidth);
            printwriter.println("overrideHeight:" + this.overrideHeight);
            printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
            printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
            printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
            printwriter.println("chatScale:" + this.chatScale);
            printwriter.println("chatWidth:" + this.chatWidth);
            printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
            printwriter.println("mipmapLevels:" + this.mipmapLevels);
            printwriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
            printwriter.println("streamMicVolume:" + this.streamMicVolume);
            printwriter.println("streamSystemVolume:" + this.streamGameVolume);
            printwriter.println("streamKbps:" + this.streamKbps);
            printwriter.println("streamFps:" + this.streamFps);
            printwriter.println("streamCompression:" + this.streamCompression);
            printwriter.println("streamSendMetadata:" + this.streamSendMetadata);
            printwriter.println("streamPreferredServer:" + this.streamPreferredServer);
            printwriter.println("streamChatEnabled:" + this.streamChatEnabled);
            printwriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
            printwriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
            printwriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
            printwriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
            printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
            printwriter.println("useNativeTransport:" + this.field_181150_U);
            printwriter.println("entityShadows:" + this.field_181151_V);
            KeyBinding[] keyBindings;
            for (int length = (keyBindings = this.keyBindings).length, i = 0; i < length; ++i) {
                final KeyBinding keybinding = keyBindings[i];
                printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode());
            }
            SoundCategory[] values;
            for (int length2 = (values = SoundCategory.values()).length, j = 0; j < length2; ++j) {
                final SoundCategory soundcategory = values[j];
                printwriter.println("soundCategory_" + soundcategory.getCategoryName() + ":" + this.getSoundLevel(soundcategory));
            }
            EnumPlayerModelParts[] values2;
            for (int length3 = (values2 = EnumPlayerModelParts.values()).length, k = 0; k < length3; ++k) {
                final EnumPlayerModelParts enumplayermodelparts = values2[k];
                printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.setModelParts.contains(enumplayermodelparts));
            }
            printwriter.close();
        }
        catch (Exception exception) {
            GameSettings.logger.error("Failed to save options", (Throwable)exception);
        }
        this.sendSettingsToServer();
    }
    
    public float getSoundLevel(final SoundCategory p_151438_1_) {
        return this.mapSoundLevels.containsKey(p_151438_1_) ? this.mapSoundLevels.get(p_151438_1_) : 1.0f;
    }
    
    public void setSoundLevel(final SoundCategory p_151439_1_, final float p_151439_2_) {
        this.mc.getSoundHandler().setSoundLevel(p_151439_1_, p_151439_2_);
        this.mapSoundLevels.put(p_151439_1_, p_151439_2_);
    }
    
    public void sendSettingsToServer() {
        if (this.mc.thePlayer != null) {
            int i = 0;
            for (final EnumPlayerModelParts enumplayermodelparts : this.setModelParts) {
                i |= enumplayermodelparts.getPartMask();
            }
            this.mc.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i));
        }
    }
    
    public Set<EnumPlayerModelParts> getModelParts() {
        return (Set<EnumPlayerModelParts>)ImmutableSet.copyOf((Collection)this.setModelParts);
    }
    
    public void setModelPartEnabled(final EnumPlayerModelParts p_178878_1_, final boolean p_178878_2_) {
        if (p_178878_2_) {
            this.setModelParts.add(p_178878_1_);
        }
        else {
            this.setModelParts.remove(p_178878_1_);
        }
        this.sendSettingsToServer();
    }
    
    public void switchModelPartEnabled(final EnumPlayerModelParts p_178877_1_) {
        if (!this.getModelParts().contains(p_178877_1_)) {
            this.setModelParts.add(p_178877_1_);
        }
        else {
            this.setModelParts.remove(p_178877_1_);
        }
        this.sendSettingsToServer();
    }
    
    public int func_181147_e() {
        return (this.renderDistanceChunks >= 4) ? this.clouds : 0;
    }
    
    public boolean func_181148_f() {
        return this.field_181150_U;
    }
    
    public enum Options
    {
        INVERT_MOUSE("INVERT_MOUSE", 0, "options.invertMouse", false, true), 
        SENSITIVITY("SENSITIVITY", 1, "options.sensitivity", true, false), 
        FOV("FOV", 2, "options.fov", true, false, 30.0f, 110.0f, 1.0f), 
        GAMMA("GAMMA", 3, "options.gamma", true, false), 
        SATURATION("SATURATION", 4, "options.saturation", true, false), 
        RENDER_DISTANCE("RENDER_DISTANCE", 5, "options.renderDistance", true, false, 2.0f, 16.0f, 1.0f), 
        VIEW_BOBBING("VIEW_BOBBING", 6, "options.viewBobbing", false, true), 
        ANAGLYPH("ANAGLYPH", 7, "options.anaglyph", false, true), 
        FRAMERATE_LIMIT("FRAMERATE_LIMIT", 8, "options.framerateLimit", true, false, 10.0f, 260.0f, 10.0f), 
        FBO_ENABLE("FBO_ENABLE", 9, "options.fboEnable", false, true), 
        RENDER_CLOUDS("RENDER_CLOUDS", 10, "options.renderClouds", false, false), 
        GRAPHICS("GRAPHICS", 11, "options.graphics", false, false), 
        AMBIENT_OCCLUSION("AMBIENT_OCCLUSION", 12, "options.ao", false, false), 
        GUI_SCALE("GUI_SCALE", 13, "options.guiScale", false, false), 
        PARTICLES("PARTICLES", 14, "options.particles", false, false), 
        CHAT_VISIBILITY("CHAT_VISIBILITY", 15, "options.chat.visibility", false, false), 
        CHAT_COLOR("CHAT_COLOR", 16, "options.chat.color", false, true), 
        CHAT_LINKS("CHAT_LINKS", 17, "options.chat.links", false, true), 
        CHAT_OPACITY("CHAT_OPACITY", 18, "options.chat.opacity", true, false), 
        CHAT_LINKS_PROMPT("CHAT_LINKS_PROMPT", 19, "options.chat.links.prompt", false, true), 
        SNOOPER_ENABLED("SNOOPER_ENABLED", 20, "options.snooper", false, true), 
        USE_FULLSCREEN("USE_FULLSCREEN", 21, "options.fullscreen", false, true), 
        ENABLE_VSYNC("ENABLE_VSYNC", 22, "options.vsync", false, true), 
        USE_VBO("USE_VBO", 23, "options.vbo", false, true), 
        TOUCHSCREEN("TOUCHSCREEN", 24, "options.touchscreen", false, true), 
        CHAT_SCALE("CHAT_SCALE", 25, "options.chat.scale", true, false), 
        CHAT_WIDTH("CHAT_WIDTH", 26, "options.chat.width", true, false), 
        CHAT_HEIGHT_FOCUSED("CHAT_HEIGHT_FOCUSED", 27, "options.chat.height.focused", true, false), 
        CHAT_HEIGHT_UNFOCUSED("CHAT_HEIGHT_UNFOCUSED", 28, "options.chat.height.unfocused", true, false), 
        MIPMAP_LEVELS("MIPMAP_LEVELS", 29, "options.mipmapLevels", true, false, 0.0f, 4.0f, 1.0f), 
        FORCE_UNICODE_FONT("FORCE_UNICODE_FONT", 30, "options.forceUnicodeFont", false, true), 
        STREAM_BYTES_PER_PIXEL("STREAM_BYTES_PER_PIXEL", 31, "options.stream.bytesPerPixel", true, false), 
        STREAM_VOLUME_MIC("STREAM_VOLUME_MIC", 32, "options.stream.micVolumne", true, false), 
        STREAM_VOLUME_SYSTEM("STREAM_VOLUME_SYSTEM", 33, "options.stream.systemVolume", true, false), 
        STREAM_KBPS("STREAM_KBPS", 34, "options.stream.kbps", true, false), 
        STREAM_FPS("STREAM_FPS", 35, "options.stream.fps", true, false), 
        STREAM_COMPRESSION("STREAM_COMPRESSION", 36, "options.stream.compression", false, false), 
        STREAM_SEND_METADATA("STREAM_SEND_METADATA", 37, "options.stream.sendMetadata", false, true), 
        STREAM_CHAT_ENABLED("STREAM_CHAT_ENABLED", 38, "options.stream.chat.enabled", false, false), 
        STREAM_CHAT_USER_FILTER("STREAM_CHAT_USER_FILTER", 39, "options.stream.chat.userFilter", false, false), 
        STREAM_MIC_TOGGLE_BEHAVIOR("STREAM_MIC_TOGGLE_BEHAVIOR", 40, "options.stream.micToggleBehavior", false, false), 
        BLOCK_ALTERNATIVES("BLOCK_ALTERNATIVES", 41, "options.blockAlternatives", false, true), 
        REDUCED_DEBUG_INFO("REDUCED_DEBUG_INFO", 42, "options.reducedDebugInfo", false, true), 
        ENTITY_SHADOWS("ENTITY_SHADOWS", 43, "options.entityShadows", false, true);
        
        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final String enumString;
        private final float valueStep;
        private float valueMin;
        private float valueMax;
        
        public static Options getEnumOptions(final int p_74379_0_) {
            Options[] values;
            for (int length = (values = values()).length, i = 0; i < length; ++i) {
                final Options gamesettings$options = values[i];
                if (gamesettings$options.returnEnumOrdinal() == p_74379_0_) {
                    return gamesettings$options;
                }
            }
            return null;
        }
        
        private Options(final String s, final int n, final String p_i1015_3_, final boolean p_i1015_4_, final boolean p_i1015_5_) {
            this(s, n, p_i1015_3_, p_i1015_4_, p_i1015_5_, 0.0f, 1.0f, 0.0f);
        }
        
        private Options(final String s, final int n, final String p_i45004_3_, final boolean p_i45004_4_, final boolean p_i45004_5_, final float p_i45004_6_, final float p_i45004_7_, final float p_i45004_8_) {
            this.enumString = p_i45004_3_;
            this.enumFloat = p_i45004_4_;
            this.enumBoolean = p_i45004_5_;
            this.valueMin = p_i45004_6_;
            this.valueMax = p_i45004_7_;
            this.valueStep = p_i45004_8_;
        }
        
        public boolean getEnumFloat() {
            return this.enumFloat;
        }
        
        public boolean getEnumBoolean() {
            return this.enumBoolean;
        }
        
        public int returnEnumOrdinal() {
            return this.ordinal();
        }
        
        public String getEnumString() {
            return this.enumString;
        }
        
        public float getValueMax() {
            return this.valueMax;
        }
        
        public void setValueMax(final float p_148263_1_) {
            this.valueMax = p_148263_1_;
        }
        
        public float normalizeValue(final float p_148266_1_) {
            return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMin) / (this.valueMax - this.valueMin), 0.0f, 1.0f);
        }
        
        public float denormalizeValue(final float p_148262_1_) {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(p_148262_1_, 0.0f, 1.0f));
        }
        
        public float snapToStepClamp(float p_148268_1_) {
            p_148268_1_ = this.snapToStep(p_148268_1_);
            return MathHelper.clamp_float(p_148268_1_, this.valueMin, this.valueMax);
        }
        
        protected float snapToStep(float p_148264_1_) {
            if (this.valueStep > 0.0f) {
                p_148264_1_ = this.valueStep * Math.round(p_148264_1_ / this.valueStep);
            }
            return p_148264_1_;
        }
    }
}
