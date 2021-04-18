package net.minecraft.client.renderer.texture;

import org.apache.logging.log4j.*;
import com.google.common.collect.*;
import java.io.*;
import net.minecraft.client.*;
import java.awt.image.*;
import net.minecraft.client.resources.data.*;
import java.util.concurrent.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.*;
import java.util.*;
import net.minecraft.client.resources.*;
import net.minecraft.crash.*;

public class TextureMap extends AbstractTexture implements ITickableTextureObject
{
    private static final Logger logger;
    public static final ResourceLocation LOCATION_MISSING_TEXTURE;
    public static final ResourceLocation locationBlocksTexture;
    private final List<TextureAtlasSprite> listAnimatedSprites;
    private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
    private final Map<String, TextureAtlasSprite> mapUploadedSprites;
    private final String basePath;
    private final IIconCreator iconCreator;
    private int mipmapLevels;
    private final TextureAtlasSprite missingImage;
    
    static {
        logger = LogManager.getLogger();
        LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
        locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
    }
    
    public TextureMap(final String p_i46099_1_) {
        this(p_i46099_1_, null);
    }
    
    public TextureMap(final String p_i46100_1_, final IIconCreator iconCreatorIn) {
        this.listAnimatedSprites = (List<TextureAtlasSprite>)Lists.newArrayList();
        this.mapRegisteredSprites = (Map<String, TextureAtlasSprite>)Maps.newHashMap();
        this.mapUploadedSprites = (Map<String, TextureAtlasSprite>)Maps.newHashMap();
        this.missingImage = new TextureAtlasSprite("missingno");
        this.basePath = p_i46100_1_;
        this.iconCreator = iconCreatorIn;
    }
    
    private void initMissingImage() {
        final int[] aint = TextureUtil.missingTextureData;
        this.missingImage.setIconWidth(16);
        this.missingImage.setIconHeight(16);
        final int[][] aint2 = new int[this.mipmapLevels + 1][];
        aint2[0] = aint;
        this.missingImage.setFramesTextureData(Lists.newArrayList((Object[])new int[][][] { aint2 }));
    }
    
    @Override
    public void loadTexture(final IResourceManager resourceManager) throws IOException {
        if (this.iconCreator != null) {
            this.loadSprites(resourceManager, this.iconCreator);
        }
    }
    
    public void loadSprites(final IResourceManager resourceManager, final IIconCreator p_174943_2_) {
        this.mapRegisteredSprites.clear();
        p_174943_2_.registerSprites(this);
        this.initMissingImage();
        this.deleteGlTexture();
        this.loadTextureAtlas(resourceManager);
    }
    
