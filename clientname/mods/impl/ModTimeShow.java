package clientname.mods.impl;

import clientname.mods.*;
import clientname.gui.hud.*;
import clientname.*;
import java.text.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;

public class ModTimeShow extends ModDraggable
{
    @Override
    public int getWidth() {
        return ModTimeShow.font.getStringWidth("Time: AA:AA:AA AA ");
    }
    
    @Override
    public int getHeight() {
        return ModTimeShow.font.FONT_HEIGHT;
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        if (Client.ModTimeShow) {
            final String pattern = "hh:mm:ss a ";
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            final String time = simpleDateFormat.format(new Date());
            ModTimeShow.font.drawString(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "Time" + Client.KlammerFarbe + "] " + time, pos.getAbsoluteX() + 1, pos.getAbsoluteY() + 1, -1);
            GlStateManager.pushMatrix();
            Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(String.valueOf(Client.KlammerFarbe) + "[" + Client.ModFarbe + "Time" + Client.KlammerFarbe + "] " + time, (float)(pos.getAbsoluteX() + 1), (float)(pos.getAbsoluteY() + 1), -1);
            GlStateManager.popMatrix();
        }
    }
}
