package clientname.mods;

import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import clientname.*;
import clientname.event.*;

public class Mod
{
    private boolean isEnabled;
    protected final Minecraft mc;
    protected static FontRenderer font;
    protected final Client client;
    
    public Mod() {
        this.isEnabled = true;
        this.mc = Minecraft.getMinecraft();
        Mod.font = this.mc.fontRendererObj;
        this.client = Client.getInstance();
        this.setEnabled(this.isEnabled);
    }
    
    private void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
        if (isEnabled) {
            EventManager.register(this);
        }
        else {
            EventManager.unregister(this);
        }
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
