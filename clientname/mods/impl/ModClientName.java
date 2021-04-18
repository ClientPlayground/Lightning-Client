package clientname.mods.impl;

import clientname.mods.*;
import clientname.*;
import clientname.gui.hud.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;

public class ModClientName extends ModDraggable
{
    @Override
    public int getWidth() {
        return ModClientName.font.getStringWidth(String.valueOf(Client.ModFarbe) + Client.ClientName);
    }
    
    @Override
    public int getHeight() {
        return ModClientName.font.FONT_HEIGHT;
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        ModClientName.font.drawString(String.valueOf(Client.ModFarbe) + Client.ClientName, pos.getAbsoluteX() + 1, pos.getAbsoluteY() + 1, -1);
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.valueOf(Client.ModFarbe) + Client.ClientName, (float)pos.getAbsoluteX(), (float)pos.getAbsoluteY(), -1);
        GlStateManager.popMatrix();
    }
    
    @Override
    public void renderDummy(final ScreenPosition pos) {
        ModClientName.font.drawString(String.valueOf(Client.ModFarbe) + Client.ClientName, pos.getAbsoluteX() + 1, pos.getAbsoluteY() + 1, -7274752);
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.valueOf(Client.ModFarbe) + Client.ClientName, (float)pos.getAbsoluteX(), (float)pos.getAbsoluteY(), -1);
        GlStateManager.popMatrix();
    }
}
