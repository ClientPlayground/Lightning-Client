package Client.Cosmetics.utils;

import net.minecraft.util.*;

public class AnimatedResourceLocation
{
    private final String folder;
    protected final int frames;
    protected final int fpt;
    protected int currentTick;
    protected int currentFrame;
    protected ResourceLocation[] textures;
    
    public AnimatedResourceLocation(final String folder, final int frames, final int fpt) {
        this(folder, frames, fpt, false);
    }
    
    public AnimatedResourceLocation(final String folder, final int frames, final int fpt, final boolean reverse) {
        this.currentTick = 0;
        this.currentFrame = 0;
        this.folder = folder;
        this.frames = frames;
        this.fpt = fpt;
        this.textures = new ResourceLocation[frames];
        for (int i = 0; i < frames; ++i) {
            if (reverse) {
                this.textures[i] = new ResourceLocation(String.valueOf(folder) + "/" + (this.textures.length - i) + ".png");
            }
            else {
                this.textures[i] = new ResourceLocation(String.valueOf(folder) + "/" + i + ".png");
            }
        }
    }
    
    public ResourceLocation getTexture() {
        return this.textures[this.currentFrame];
    }
    
    public int getCurrentFrame() {
        return this.currentFrame;
    }
    
    public void update() {
        if (this.currentTick > this.fpt) {
            this.currentTick = 0;
            ++this.currentFrame;
            if (this.currentFrame > this.frames - 1) {
                this.currentFrame = 0;
            }
        }
        ++this.currentTick;
    }
    
    public void setCurrentFrame(final int currentFrame) {
        this.currentFrame = currentFrame;
    }
    
    public int getFrames() {
        return this.frames;
    }
}