    public void loadTextureAtlas(final IResourceManager resourceManager) {
        final int i = Minecraft.getGLMaximumTextureSize();
        final Stitcher stitcher = new Stitcher(i, i, true, 0, this.mipmapLevels);
        this.mapUploadedSprites.clear();
        this.listAnimatedSprites.clear();
        int j = Integer.MAX_VALUE;
        int k = 1 << this.mipmapLevels;
        for (final Map.Entry<String, TextureAtlasSprite> entry : this.mapRegisteredSprites.entrySet()) {
            final TextureAtlasSprite textureatlassprite = entry.getValue();
            final ResourceLocation resourcelocation = new ResourceLocation(textureatlassprite.getIconName());
            final ResourceLocation resourcelocation2 = this.completeResourceLocation(resourcelocation, 0);
            try {
                final IResource iresource = resourceManager.getResource(resourcelocation2);
                final BufferedImage[] abufferedimage = new BufferedImage[1 + this.mipmapLevels];
                abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());
                final TextureMetadataSection texturemetadatasection = iresource.getMetadata("texture");
                if (texturemetadatasection != null) {
                    final List<Integer> list = texturemetadatasection.getListMipmaps();
                    if (!list.isEmpty()) {
                        final int l = abufferedimage[0].getWidth();
                        final int i2 = abufferedimage[0].getHeight();
                        if (MathHelper.roundUpToPowerOfTwo(l) != l || MathHelper.roundUpToPowerOfTwo(i2) != i2) {
                            throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                        }
                    }
                    for (final int i3 : list) {
                        if (i3 > 0 && i3 < abufferedimage.length - 1 && abufferedimage[i3] == null) {
                            final ResourceLocation resourcelocation3 = this.completeResourceLocation(resourcelocation, i3);
                            try {
                                abufferedimage[i3] = TextureUtil.readBufferedImage(resourceManager.getResource(resourcelocation3).getInputStream());
                            }
                            catch (IOException ioexception) {
                                TextureMap.logger.error("Unable to load miplevel {} from: {}", new Object[] { i3, resourcelocation3, ioexception });
                            }
                        }
                    }
                }
                final AnimationMetadataSection animationmetadatasection = iresource.getMetadata("animation");
                textureatlassprite.loadSprite(abufferedimage, animationmetadatasection);
            }
            catch (RuntimeException runtimeexception) {
                TextureMap.logger.error("Unable to parse metadata from " + resourcelocation2, (Throwable)runtimeexception);
                continue;
            }
            catch (IOException ioexception2) {
                TextureMap.logger.error("Using missing texture, unable to load " + resourcelocation2, (Throwable)ioexception2);
                continue;
            }
            j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
            final int l2 = Math.min(Integer.lowestOneBit(textureatlassprite.getIconWidth()), Integer.lowestOneBit(textureatlassprite.getIconHeight()));
            if (l2 < k) {
                TextureMap.logger.warn("Texture {} with size {}x{} limits mip level from {} to {}", new Object[] { resourcelocation2, textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight(), MathHelper.calculateLogBaseTwo(k), MathHelper.calculateLogBaseTwo(l2) });
                k = l2;
            }
            stitcher.addSprite(textureatlassprite);
        }
        final int j2 = Math.min(j, k);
        final int k2 = MathHelper.calculateLogBaseTwo(j2);
        if (k2 < this.mipmapLevels) {
            TextureMap.logger.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", new Object[] { this.basePath, this.mipmapLevels, k2, j2 });
            this.mipmapLevels = k2;
        }
        for (final TextureAtlasSprite textureatlassprite2 : this.mapRegisteredSprites.values()) {
            try {
                textureatlassprite2.generateMipmaps(this.mipmapLevels);
            }
            catch (Throwable throwable1) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
                crashreportcategory.addCrashSectionCallable("Sprite name", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return textureatlassprite2.getIconName();
                    }
                });
                crashreportcategory.addCrashSectionCallable("Sprite size", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return String.valueOf(textureatlassprite2.getIconWidth()) + " x " + textureatlassprite2.getIconHeight();
                    }
                });
                crashreportcategory.addCrashSectionCallable("Sprite frames", new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return String.valueOf(textureatlassprite2.getFrameCount()) + " frames";
                    }
                });
                crashreportcategory.addCrashSection("Mipmap levels", this.mipmapLevels);
                throw new ReportedException(crashreport);
            }
        }
        this.missingImage.generateMipmaps(this.mipmapLevels);
        stitcher.addSprite(this.missingImage);
        try {
            stitcher.doStitch();
        }
        catch (StitcherException stitcherexception) {
            throw stitcherexception;
        }
        TextureMap.logger.info("Created: {}x{} {}-atlas", new Object[] { stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), this.basePath });
        TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(), stitcher.getCurrentHeight());
        final Map<String, TextureAtlasSprite> map = (Map<String, TextureAtlasSprite>)Maps.newHashMap((Map)this.mapRegisteredSprites);
        for (final TextureAtlasSprite textureatlassprite3 : stitcher.getStichSlots()) {
            final String s = textureatlassprite3.getIconName();
            map.remove(s);
            this.mapUploadedSprites.put(s, textureatlassprite3);
            try {
                TextureUtil.uploadTextureMipmap(textureatlassprite3.getFrameTextureData(0), textureatlassprite3.getIconWidth(), textureatlassprite3.getIconHeight(), textureatlassprite3.getOriginX(), textureatlassprite3.getOriginY(), false, false);
            }
            catch (Throwable throwable2) {
                final CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Stitching texture atlas");
                final CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Texture being stitched together");
                crashreportcategory2.addCrashSection("Atlas path", this.basePath);
                crashreportcategory2.addCrashSection("Sprite", textureatlassprite3);
                throw new ReportedException(crashreport2);
            }
            if (textureatlassprite3.hasAnimationMetadata()) {
                this.listAnimatedSprites.add(textureatlassprite3);
            }
        }
        for (final TextureAtlasSprite textureatlassprite4 : map.values()) {
            textureatlassprite4.copyFrom(this.missingImage);
        }
    }
    
    private ResourceLocation completeResourceLocation(final ResourceLocation location, final int p_147634_2_) {
        return (p_147634_2_ == 0) ? new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", this.basePath, location.getResourcePath(), ".png")) : new ResourceLocation(location.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", this.basePath, location.getResourcePath(), p_147634_2_, ".png"));
    }
    
    public TextureAtlasSprite getAtlasSprite(final String iconName) {
        TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);
        if (textureatlassprite == null) {
            textureatlassprite = this.missingImage;
        }
        return textureatlassprite;
    }
    
    public void updateAnimations() {
        TextureUtil.bindTexture(this.getGlTextureId());
        for (final TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
            textureatlassprite.updateAnimation();
        }
    }
    
    public TextureAtlasSprite registerSprite(final ResourceLocation location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null!");
        }
        TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(location);
        if (textureatlassprite == null) {
            textureatlassprite = TextureAtlasSprite.makeAtlasSprite(location);
            this.mapRegisteredSprites.put(location.toString(), textureatlassprite);
        }
        return textureatlassprite;
    }
    
    @Override
    public void tick() {
        this.updateAnimations();
    }
    
    public void setMipmapLevels(final int mipmapLevelsIn) {
        this.mipmapLevels = mipmapLevelsIn;
    }
    
    public TextureAtlasSprite getMissingSprite() {
        return this.missingImage;
    }
}
