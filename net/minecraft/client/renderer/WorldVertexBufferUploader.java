package net.minecraft.client.renderer;

import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.vertex.*;
import java.nio.*;
import java.util.*;

public class WorldVertexBufferUploader
{
    public void func_181679_a(final WorldRenderer p_181679_1_) {
        if (p_181679_1_.getVertexCount() > 0) {
            final VertexFormat vertexformat = p_181679_1_.getVertexFormat();
            final int i = vertexformat.getNextOffset();
            final ByteBuffer bytebuffer = p_181679_1_.getByteBuffer();
            final List<VertexFormatElement> list = vertexformat.getElements();
            for (int j = 0; j < list.size(); ++j) {
                final VertexFormatElement vertexformatelement = list.get(j);
                final VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
                final int k = vertexformatelement.getType().getGlConstant();
                final int l = vertexformatelement.getIndex();
                bytebuffer.position(vertexformat.func_181720_d(j));
                switch (vertexformatelement$enumusage) {
                    case POSITION: {
                        GL11.glVertexPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                        GL11.glEnableClientState(32884);
                        break;
                    }
                    case UV: {
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + l);
                        GL11.glTexCoordPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                        GL11.glEnableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;
                    }
                    case COLOR: {
                        GL11.glColorPointer(vertexformatelement.getElementCount(), k, i, bytebuffer);
                        GL11.glEnableClientState(32886);
                        break;
                    }
                    case NORMAL: {
                        GL11.glNormalPointer(k, i, bytebuffer);
                        GL11.glEnableClientState(32885);
                        break;
                    }
                }
            }
            GL11.glDrawArrays(p_181679_1_.getDrawMode(), 0, p_181679_1_.getVertexCount());
            for (int i2 = 0, j2 = list.size(); i2 < j2; ++i2) {
                final VertexFormatElement vertexformatelement2 = list.get(i2);
                final VertexFormatElement.EnumUsage vertexformatelement$enumusage2 = vertexformatelement2.getUsage();
                final int k2 = vertexformatelement2.getIndex();
                switch (vertexformatelement$enumusage2) {
                    case POSITION: {
                        GL11.glDisableClientState(32884);
                        break;
                    }
                    case UV: {
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + k2);
                        GL11.glDisableClientState(32888);
                        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                        break;
                    }
                    case COLOR: {
                        GL11.glDisableClientState(32886);
                        GlStateManager.resetColor();
                        break;
                    }
                    case NORMAL: {
                        GL11.glDisableClientState(32885);
                        break;
                    }
                }
            }
        }
        p_181679_1_.reset();
    }
}
