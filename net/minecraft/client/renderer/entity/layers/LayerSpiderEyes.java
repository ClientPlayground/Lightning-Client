package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.monster.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class LayerSpiderEyes implements LayerRenderer<EntitySpider>
{
    private static final ResourceLocation SPIDER_EYES;
    private final RenderSpider spiderRenderer;
    
    static {
        SPIDER_EYES = new ResourceLocation("textures/entity/spider_eyes.png");
    }
    
    public LayerSpiderEyes(final RenderSpider spiderRendererIn) {
        this.spiderRenderer = spiderRendererIn;
    }
    
    @Override
    public void doRenderLayer(final EntitySpider entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        this.spiderRenderer.bindTexture(LayerSpiderEyes.SPIDER_EYES);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(1, 1);
        if (entitylivingbaseIn.isInvisible()) {
            GlStateManager.depthMask(false);
        }
        else {
            GlStateManager.depthMask(true);
        }
        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.spiderRenderer.getMainModel().render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        i = entitylivingbaseIn.getBrightnessForRender(partialTicks);
        j = i % 65536;
        k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
        this.spiderRenderer.func_177105_a(entitylivingbaseIn, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
