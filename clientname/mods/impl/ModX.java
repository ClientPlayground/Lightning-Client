package clientname.mods.impl;

import clientname.mods.*;
import clientname.gui.hud.*;
import clientname.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;

public class ModX extends ModDraggable
{
    @Override
    public int getWidth() {
        return 100;
    }
    
    @Override
    public int getHeight() {
        return 30;
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        if (Client.ModPosition) {
            ModX.font.drawString(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "X" + Client.KlammerFarbe + "] " + Math.round(Minecraft.getMinecraft().thePlayer.posX * 1000.0) / 1000L, pos.getAbsoluteX() + 2, pos.getAbsoluteY() + 2, -1);
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "X" + Client.KlammerFarbe + "] " + Math.round(Minecraft.getMinecraft().thePlayer.posX * 1000.0) / 1000L, (float)(pos.getAbsoluteX() + 2), (float)(pos.getAbsoluteY() + 2), -1);
            GlStateManager.popMatrix();
        }
    }
}
