package net.minecraft.client.audio;

import net.minecraft.util.*;

public interface ISound
{
    ResourceLocation getSoundLocation();
    
    boolean canRepeat();
    
    int getRepeatDelay();
    
    float getVolume();
    
    float getPitch();
    
    float getXPosF();
    
    float getYPosF();
    
    float getZPosF();
    
    AttenuationType getAttenuationType();
    
    public enum AttenuationType
    {
        NONE("NONE", 0, 0), 
        LINEAR("LINEAR", 1, 2);
        
        private final int type;
        
        private AttenuationType(final String s, final int n, final int typeIn) {
            this.type = typeIn;
        }
        
        public int getTypeInt() {
            return this.type;
        }
    }
}
