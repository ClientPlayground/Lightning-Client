package net.minecraft.client.renderer.texture;

import com.google.common.collect.*;
import java.awt.image.*;
import net.minecraft.client.resources.data.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.*;
import net.minecraft.util.*;
import net.minecraft.crash.*;

public class TextureAtlasSprite
{
    private final String iconName;
    protected List<int[][]> framesTextureData;
    protected int[][] interpolatedFrameData;
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;
    private static String locationNameClock;
    private static String locationNameCompass;
    
    static {
        TextureAtlasSprite.locationNameClock = "builtin/clock";
        TextureAtlasSprite.locationNameCompass = "builtin/compass";
    }
    
    protected TextureAtlasSprite(final String spriteName) {
        this.framesTextureData = (List<int[][]>)Lists.newArrayList();
        this.iconName = spriteName;
    }
    
    protected static TextureAtlasSprite makeAtlasSprite(final ResourceLocation spriteResourceLocation) {
        final String s = spriteResourceLocation.toString();
        return TextureAtlasSprite.locationNameClock.equals(s) ? new TextureClock(s) : (TextureAtlasSprite.locationNameCompass.equals(s) ? new TextureCompass(s) : new TextureAtlasSprite(s));
    }
    
    public static void setLocationNameClock(final String clockName) {
        TextureAtlasSprite.locationNameClock = clockName;
    }
    
    public static void setLocationNameCompass(final String compassName) {
        TextureAtlasSprite.locationNameCompass = compassName;
    }
    
    public void initSprite(final int inX, final int inY, final int originInX, final int originInY, final boolean rotatedIn) {
        this.originX = originInX;
        this.originY = originInY;
        this.rotated = rotatedIn;
        final float f = (float)(0.009999999776482582 / inX);
        final float f2 = (float)(0.009999999776482582 / inY);
        this.minU = originInX / (float)inX + f;
        this.maxU = (originInX + this.width) / (float)inX - f;
        this.minV = originInY / (float)inY + f2;
        this.maxV = (originInY + this.height) / (float)inY - f2;
    }
    
    public void copyFrom(final TextureAtlasSprite atlasSpirit) {
        this.originX = atlasSpirit.originX;
        this.originY = atlasSpirit.originY;
        this.width = atlasSpirit.width;
        this.height = atlasSpirit.height;
        this.rotated = atlasSpirit.rotated;
        this.minU = atlasSpirit.minU;
        this.maxU = atlasSpirit.maxU;
        this.minV = atlasSpirit.minV;
        this.maxV = atlasSpirit.maxV;
    }
    
    public int getOriginX() {
        return this.originX;
    }
    
    public int getOriginY() {
        return this.originY;
    }
    
    public int getIconWidth() {
        return this.width;
    }
    
    public int getIconHeight() {
        return this.height;
    }
    
    public float getMinU() {
        return this.minU;
    }
    
    public float getMaxU() {
        return this.maxU;
    }
    
    public float getInterpolatedU(final double u) {
        final float f = this.maxU - this.minU;
        return this.minU + f * (float)u / 16.0f;
    }
    
    public float getMinV() {
        return this.minV;
    }
    
    public float getMaxV() {
        return this.maxV;
    }
    
    public float getInterpolatedV(final double v) {
        final float f = this.maxV - this.minV;
        return this.minV + f * ((float)v / 16.0f);
    }
    
    public String getIconName() {
        return this.iconName;
    }
    
