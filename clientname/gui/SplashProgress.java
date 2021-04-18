package clientname.gui;

import net.minecraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.shader.*;
import net.minecraft.client.renderer.*;
import clientname.*;
import net.minecraft.client.gui.*;
import java.awt.*;

public class SplashProgress
{
    private static final int MAX = 7;
    private static int PROGRESS;
    private static String CURRENT;
    private static ResourceLocation splash;
    private static UnicodeFontRenderer ufr;
    
    static {
        SplashProgress.PROGRESS = 0;
        SplashProgress.CURRENT = "";
    }
    
    public static void update() {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getLanguageManager() == null) {
            return;
        }
        drawSplash(Minecraft.getMinecraft().getTextureManager());
    }
    
    public static void setProgress(final int givenProgress, final String givenText) {
        SplashProgress.PROGRESS = givenProgress;
        SplashProgress.CURRENT = givenText;
        update();
    }
    
    public static void drawSplash(final TextureManager tm) {
        final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        final int scaleFactor = scaledResolution.getScaleFactor();
        final Framebuffer framebuffer = new Framebuffer(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        if (SplashProgress.splash == null) {
            SplashProgress.splash = new ResourceLocation(Client.SplashScreen);
        }
        tm.bindTexture(SplashProgress.splash);
        GlStateManager.resetColor();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.drawScaledCustomSizeModalRect(0, 0, 0.0f, 0.0f, 1920, 1080, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 1920.0f, 1080.0f);
        drawProgress();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(352, 0.1f);
        Minecraft.getMinecraft().updateDisplay();
    }
    
    private static void drawProgress() {
        if (Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null) {
            return;
        }
        if (SplashProgress.ufr == null) {
            SplashProgress.ufr = UnicodeFontRenderer.getFontOnPC("Arial", 20);
        }
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final double nProgress = SplashProgress.PROGRESS;
        final double calc = nProgress / 7.0 * sr.getScaledWidth();
        Gui.drawRect(0, sr.getScaledHeight() - 35, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 50).getRGB());
        GlStateManager.resetColor();
        resetTextureState();
        SplashProgress.ufr.drawString(SplashProgress.CURRENT, 20.0f, (float)(sr.getScaledHeight() - 25), -1);
        final String step = String.valueOf(SplashProgress.PROGRESS) + "/" + 7;
        SplashProgress.ufr.drawString(step, (float)(sr.getScaledWidth() - 20 - SplashProgress.ufr.getStringWidth(step)), (float)(sr.getScaledHeight() - 25), -505290241);
        GlStateManager.resetColor();
        resetTextureState();
        Gui.drawRect(0, sr.getScaledHeight() - 2, (int)calc, sr.getScaledHeight(), new Color(255, 0, 255).getRGB());
        Gui.drawRect(0, sr.getScaledHeight() - 2, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 10).getRGB());
    }
    
    private static void resetTextureState() {
        GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = -1;
    }
}
