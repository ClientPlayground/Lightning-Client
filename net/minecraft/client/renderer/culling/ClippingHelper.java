package net.minecraft.client.renderer.culling;

public class ClippingHelper
{
    public float[][] frustum;
    public float[] projectionMatrix;
    public float[] modelviewMatrix;
    public float[] clippingMatrix;
    
    public ClippingHelper() {
        this.frustum = new float[6][4];
        this.projectionMatrix = new float[16];
        this.modelviewMatrix = new float[16];
        this.clippingMatrix = new float[16];
    }
    
    private double dot(final float[] p_178624_1_, final double p_178624_2_, final double p_178624_4_, final double p_178624_6_) {
        return p_178624_1_[0] * p_178624_2_ + p_178624_1_[1] * p_178624_4_ + p_178624_1_[2] * p_178624_6_ + p_178624_1_[3];
    }
    
    public boolean isBoxInFrustum(final double p_78553_1_, final double p_78553_3_, final double p_78553_5_, final double p_78553_7_, final double p_78553_9_, final double p_78553_11_) {
        for (int i = 0; i < 6; ++i) {
            final float[] afloat = this.frustum[i];
            if (this.dot(afloat, p_78553_1_, p_78553_3_, p_78553_5_) <= 0.0 && this.dot(afloat, p_78553_7_, p_78553_3_, p_78553_5_) <= 0.0 && this.dot(afloat, p_78553_1_, p_78553_9_, p_78553_5_) <= 0.0 && this.dot(afloat, p_78553_7_, p_78553_9_, p_78553_5_) <= 0.0 && this.dot(afloat, p_78553_1_, p_78553_3_, p_78553_11_) <= 0.0 && this.dot(afloat, p_78553_7_, p_78553_3_, p_78553_11_) <= 0.0 && this.dot(afloat, p_78553_1_, p_78553_9_, p_78553_11_) <= 0.0 && this.dot(afloat, p_78553_7_, p_78553_9_, p_78553_11_) <= 0.0) {
                return false;
            }
        }
        return true;
    }
}
