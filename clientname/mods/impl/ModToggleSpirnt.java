package clientname.mods.impl;

import clientname.mods.*;
import clientname.gui.hud.*;
import clientname.*;

public class ModToggleSpirnt extends ModDraggable
{
    @Override
    public int getWidth() {
        return ModToggleSpirnt.font.getStringWidth("[Sprinting Toggled]");
    }
    
    @Override
    public int getHeight() {
        return ModToggleSpirnt.font.FONT_HEIGHT;
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        if (Client.ToggleSprint) {
            this.mc.fontRendererObj.drawStringWithShadow(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "Sprinting Toggled" + Client.KlammerFarbe + "]", (float)pos.getAbsoluteX(), (float)pos.getAbsoluteY(), -1);
        }
    }
}
