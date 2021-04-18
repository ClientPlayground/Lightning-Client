package clientname.mods;

import net.minecraft.client.*;
import clientname.mods.impl.*;
import clientname.gui.hud.*;

public class ModInstances
{
    public static Minecraft mc;
    private static ModClientName modHelloWorld;
    private static ModArmorStatus modArmorStatus;
    private static ModFPS modFPS;
    private static ModToggleSpirnt modToggleSprint;
    private static ModKeystrokes modKeystrokes;
    private static ModPing modPing;
    private static ModTimeShow modTimeShow;
    private static ModPotionStatus modPotionStatus;
    private static ModX modX;
    private static ModY modY;
    private static ModZ modZ;
    private static ModPerspective modPerspective;
    
    public static void register(final HUDManager api) {
        ModInstances.modHelloWorld = new ModClientName();
        api.register(ModInstances.modHelloWorld);
        ModInstances.modArmorStatus = new ModArmorStatus();
        api.register(ModInstances.modArmorStatus);
        ModInstances.modToggleSprint = new ModToggleSpirnt();
        api.register(ModInstances.modToggleSprint);
        ModInstances.modFPS = new ModFPS();
        api.register(ModInstances.modFPS);
        ModInstances.modX = new ModX();
        api.register(ModInstances.modX);
        ModInstances.modY = new ModY();
        api.register(ModInstances.modY);
        ModInstances.modZ = new ModZ();
        api.register(ModInstances.modZ);
        ModInstances.modKeystrokes = new ModKeystrokes();
        api.register(ModInstances.modKeystrokes);
        ModInstances.modPing = new ModPing();
        api.register(ModInstances.modPing);
        ModInstances.modTimeShow = new ModTimeShow();
        api.register(ModInstances.modTimeShow);
        ModInstances.modPotionStatus = new ModPotionStatus();
        api.register(ModInstances.modPotionStatus);
        ModInstances.modPerspective = new ModPerspective();
        api.register(ModInstances.modPerspective);
    }
    
    public static ModPerspective getModPerspective() {
        return ModInstances.modPerspective;
    }
}
