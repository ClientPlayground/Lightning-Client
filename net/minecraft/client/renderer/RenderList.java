package net.minecraft.client.renderer;

import net.minecraft.util.*;
import net.minecraft.client.renderer.chunk.*;
import org.lwjgl.opengl.*;
import java.util.*;

public class RenderList extends ChunkRenderContainer
{
    @Override
    public void renderChunkLayer(final EnumWorldBlockLayer layer) {
        if (this.initialized) {
            for (final RenderChunk renderchunk : this.renderChunks) {
                final ListedRenderChunk listedrenderchunk = (ListedRenderChunk)renderchunk;
                GlStateManager.pushMatrix();
                this.preRenderChunk(renderchunk);
                GL11.glCallList(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();
            this.renderChunks.clear();
        }
    }
}
