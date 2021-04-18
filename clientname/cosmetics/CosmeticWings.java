package clientname.cosmetics;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.entity.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import clientname.*;
import net.minecraft.entity.*;
import clientname.cosmetics.util.*;
import net.minecraft.client.model.*;
import net.minecraft.client.*;
import net.minecraft.util.*;

public class CosmeticWings extends CosmeticBase
{
    private final CosmeticWingsModel wingsModel;
    
    public CosmeticWings(final RenderPlayer renderPlayer) {
        super(renderPlayer);
        this.wingsModel = new CosmeticWingsModel(renderPlayer);
    }
    
    @Override
    public void render(final AbstractClientPlayer player, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
        GL11.glPushMatrix();
        if (player.isSneaking()) {
            GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.translate(0.0, 0.2, -0.05);
        }
        if (Client.CosmeticWings) {
            this.wingsModel.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GL11.glPopMatrix();
    }
    
    public class CosmeticWingsModel extends CosmeticModelBase
    {
        private ModelRenderer wing;
        private ModelRenderer wingTip;
        
        public CosmeticWingsModel(final RenderPlayer player) {
            super(player);
            this.setTextureOffset("wing.bone", 0, 0);
            this.setTextureOffset("wing.skin", -10, 8);
            this.setTextureOffset("wingtip.bone", 0, 5);
            this.setTextureOffset("wingtip.skin", -10, 18);
            (this.wing = new ModelRenderer(this, "wing")).setTextureSize(30, 30);
            this.wing.setRotationPoint(-2.0f, 0.0f, 0.0f);
            this.wing.addBox("bone", -10.0f, -1.0f, -1.0f, 10, 2, 2);
            this.wing.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10);
            (this.wingTip = new ModelRenderer(this, "wingtip")).setTextureSize(30, 30);
            this.wingTip.setRotationPoint(-10.0f, 0.0f, 0.0f);
            this.wingTip.addBox("bone", -10.0f, -0.5f, -0.5f, 10, 1, 1);
            this.wingTip.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10);
            this.wing.addChild(this.wingTip);
        }
        
        @Override
        public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float headYaw, final float headPitch, final float scale) {
            if (entityIn.getName().equals(Minecraft.getMinecraft().getSession().getUsername())) {
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.scale(0.9, 0.9, 0.9);
                GlStateManager.rotate(20.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translate(0.0, 0.0, 0.09);
                GlStateManager.translate(0.0, 0.2, 0.0);
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("wings.png"));
                for (int j = 0; j < 2; ++j) {
                    final float f11 = System.currentTimeMillis() % 1000L / 1000.0f * 3.1415927f * 2.0f;
                    this.wing.rotateAngleX = (float)Math.toRadians(-80.0) - (float)Math.cos(f11) * 0.105f;
                    this.wing.rotateAngleY = (float)Math.toRadians(5.0) + (float)Math.sin(f11) * 0.105f;
                    this.wing.rotateAngleZ = (float)Math.toRadians(5.0);
                    this.wingTip.rotateAngleZ = -(float)(Math.sin(f11 + 2.0f) + 0.9) * 0.75f;
                    this.wing.render(0.06f);
                    GlStateManager.scale(-1.0f, 1.0f, 1.0f);
                    if (j == 0) {}
                }
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }
    }
}
