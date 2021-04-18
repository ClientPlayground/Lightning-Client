package clientname;

import clientname.gui.hud.*;
import clientname.gui.*;
import clientname.mods.*;
import clientname.event.impl.*;
import net.minecraft.client.*;
import clientname.event.*;

public class Client
{
    private static final Client INSTANCE;
    public static String ClientName;
    public static String KlammerFarbe;
    public static String ModFarbe;
    public static String Background;
    public static String Cape;
    public static String DiscordID;
    public static String SplashScreen;
    public static String Logo;
    public static int ButtonHover;
    public static boolean ModFullbright;
    public static boolean ToggleSprint;
    public static boolean CosmeticWings;
    public static boolean CosmeticHalo;
    public static boolean CosmeticCape;
    public static boolean ModFPS;
    public static boolean ModPing;
    public static boolean ModPotionstatus;
    public static boolean ModTimeShow;
    public static boolean ModPosition;
    public static boolean ModArmorStatus;
    public static boolean ModKeystrokes;
    private DiscordRP discordRP;
    private HUDManager hudManager;
    int scrollTotal;
    private static boolean prevIsKeyDown;
    private static float savedFOV;
    
    static {
        INSTANCE = new Client();
        Client.ClientName = "Lightning Client";
        Client.KlammerFarbe = "§f";
        Client.ModFarbe = "§d";
        Client.Background = "background.png";
        Client.Cape = "PogChampp.png";
        Client.DiscordID = "804323807463735347";
        Client.SplashScreen = "background.png";
        Client.Logo = "Logo.jpeg";
        Client.ButtonHover = 16711935;
        Client.ModFPS = true;
        Client.ModPing = true;
        Client.ModPotionstatus = true;
        Client.ModTimeShow = true;
        Client.ModPosition = true;
        Client.ModArmorStatus = true;
        Client.ModKeystrokes = true;
        Client.prevIsKeyDown = false;
        Client.savedFOV = 0.0f;
    }
    
    public Client() {
        this.discordRP = new DiscordRP();
        this.scrollTotal = 4;
    }
    
    public static final Client getInstance() {
        return Client.INSTANCE;
    }
    
    public void init() {
        SplashProgress.setProgress(1, String.valueOf(Client.ClientName) + " - Discord Initialisation");
        FileManager.init();
        this.discordRP.start();
        EventManager.register(this);
    }
    
    public void start() {
        ModInstances.register(this.hudManager = HUDManager.getInstance());
    }
    
    public void shutdown() {
        this.discordRP.shutdown();
    }
    
    public DiscordRP getDiscordRP() {
        return this.discordRP;
    }
    
    @EventTarget
    public void onTick(final ClientTickEvent e) {
        if (Minecraft.getMinecraft().gameSettings.CLIENT_GUI_MOD_POS.isPressed()) {
            this.hudManager.openConfigScreen();
        }
        final boolean isKeyDown = Minecraft.getMinecraft().gameSettings.ZOOM.isKeyDown();
        if (Client.prevIsKeyDown != isKeyDown) {
            if (isKeyDown) {
                Client.savedFOV = Minecraft.getMinecraft().gameSettings.fovSetting;
                Minecraft.getMinecraft().gameSettings.fovSetting = 30.0f;
                Minecraft.getMinecraft().gameSettings.smoothCamera = true;
            }
            else {
                Minecraft.getMinecraft().gameSettings.fovSetting = Client.savedFOV;
                Minecraft.getMinecraft().gameSettings.smoothCamera = false;
            }
        }
        Client.prevIsKeyDown = isKeyDown;
    }
}