    public void updateAnimation() {
        ++this.tickCounter;
        if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
            final int i = this.animationMetadata.getFrameIndex(this.frameCounter);
            final int j = (this.animationMetadata.getFrameCount() == 0) ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % j;
            this.tickCounter = 0;
            final int k = this.animationMetadata.getFrameIndex(this.frameCounter);
            if (i != k && k >= 0 && k < this.framesTextureData.size()) {
                TextureUtil.uploadTextureMipmap(this.framesTextureData.get(k), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
        else if (this.animationMetadata.isInterpolate()) {
            this.updateAnimationInterpolated();
        }
    }
    
    private void updateAnimationInterpolated() {
        final double d0 = 1.0 - this.tickCounter / (double)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        final int i = this.animationMetadata.getFrameIndex(this.frameCounter);
        final int j = (this.animationMetadata.getFrameCount() == 0) ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
        final int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);
        if (i != k && k >= 0 && k < this.framesTextureData.size()) {
            final int[][] aint = this.framesTextureData.get(i);
            final int[][] aint2 = this.framesTextureData.get(k);
            if (this.interpolatedFrameData == null || this.interpolatedFrameData.length != aint.length) {
                this.interpolatedFrameData = new int[aint.length][];
            }
            for (int l = 0; l < aint.length; ++l) {
                if (this.interpolatedFrameData[l] == null) {
                    this.interpolatedFrameData[l] = new int[aint[l].length];
                }
                if (l < aint2.length && aint2[l].length == aint[l].length) {
                    for (int i2 = 0; i2 < aint[l].length; ++i2) {
                        final int j2 = aint[l][i2];
                        final int k2 = aint2[l][i2];
                        final int l2 = (int)(((j2 & 0xFF0000) >> 16) * d0 + ((k2 & 0xFF0000) >> 16) * (1.0 - d0));
                        final int i3 = (int)(((j2 & 0xFF00) >> 8) * d0 + ((k2 & 0xFF00) >> 8) * (1.0 - d0));
                        final int j3 = (int)((j2 & 0xFF) * d0 + (k2 & 0xFF) * (1.0 - d0));
                        this.interpolatedFrameData[l][i2] = ((j2 & 0xFF000000) | l2 << 16 | i3 << 8 | j3);
                    }
                }
            }
            TextureUtil.uploadTextureMipmap(this.interpolatedFrameData, this.width, this.height, this.originX, this.originY, false, false);
        }
    }
    
    public int[][] getFrameTextureData(final int index) {
        return this.framesTextureData.get(index);
    }
    
    public int getFrameCount() {
        return this.framesTextureData.size();
    }
    
    public void setIconWidth(final int newWidth) {
        this.width = newWidth;
    }
    
    public void setIconHeight(final int newHeight) {
        this.height = newHeight;
    }
    
    public void loadSprite(final BufferedImage[] images, final AnimationMetadataSection meta) throws IOException {
        this.resetSprite();
        final int i = images[0].getWidth();
        final int j = images[0].getHeight();
        this.width = i;
        this.height = j;
        final int[][] aint = new int[images.length][];
        for (int k = 0; k < images.length; ++k) {
            final BufferedImage bufferedimage = images[k];
            if (bufferedimage != null) {
                if (k > 0 && (bufferedimage.getWidth() != i >> k || bufferedimage.getHeight() != j >> k)) {
                    throw new RuntimeException(String.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d", k, bufferedimage.getWidth(), bufferedimage.getHeight(), i >> k, j >> k));
                }
                aint[k] = new int[bufferedimage.getWidth() * bufferedimage.getHeight()];
                bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), aint[k], 0, bufferedimage.getWidth());
            }
        }
        if (meta == null) {
            if (j != i) {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }
            this.framesTextureData.add(aint);
        }
        else {
            final int j2 = j / i;
            final int k2 = i;
            final int l = i;
            this.height = this.width;
            if (meta.getFrameCount() > 0) {
                for (final int i2 : meta.getFrameIndexSet()) {
                    if (i2 >= j2) {
                        throw new RuntimeException("invalid frameindex " + i2);
                    }
                    this.allocateFrameTextureData(i2);
                    this.framesTextureData.set(i2, getFrameTextureData(aint, k2, l, i2));
                }
                this.animationMetadata = meta;
            }
            else {
                final List<AnimationFrame> list = (List<AnimationFrame>)Lists.newArrayList();
                for (int l2 = 0; l2 < j2; ++l2) {
                    this.framesTextureData.add(getFrameTextureData(aint, k2, l, l2));
                    list.add(new AnimationFrame(l2, -1));
                }
                this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, meta.getFrameTime(), meta.isInterpolate());
            }
        }
    }
    
    public void generateMipmaps(final int level) {
        final List<int[][]> list = (List<int[][]>)Lists.newArrayList();
        for (int i = 0; i < this.framesTextureData.size(); ++i) {
            final int[][] aint = this.framesTextureData.get(i);
            if (aint != null) {
                try {
                    list.add(TextureUtil.generateMipmapData(level, this.width, aint));
                }
                catch (Throwable throwable) {
                    final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
                    final CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
                    crashreportcategory.addCrashSection("Frame index", i);
                    crashreportcategory.addCrashSectionCallable("Frame sizes", new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            final StringBuilder stringbuilder = new StringBuilder();
                            int[][] val$aint;
                            for (int length = (val$aint = aint).length, i = 0; i < length; ++i) {
                                final int[] aint1 = val$aint[i];
                                if (stringbuilder.length() > 0) {
                                    stringbuilder.append(", ");
                                }
                                stringbuilder.append((aint1 == null) ? "null" : Integer.valueOf(aint1.length));
                            }
                            return stringbuilder.toString();
                        }
                    });
                    throw new ReportedException(crashreport);
                }
            }
        }
        this.setFramesTextureData(list);
    }
    
    private void allocateFrameTextureData(final int index) {
        if (this.framesTextureData.size() <= index) {
            for (int i = this.framesTextureData.size(); i <= index; ++i) {
                this.framesTextureData.add(null);
            }
        }
    }
    
    private static int[][] getFrameTextureData(final int[][] data, final int rows, final int columns, final int p_147962_3_) {
        final int[][] aint = new int[data.length][];
        for (int i = 0; i < data.length; ++i) {
            final int[] aint2 = data[i];
            if (aint2 != null) {
                aint[i] = new int[(rows >> i) * (columns >> i)];
                System.arraycopy(aint2, p_147962_3_ * aint[i].length, aint[i], 0, aint[i].length);
            }
        }
        return aint;
    }
    
    public void clearFramesTextureData() {
        this.framesTextureData.clear();
    }
    
    public boolean hasAnimationMetadata() {
        return this.animationMetadata != null;
    }
    
    public void setFramesTextureData(final List<int[][]> newFramesTextureData) {
        this.framesTextureData = newFramesTextureData;
    }
    
    private void resetSprite() {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
    }
    
    @Override
    public String toString() {
        return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }
}
