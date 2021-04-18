package clientname.mods.impl;

import clientname.mods.*;
import net.minecraft.client.*;
import clientname.gui.hud.*;
import clientname.*;
import net.minecraft.client.renderer.*;

public class ModPing extends ModDraggable
{
    @Override
    public int getWidth() {
        return ModPing.font.getStringWidth("Ping: " + Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID()).getResponseTime());
    }
    
    @Override
    public int getHeight() {
        return ModPing.font.FONT_HEIGHT;
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        if (Client.ModPing) {
            ModPing.font.drawString(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "Ping" + Client.KlammerFarbe + "] " + Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID()).getResponseTime(), pos.getAbsoluteX() + 1, pos.getAbsoluteY() + 1, -1);
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "Ping" + Client.KlammerFarbe + "] " + Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID()).getResponseTime(), (float)(pos.getAbsoluteX() + 1), (float)(pos.getAbsoluteY() + 1), -1);
            GlStateManager.popMatrix();
        }
    }
}
