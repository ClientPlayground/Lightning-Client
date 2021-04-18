package net.minecraft.client.renderer.block.model;

import net.minecraft.util.*;

public class BakedQuad
{
    protected final int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    
    public BakedQuad(final int[] vertexDataIn, final int tintIndexIn, final EnumFacing faceIn) {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
    }
    
    public int[] getVertexData() {
        return this.vertexData;
    }
    
    public boolean hasTintIndex() {
        return this.tintIndex != -1;
    }
    
    public int getTintIndex() {
        return this.tintIndex;
    }
    
    public EnumFacing getFace() {
        return this.face;
    }
}
