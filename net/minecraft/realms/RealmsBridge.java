package net.minecraft.realms;

import net.minecraft.client.gui.*;
import org.apache.logging.log4j.*;
import net.minecraft.client.*;
import java.lang.reflect.*;

public class RealmsBridge extends RealmsScreen
{
    private static final Logger LOGGER;
    private GuiScreen previousScreen;
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public void switchToRealms(final GuiScreen p_switchToRealms_1_) {
        this.previousScreen = p_switchToRealms_1_;
        try {
            final Class<?> oclass = Class.forName("com.mojang.realmsclient.RealmsMainScreen");
            final Constructor<?> constructor = oclass.getDeclaredConstructor(RealmsScreen.class);
            constructor.setAccessible(true);
            final Object object = constructor.newInstance(this);
            Minecraft.getMinecraft().displayGuiScreen(((RealmsScreen)object).getProxy());
        }
        catch (Exception exception) {
            RealmsBridge.LOGGER.error("Realms module missing", (Throwable)exception);
        }
    }
    
    @Override
    public void init() {
        Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
    }
}
