package clientname.mods.impl;

import clientname.mods.*;
import clientname.gui.hud.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.vertex.*;
import clientname.*;
import net.minecraft.client.renderer.*;
import net.minecraft.potion.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import net.minecraft.client.resources.*;
import java.util.*;

public class ModPotionStatus extends ModDraggable
{
    private ScreenPosition pos;
    protected FontRenderer fontRendererObj;
    protected float zLevelFloat;
    
    public ModPotionStatus() {
        this.pos = ScreenPosition.fromRelativePosition(0.5, 0.5);
    }
    
    @Override
    public int getWidth() {
        return 101;
    }
    
    @Override
    public int getHeight() {
        return 154;
    }
    
    public void drawTexturedModalRect(final int x, final int y, final int textureX, final int textureY, final int width, final int height) {
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x + 0, y + height, this.zLevelFloat).tex((textureX + 0) * f, (textureY + height) * f2).endVertex();
        worldrenderer.pos(x + width, y + height, this.zLevelFloat).tex((textureX + width) * f, (textureY + height) * f2).endVertex();
        worldrenderer.pos(x + width, y + 0, this.zLevelFloat).tex((textureX + width) * f, (textureY + 0) * f2).endVertex();
        worldrenderer.pos(x + 0, y + 0, this.zLevelFloat).tex((textureX + 0) * f, (textureY + 0) * f2).endVertex();
        tessellator.draw();
    }
    
    @Override
    public void render(final ScreenPosition pos) {
        if (Client.ModPotionstatus) {
            final int offsetX = 21;
            final int offsetY = 14;
            final int i = 80;
            int i2 = 16;
            final Collection<PotionEffect> collection = this.mc.thePlayer.getActivePotionEffects();
            if (!collection.isEmpty()) {
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableLighting();
                int l = 33;
                if (collection.size() > 5) {
                    l = 132 / (collection.size() - 1);
                }
                for (final PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects()) {
                    final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    if (potion.hasStatusIcon()) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        final int i3 = potion.getStatusIconIndex();
                        this.drawTexturedModalRect(pos.getAbsoluteX() + offsetX - 20, pos.getAbsoluteY() + i2 - offsetY, 0 + i3 % 8 * 18, 198 + i3 / 8 * 18, 18, 18);
                    }
                    String s1 = I18n.format(potion.getName(), new Object[0]);
                    if (potioneffect.getAmplifier() == 1) {
                        s1 = String.valueOf(s1) + " " + I18n.format("enchantment.level.2", new Object[0]);
                    }
                    else if (potioneffect.getAmplifier() == 2) {
                        s1 = String.valueOf(s1) + " " + I18n.format("enchantment.level.3", new Object[0]);
                    }
                    else if (potioneffect.getAmplifier() == 3) {
                        s1 = String.valueOf(s1) + " " + I18n.format("enchantment.level.4", new Object[0]);
                    }
                    ModPotionStatus.font.drawString(s1, (float)(pos.getAbsoluteX() + offsetX), (float)(pos.getAbsoluteY() + i2 - offsetY), 16777215, true);
                    final String s2 = Potion.getDurationString(potioneffect);
                    ModPotionStatus.font.drawString(s2, (float)(pos.getAbsoluteX() + offsetX), (float)(pos.getAbsoluteY() + i2 + 10 - offsetY), 8355711, true);
                    i2 += l;
                }
            }
        }
    }
}
