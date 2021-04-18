package net.minecraft.client.renderer;

import java.awt.image.*;
import java.awt.*;

public class ImageBufferDownload implements IImageBuffer
{
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;
    
    @Override
    public BufferedImage parseUserSkin(final BufferedImage image) {
        if (image == null) {
            return null;
        }
        this.imageWidth = 64;
        this.imageHeight = 64;
        final BufferedImage bufferedimage = new BufferedImage(this.imageWidth, this.imageHeight, 2);
        final Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        if (image.getHeight() == 32) {
            graphics.drawImage(bufferedimage, 24, 48, 20, 52, 4, 16, 8, 20, null);
            graphics.drawImage(bufferedimage, 28, 48, 24, 52, 8, 16, 12, 20, null);
            graphics.drawImage(bufferedimage, 20, 52, 16, 64, 8, 20, 12, 32, null);
            graphics.drawImage(bufferedimage, 24, 52, 20, 64, 4, 20, 8, 32, null);
            graphics.drawImage(bufferedimage, 28, 52, 24, 64, 0, 20, 4, 32, null);
            graphics.drawImage(bufferedimage, 32, 52, 28, 64, 12, 20, 16, 32, null);
            graphics.drawImage(bufferedimage, 40, 48, 36, 52, 44, 16, 48, 20, null);
            graphics.drawImage(bufferedimage, 44, 48, 40, 52, 48, 16, 52, 20, null);
            graphics.drawImage(bufferedimage, 36, 52, 32, 64, 48, 20, 52, 32, null);
            graphics.drawImage(bufferedimage, 40, 52, 36, 64, 44, 20, 48, 32, null);
            graphics.drawImage(bufferedimage, 44, 52, 40, 64, 40, 20, 44, 32, null);
            graphics.drawImage(bufferedimage, 48, 52, 44, 64, 52, 20, 56, 32, null);
        }
        graphics.dispose();
        this.imageData = ((DataBufferInt)bufferedimage.getRaster().getDataBuffer()).getData();
        this.setAreaOpaque(0, 0, 32, 16);
        this.setAreaTransparent(32, 0, 64, 32);
        this.setAreaOpaque(0, 16, 64, 32);
        this.setAreaTransparent(0, 32, 16, 48);
        this.setAreaTransparent(16, 32, 40, 48);
        this.setAreaTransparent(40, 32, 56, 48);
        this.setAreaTransparent(0, 48, 16, 64);
        this.setAreaOpaque(16, 48, 48, 64);
        this.setAreaTransparent(48, 48, 64, 64);
        return bufferedimage;
    }
    
    @Override
    public void skinAvailable() {
    }
    
    private void setAreaTransparent(final int p_78434_1_, final int p_78434_2_, final int p_78434_3_, final int p_78434_4_) {
        if (!this.hasTransparency(p_78434_1_, p_78434_2_, p_78434_3_, p_78434_4_)) {
            for (int i = p_78434_1_; i < p_78434_3_; ++i) {
                for (int j = p_78434_2_; j < p_78434_4_; ++j) {
                    final int[] imageData = this.imageData;
                    final int n = i + j * this.imageWidth;
                    imageData[n] &= 0xFFFFFF;
                }
            }
        }
    }
    
    private void setAreaOpaque(final int p_78433_1_, final int p_78433_2_, final int p_78433_3_, final int p_78433_4_) {
        for (int i = p_78433_1_; i < p_78433_3_; ++i) {
            for (int j = p_78433_2_; j < p_78433_4_; ++j) {
                final int[] imageData = this.imageData;
                final int n = i + j * this.imageWidth;
                imageData[n] |= 0xFF000000;
            }
        }
    }
    
    private boolean hasTransparency(final int p_78435_1_, final int p_78435_2_, final int p_78435_3_, final int p_78435_4_) {
        for (int i = p_78435_1_; i < p_78435_3_; ++i) {
            for (int j = p_78435_2_; j < p_78435_4_; ++j) {
                final int k = this.imageData[i + j * this.imageWidth];
                if ((k >> 24 & 0xFF) < 128) {
                    return true;
                }
            }
        }
        return false;
    }
}
