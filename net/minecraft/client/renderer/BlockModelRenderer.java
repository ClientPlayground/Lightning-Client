package net.minecraft.client.renderer;

import net.minecraft.world.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.crash.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;
import net.minecraft.client.renderer.vertex.*;

public class BlockModelRenderer
{
    public boolean renderModel(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn) {
        final Block block = blockStateIn.getBlock();
        block.setBlockBoundsBasedOnState(blockAccessIn, blockPosIn);
        return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, worldRendererIn, true);
    }
    
    public boolean renderModel(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        final boolean flag = Minecraft.isAmbientOcclusionEnabled() && blockStateIn.getBlock().getLightValue() == 0 && modelIn.isAmbientOcclusion();
        try {
            final Block block = blockStateIn.getBlock();
            return flag ? this.renderModelAmbientOcclusion(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides) : this.renderModelStandard(blockAccessIn, modelIn, block, blockPosIn, worldRendererIn, checkSides);
        }
        catch (Throwable throwable) {
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, blockPosIn, blockStateIn);
            crashreportcategory.addCrashSection("Using AO", flag);
            throw new ReportedException(crashreport);
        }
    }
    
    public boolean renderModelAmbientOcclusion(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        boolean flag = false;
        final float[] afloat = new float[EnumFacing.values().length * 2];
        final BitSet bitset = new BitSet(3);
        final AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new AmbientOcclusionFace();
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing enumfacing = values[i];
            final List<BakedQuad> list = modelIn.getFaceQuads(enumfacing);
            if (!list.isEmpty()) {
                final BlockPos blockpos = blockPosIn.offset(enumfacing);
                if (!checkSides || blockIn.shouldSideBeRendered(blockAccessIn, blockpos, enumfacing)) {
                    this.renderModelAmbientOcclusionQuads(blockAccessIn, blockIn, blockPosIn, worldRendererIn, list, afloat, bitset, blockmodelrenderer$ambientocclusionface);
                    flag = true;
                }
            }
        }
        final List<BakedQuad> list2 = modelIn.getGeneralQuads();
        if (list2.size() > 0) {
            this.renderModelAmbientOcclusionQuads(blockAccessIn, blockIn, blockPosIn, worldRendererIn, list2, afloat, bitset, blockmodelrenderer$ambientocclusionface);
            flag = true;
        }
        return flag;
    }
    
    public boolean renderModelStandard(final IBlockAccess blockAccessIn, final IBakedModel modelIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final boolean checkSides) {
        boolean flag = false;
        final BitSet bitset = new BitSet(3);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, j = 0; j < length; ++j) {
            final EnumFacing enumfacing = values[j];
            final List<BakedQuad> list = modelIn.getFaceQuads(enumfacing);
            if (!list.isEmpty()) {
                final BlockPos blockpos = blockPosIn.offset(enumfacing);
                if (!checkSides || blockIn.shouldSideBeRendered(blockAccessIn, blockpos, enumfacing)) {
                    final int i = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos);
                    this.renderModelStandardQuads(blockAccessIn, blockIn, blockPosIn, enumfacing, i, false, worldRendererIn, list, bitset);
                    flag = true;
                }
            }
        }
        final List<BakedQuad> list2 = modelIn.getGeneralQuads();
        if (list2.size() > 0) {
            this.renderModelStandardQuads(blockAccessIn, blockIn, blockPosIn, null, -1, true, worldRendererIn, list2, bitset);
            flag = true;
        }
        return flag;
    }
    
    private void renderModelAmbientOcclusionQuads(final IBlockAccess blockAccessIn, final Block blockIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn, final List<BakedQuad> listQuadsIn, final float[] quadBounds, final BitSet boundsFlags, final AmbientOcclusionFace aoFaceIn) {
        double d0 = blockPosIn.getX();
        double d2 = blockPosIn.getY();
        double d3 = blockPosIn.getZ();
        final Block.EnumOffsetType block$enumoffsettype = blockIn.getOffsetType();
        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            final long i = MathHelper.getPositionRandom(blockPosIn);
            d0 += ((i >> 16 & 0xFL) / 15.0f - 0.5) * 0.5;
            d3 += ((i >> 24 & 0xFL) / 15.0f - 0.5) * 0.5;
            if (block$enumoffsettype == Block.EnumOffsetType.XYZ) {
                d2 += ((i >> 20 & 0xFL) / 15.0f - 1.0) * 0.2;
            }
        }
        for (final BakedQuad bakedquad : listQuadsIn) {
            this.fillQuadBounds(blockIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, boundsFlags);
            aoFaceIn.updateVertexBrightness(blockAccessIn, blockIn, blockPosIn, bakedquad.getFace(), quadBounds, boundsFlags);
            worldRendererIn.addVertexData(bakedquad.getVertexData());
            worldRendererIn.putBrightness4(aoFaceIn.vertexBrightness[0], aoFaceIn.vertexBrightness[1], aoFaceIn.vertexBrightness[2], aoFaceIn.vertexBrightness[3]);
            if (bakedquad.hasTintIndex()) {
                int j = blockIn.colorMultiplier(blockAccessIn, blockPosIn, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    j = TextureUtil.anaglyphColor(j);
                }
                final float f = (j >> 16 & 0xFF) / 255.0f;
                final float f2 = (j >> 8 & 0xFF) / 255.0f;
                final float f3 = (j & 0xFF) / 255.0f;
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[0] * f, aoFaceIn.vertexColorMultiplier[0] * f2, aoFaceIn.vertexColorMultiplier[0] * f3, 4);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[1] * f, aoFaceIn.vertexColorMultiplier[1] * f2, aoFaceIn.vertexColorMultiplier[1] * f3, 3);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[2] * f, aoFaceIn.vertexColorMultiplier[2] * f2, aoFaceIn.vertexColorMultiplier[2] * f3, 2);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[3] * f, aoFaceIn.vertexColorMultiplier[3] * f2, aoFaceIn.vertexColorMultiplier[3] * f3, 1);
            }
            else {
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[0], aoFaceIn.vertexColorMultiplier[0], aoFaceIn.vertexColorMultiplier[0], 4);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[1], aoFaceIn.vertexColorMultiplier[1], aoFaceIn.vertexColorMultiplier[1], 3);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[2], aoFaceIn.vertexColorMultiplier[2], aoFaceIn.vertexColorMultiplier[2], 2);
                worldRendererIn.putColorMultiplier(aoFaceIn.vertexColorMultiplier[3], aoFaceIn.vertexColorMultiplier[3], aoFaceIn.vertexColorMultiplier[3], 1);
            }
            worldRendererIn.putPosition(d0, d2, d3);
        }
    }
    
    private void fillQuadBounds(final Block blockIn, final int[] vertexData, final EnumFacing facingIn, final float[] quadBounds, final BitSet boundsFlags) {
        float f = 32.0f;
        float f2 = 32.0f;
        float f3 = 32.0f;
        float f4 = -32.0f;
        float f5 = -32.0f;
        float f6 = -32.0f;
        for (int i = 0; i < 4; ++i) {
            final float f7 = Float.intBitsToFloat(vertexData[i * 7]);
            final float f8 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
            final float f9 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
            f = Math.min(f, f7);
            f2 = Math.min(f2, f8);
            f3 = Math.min(f3, f9);
            f4 = Math.max(f4, f7);
            f5 = Math.max(f5, f8);
            f6 = Math.max(f6, f9);
        }
        if (quadBounds != null) {
            quadBounds[EnumFacing.WEST.getIndex()] = f;
            quadBounds[EnumFacing.EAST.getIndex()] = f4;
            quadBounds[EnumFacing.DOWN.getIndex()] = f2;
            quadBounds[EnumFacing.UP.getIndex()] = f5;
            quadBounds[EnumFacing.NORTH.getIndex()] = f3;
            quadBounds[EnumFacing.SOUTH.getIndex()] = f6;
            quadBounds[EnumFacing.WEST.getIndex() + EnumFacing.values().length] = 1.0f - f;
            quadBounds[EnumFacing.EAST.getIndex() + EnumFacing.values().length] = 1.0f - f4;
            quadBounds[EnumFacing.DOWN.getIndex() + EnumFacing.values().length] = 1.0f - f2;
            quadBounds[EnumFacing.UP.getIndex() + EnumFacing.values().length] = 1.0f - f5;
            quadBounds[EnumFacing.NORTH.getIndex() + EnumFacing.values().length] = 1.0f - f3;
            quadBounds[EnumFacing.SOUTH.getIndex() + EnumFacing.values().length] = 1.0f - f6;
        }
        final float f10 = 1.0E-4f;
        final float f11 = 0.9999f;
        switch (facingIn) {
            case DOWN: {
                boundsFlags.set(1, f >= 1.0E-4f || f3 >= 1.0E-4f || f4 <= 0.9999f || f6 <= 0.9999f);
                boundsFlags.set(0, (f2 < 1.0E-4f || blockIn.isFullCube()) && f2 == f5);
                break;
            }
            case UP: {
                boundsFlags.set(1, f >= 1.0E-4f || f3 >= 1.0E-4f || f4 <= 0.9999f || f6 <= 0.9999f);
                boundsFlags.set(0, (f5 > 0.9999f || blockIn.isFullCube()) && f2 == f5);
                break;
            }
            case NORTH: {
                boundsFlags.set(1, f >= 1.0E-4f || f2 >= 1.0E-4f || f4 <= 0.9999f || f5 <= 0.9999f);
                boundsFlags.set(0, (f3 < 1.0E-4f || blockIn.isFullCube()) && f3 == f6);
                break;
            }
            case SOUTH: {
                boundsFlags.set(1, f >= 1.0E-4f || f2 >= 1.0E-4f || f4 <= 0.9999f || f5 <= 0.9999f);
                boundsFlags.set(0, (f6 > 0.9999f || blockIn.isFullCube()) && f3 == f6);
                break;
            }
            case WEST: {
                boundsFlags.set(1, f2 >= 1.0E-4f || f3 >= 1.0E-4f || f5 <= 0.9999f || f6 <= 0.9999f);
                boundsFlags.set(0, (f < 1.0E-4f || blockIn.isFullCube()) && f == f4);
                break;
            }
            case EAST: {
                boundsFlags.set(1, f2 >= 1.0E-4f || f3 >= 1.0E-4f || f5 <= 0.9999f || f6 <= 0.9999f);
                boundsFlags.set(0, (f4 > 0.9999f || blockIn.isFullCube()) && f == f4);
                break;
            }
        }
    }
    
    private void renderModelStandardQuads(final IBlockAccess blockAccessIn, final Block blockIn, final BlockPos blockPosIn, final EnumFacing faceIn, int brightnessIn, final boolean ownBrightness, final WorldRenderer worldRendererIn, final List<BakedQuad> listQuadsIn, final BitSet boundsFlags) {
        double d0 = blockPosIn.getX();
        double d2 = blockPosIn.getY();
        double d3 = blockPosIn.getZ();
        final Block.EnumOffsetType block$enumoffsettype = blockIn.getOffsetType();
        if (block$enumoffsettype != Block.EnumOffsetType.NONE) {
            final int i = blockPosIn.getX();
            final int j = blockPosIn.getZ();
            long k = (long)(i * 3129871) ^ j * 116129781L;
            k = k * k * 42317861L + k * 11L;
            d0 += ((k >> 16 & 0xFL) / 15.0f - 0.5) * 0.5;
            d3 += ((k >> 24 & 0xFL) / 15.0f - 0.5) * 0.5;
            if (block$enumoffsettype == Block.EnumOffsetType.XYZ) {
                d2 += ((k >> 20 & 0xFL) / 15.0f - 1.0) * 0.2;
            }
        }
        for (final BakedQuad bakedquad : listQuadsIn) {
            if (ownBrightness) {
                this.fillQuadBounds(blockIn, bakedquad.getVertexData(), bakedquad.getFace(), null, boundsFlags);
                brightnessIn = (boundsFlags.get(0) ? blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(bakedquad.getFace())) : blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn));
            }
            worldRendererIn.addVertexData(bakedquad.getVertexData());
            worldRendererIn.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);
            if (bakedquad.hasTintIndex()) {
                int l = blockIn.colorMultiplier(blockAccessIn, blockPosIn, bakedquad.getTintIndex());
                if (EntityRenderer.anaglyphEnable) {
                    l = TextureUtil.anaglyphColor(l);
                }
                final float f = (l >> 16 & 0xFF) / 255.0f;
                final float f2 = (l >> 8 & 0xFF) / 255.0f;
                final float f3 = (l & 0xFF) / 255.0f;
                worldRendererIn.putColorMultiplier(f, f2, f3, 4);
                worldRendererIn.putColorMultiplier(f, f2, f3, 3);
                worldRendererIn.putColorMultiplier(f, f2, f3, 2);
                worldRendererIn.putColorMultiplier(f, f2, f3, 1);
            }
            worldRendererIn.putPosition(d0, d2, d3);
        }
    }
    
    public void renderModelBrightnessColor(final IBakedModel bakedModel, final float p_178262_2_, final float p_178262_3_, final float p_178262_4_, final float p_178262_5_) {
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing enumfacing = values[i];
            this.renderModelBrightnessColorQuads(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, bakedModel.getFaceQuads(enumfacing));
        }
        this.renderModelBrightnessColorQuads(p_178262_2_, p_178262_3_, p_178262_4_, p_178262_5_, bakedModel.getGeneralQuads());
    }
    
    public void renderModelBrightness(final IBakedModel p_178266_1_, final IBlockState p_178266_2_, final float p_178266_3_, final boolean p_178266_4_) {
        final Block block = p_178266_2_.getBlock();
        block.setBlockBoundsForItemRender();
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        int i = block.getRenderColor(block.getStateForEntityRender(p_178266_2_));
        if (EntityRenderer.anaglyphEnable) {
            i = TextureUtil.anaglyphColor(i);
        }
        final float f = (i >> 16 & 0xFF) / 255.0f;
        final float f2 = (i >> 8 & 0xFF) / 255.0f;
        final float f3 = (i & 0xFF) / 255.0f;
        if (!p_178266_4_) {
            GlStateManager.color(p_178266_3_, p_178266_3_, p_178266_3_, 1.0f);
        }
        this.renderModelBrightnessColor(p_178266_1_, p_178266_3_, f, f2, f3);
    }
    
    private void renderModelBrightnessColorQuads(final float p_178264_1_, final float p_178264_2_, final float p_178264_3_, final float p_178264_4_, final List<BakedQuad> p_178264_5_) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        for (final BakedQuad bakedquad : p_178264_5_) {
            worldrenderer.begin(7, DefaultVertexFormats.ITEM);
            worldrenderer.addVertexData(bakedquad.getVertexData());
            if (bakedquad.hasTintIndex()) {
                worldrenderer.putColorRGB_F4(p_178264_2_ * p_178264_1_, p_178264_3_ * p_178264_1_, p_178264_4_ * p_178264_1_);
            }
            else {
                worldrenderer.putColorRGB_F4(p_178264_1_, p_178264_1_, p_178264_1_);
            }
            final Vec3i vec3i = bakedquad.getFace().getDirectionVec();
            worldrenderer.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
            tessellator.draw();
        }
    }
    
    public enum EnumNeighborInfo
    {
        DOWN("DOWN", 0, new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH }, 0.5f, false, new Orientation[0], new Orientation[0], new Orientation[0], new Orientation[0]), 
        UP("UP", 1, new EnumFacing[] { EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH }, 1.0f, false, new Orientation[0], new Orientation[0], new Orientation[0], new Orientation[0]), 
        NORTH("NORTH", 2, new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST }, 0.8f, true, new Orientation[] { Orientation.UP, Orientation.FLIP_WEST, Orientation.UP, Orientation.WEST, Orientation.FLIP_UP, Orientation.WEST, Orientation.FLIP_UP, Orientation.FLIP_WEST }, new Orientation[] { Orientation.UP, Orientation.FLIP_EAST, Orientation.UP, Orientation.EAST, Orientation.FLIP_UP, Orientation.EAST, Orientation.FLIP_UP, Orientation.FLIP_EAST }, new Orientation[] { Orientation.DOWN, Orientation.FLIP_EAST, Orientation.DOWN, Orientation.EAST, Orientation.FLIP_DOWN, Orientation.EAST, Orientation.FLIP_DOWN, Orientation.FLIP_EAST }, new Orientation[] { Orientation.DOWN, Orientation.FLIP_WEST, Orientation.DOWN, Orientation.WEST, Orientation.FLIP_DOWN, Orientation.WEST, Orientation.FLIP_DOWN, Orientation.FLIP_WEST }), 
        SOUTH("SOUTH", 3, new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP }, 0.8f, true, new Orientation[] { Orientation.UP, Orientation.FLIP_WEST, Orientation.FLIP_UP, Orientation.FLIP_WEST, Orientation.FLIP_UP, Orientation.WEST, Orientation.UP, Orientation.WEST }, new Orientation[] { Orientation.DOWN, Orientation.FLIP_WEST, Orientation.FLIP_DOWN, Orientation.FLIP_WEST, Orientation.FLIP_DOWN, Orientation.WEST, Orientation.DOWN, Orientation.WEST }, new Orientation[] { Orientation.DOWN, Orientation.FLIP_EAST, Orientation.FLIP_DOWN, Orientation.FLIP_EAST, Orientation.FLIP_DOWN, Orientation.EAST, Orientation.DOWN, Orientation.EAST }, new Orientation[] { Orientation.UP, Orientation.FLIP_EAST, Orientation.FLIP_UP, Orientation.FLIP_EAST, Orientation.FLIP_UP, Orientation.EAST, Orientation.UP, Orientation.EAST }), 
        WEST("WEST", 4, new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH }, 0.6f, true, new Orientation[] { Orientation.UP, Orientation.SOUTH, Orientation.UP, Orientation.FLIP_SOUTH, Orientation.FLIP_UP, Orientation.FLIP_SOUTH, Orientation.FLIP_UP, Orientation.SOUTH }, new Orientation[] { Orientation.UP, Orientation.NORTH, Orientation.UP, Orientation.FLIP_NORTH, Orientation.FLIP_UP, Orientation.FLIP_NORTH, Orientation.FLIP_UP, Orientation.NORTH }, new Orientation[] { Orientation.DOWN, Orientation.NORTH, Orientation.DOWN, Orientation.FLIP_NORTH, Orientation.FLIP_DOWN, Orientation.FLIP_NORTH, Orientation.FLIP_DOWN, Orientation.NORTH }, new Orientation[] { Orientation.DOWN, Orientation.SOUTH, Orientation.DOWN, Orientation.FLIP_SOUTH, Orientation.FLIP_DOWN, Orientation.FLIP_SOUTH, Orientation.FLIP_DOWN, Orientation.SOUTH }), 
        EAST("EAST", 5, new EnumFacing[] { EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH }, 0.6f, true, new Orientation[] { Orientation.FLIP_DOWN, Orientation.SOUTH, Orientation.FLIP_DOWN, Orientation.FLIP_SOUTH, Orientation.DOWN, Orientation.FLIP_SOUTH, Orientation.DOWN, Orientation.SOUTH }, new Orientation[] { Orientation.FLIP_DOWN, Orientation.NORTH, Orientation.FLIP_DOWN, Orientation.FLIP_NORTH, Orientation.DOWN, Orientation.FLIP_NORTH, Orientation.DOWN, Orientation.NORTH }, new Orientation[] { Orientation.FLIP_UP, Orientation.NORTH, Orientation.FLIP_UP, Orientation.FLIP_NORTH, Orientation.UP, Orientation.FLIP_NORTH, Orientation.UP, Orientation.NORTH }, new Orientation[] { Orientation.FLIP_UP, Orientation.SOUTH, Orientation.FLIP_UP, Orientation.FLIP_SOUTH, Orientation.UP, Orientation.FLIP_SOUTH, Orientation.UP, Orientation.SOUTH });
        
        protected final EnumFacing[] field_178276_g;
        protected final float field_178288_h;
        protected final boolean field_178289_i;
        protected final Orientation[] field_178286_j;
        protected final Orientation[] field_178287_k;
        protected final Orientation[] field_178284_l;
        protected final Orientation[] field_178285_m;
        private static final EnumNeighborInfo[] field_178282_n;
        
        static {
            (field_178282_n = new EnumNeighborInfo[6])[EnumFacing.DOWN.getIndex()] = EnumNeighborInfo.DOWN;
            EnumNeighborInfo.field_178282_n[EnumFacing.UP.getIndex()] = EnumNeighborInfo.UP;
            EnumNeighborInfo.field_178282_n[EnumFacing.NORTH.getIndex()] = EnumNeighborInfo.NORTH;
            EnumNeighborInfo.field_178282_n[EnumFacing.SOUTH.getIndex()] = EnumNeighborInfo.SOUTH;
            EnumNeighborInfo.field_178282_n[EnumFacing.WEST.getIndex()] = EnumNeighborInfo.WEST;
            EnumNeighborInfo.field_178282_n[EnumFacing.EAST.getIndex()] = EnumNeighborInfo.EAST;
        }
        
        private EnumNeighborInfo(final String s, final int n, final EnumFacing[] p_i46236_3_, final float p_i46236_4_, final boolean p_i46236_5_, final Orientation[] p_i46236_6_, final Orientation[] p_i46236_7_, final Orientation[] p_i46236_8_, final Orientation[] p_i46236_9_) {
            this.field_178276_g = p_i46236_3_;
            this.field_178288_h = p_i46236_4_;
            this.field_178289_i = p_i46236_5_;
            this.field_178286_j = p_i46236_6_;
            this.field_178287_k = p_i46236_7_;
            this.field_178284_l = p_i46236_8_;
            this.field_178285_m = p_i46236_9_;
        }
        
        public static EnumNeighborInfo getNeighbourInfo(final EnumFacing p_178273_0_) {
            return EnumNeighborInfo.field_178282_n[p_178273_0_.getIndex()];
        }
    }
    
    public enum Orientation
    {
        DOWN("DOWN", 0, EnumFacing.DOWN, false), 
        UP("UP", 1, EnumFacing.UP, false), 
        NORTH("NORTH", 2, EnumFacing.NORTH, false), 
        SOUTH("SOUTH", 3, EnumFacing.SOUTH, false), 
        WEST("WEST", 4, EnumFacing.WEST, false), 
        EAST("EAST", 5, EnumFacing.EAST, false), 
        FLIP_DOWN("FLIP_DOWN", 6, EnumFacing.DOWN, true), 
        FLIP_UP("FLIP_UP", 7, EnumFacing.UP, true), 
        FLIP_NORTH("FLIP_NORTH", 8, EnumFacing.NORTH, true), 
        FLIP_SOUTH("FLIP_SOUTH", 9, EnumFacing.SOUTH, true), 
        FLIP_WEST("FLIP_WEST", 10, EnumFacing.WEST, true), 
        FLIP_EAST("FLIP_EAST", 11, EnumFacing.EAST, true);
        
        protected final int field_178229_m;
        
        private Orientation(final String s, final int n, final EnumFacing p_i46233_3_, final boolean p_i46233_4_) {
            this.field_178229_m = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing.values().length : 0);
        }
    }
    
    enum VertexTranslations
    {
        DOWN("DOWN", 0, 0, 1, 2, 3), 
        UP("UP", 1, 2, 3, 0, 1), 
        NORTH("NORTH", 2, 3, 0, 1, 2), 
        SOUTH("SOUTH", 3, 0, 1, 2, 3), 
        WEST("WEST", 4, 3, 0, 1, 2), 
        EAST("EAST", 5, 1, 2, 3, 0);
        
        private final int field_178191_g;
        private final int field_178200_h;
        private final int field_178201_i;
        private final int field_178198_j;
        private static final VertexTranslations[] field_178199_k;
        
        static {
            (field_178199_k = new VertexTranslations[6])[EnumFacing.DOWN.getIndex()] = VertexTranslations.DOWN;
            VertexTranslations.field_178199_k[EnumFacing.UP.getIndex()] = VertexTranslations.UP;
            VertexTranslations.field_178199_k[EnumFacing.NORTH.getIndex()] = VertexTranslations.NORTH;
            VertexTranslations.field_178199_k[EnumFacing.SOUTH.getIndex()] = VertexTranslations.SOUTH;
            VertexTranslations.field_178199_k[EnumFacing.WEST.getIndex()] = VertexTranslations.WEST;
            VertexTranslations.field_178199_k[EnumFacing.EAST.getIndex()] = VertexTranslations.EAST;
        }
        
        private VertexTranslations(final String s, final int n, final int p_i46234_3_, final int p_i46234_4_, final int p_i46234_5_, final int p_i46234_6_) {
            this.field_178191_g = p_i46234_3_;
            this.field_178200_h = p_i46234_4_;
            this.field_178201_i = p_i46234_5_;
            this.field_178198_j = p_i46234_6_;
        }
        
        public static VertexTranslations getVertexTranslations(final EnumFacing p_178184_0_) {
            return VertexTranslations.field_178199_k[p_178184_0_.getIndex()];
        }
    }
    
    class AmbientOcclusionFace
    {
        private final float[] vertexColorMultiplier;
        private final int[] vertexBrightness;
        
        AmbientOcclusionFace() {
            this.vertexColorMultiplier = new float[4];
            this.vertexBrightness = new int[4];
        }
        
        public void updateVertexBrightness(final IBlockAccess blockAccessIn, final Block blockIn, final BlockPos blockPosIn, final EnumFacing facingIn, final float[] quadBounds, final BitSet boundsFlags) {
            final BlockPos blockpos = boundsFlags.get(0) ? blockPosIn.offset(facingIn) : blockPosIn;
            final EnumNeighborInfo blockmodelrenderer$enumneighborinfo = EnumNeighborInfo.getNeighbourInfo(facingIn);
            final BlockPos blockpos2 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[0]);
            final BlockPos blockpos3 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[1]);
            final BlockPos blockpos4 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
            final BlockPos blockpos5 = blockpos.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
            final int i = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos2);
            final int j = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos3);
            final int k = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos4);
            final int l = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos5);
            final float f = blockAccessIn.getBlockState(blockpos2).getBlock().getAmbientOcclusionLightValue();
            final float f2 = blockAccessIn.getBlockState(blockpos3).getBlock().getAmbientOcclusionLightValue();
            final float f3 = blockAccessIn.getBlockState(blockpos4).getBlock().getAmbientOcclusionLightValue();
            final float f4 = blockAccessIn.getBlockState(blockpos5).getBlock().getAmbientOcclusionLightValue();
            final boolean flag = blockAccessIn.getBlockState(blockpos2.offset(facingIn)).getBlock().isTranslucent();
            final boolean flag2 = blockAccessIn.getBlockState(blockpos3.offset(facingIn)).getBlock().isTranslucent();
            final boolean flag3 = blockAccessIn.getBlockState(blockpos4.offset(facingIn)).getBlock().isTranslucent();
            final boolean flag4 = blockAccessIn.getBlockState(blockpos5.offset(facingIn)).getBlock().isTranslucent();
            float f5;
            int i2;
            if (!flag3 && !flag) {
                f5 = f;
                i2 = i;
            }
            else {
                final BlockPos blockpos6 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f5 = blockAccessIn.getBlockState(blockpos6).getBlock().getAmbientOcclusionLightValue();
                i2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos6);
            }
            float f6;
            int j2;
            if (!flag4 && !flag) {
                f6 = f;
                j2 = i;
            }
            else {
                final BlockPos blockpos7 = blockpos2.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f6 = blockAccessIn.getBlockState(blockpos7).getBlock().getAmbientOcclusionLightValue();
                j2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos7);
            }
            float f7;
            int k2;
            if (!flag3 && !flag2) {
                f7 = f2;
                k2 = j;
            }
            else {
                final BlockPos blockpos8 = blockpos3.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[2]);
                f7 = blockAccessIn.getBlockState(blockpos8).getBlock().getAmbientOcclusionLightValue();
                k2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos8);
            }
            float f8;
            int l2;
            if (!flag4 && !flag2) {
                f8 = f2;
                l2 = j;
            }
            else {
                final BlockPos blockpos9 = blockpos3.offset(blockmodelrenderer$enumneighborinfo.field_178276_g[3]);
                f8 = blockAccessIn.getBlockState(blockpos9).getBlock().getAmbientOcclusionLightValue();
                l2 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockpos9);
            }
            int i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn);
            if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockPosIn.offset(facingIn)).getBlock().isOpaqueCube()) {
                i3 = blockIn.getMixedBrightnessForBlock(blockAccessIn, blockPosIn.offset(facingIn));
            }
            final float f9 = boundsFlags.get(0) ? blockAccessIn.getBlockState(blockpos).getBlock().getAmbientOcclusionLightValue() : blockAccessIn.getBlockState(blockPosIn).getBlock().getAmbientOcclusionLightValue();
            final VertexTranslations blockmodelrenderer$vertextranslations = VertexTranslations.getVertexTranslations(facingIn);
            if (boundsFlags.get(1) && blockmodelrenderer$enumneighborinfo.field_178289_i) {
                final float f10 = (f4 + f + f6 + f9) * 0.25f;
                final float f11 = (f3 + f + f5 + f9) * 0.25f;
                final float f12 = (f3 + f2 + f7 + f9) * 0.25f;
                final float f13 = (f4 + f2 + f8 + f9) * 0.25f;
                final float f14 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[1].field_178229_m];
                final float f15 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[3].field_178229_m];
                final float f16 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[5].field_178229_m];
                final float f17 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178286_j[7].field_178229_m];
                final float f18 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[1].field_178229_m];
                final float f19 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[3].field_178229_m];
                final float f20 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[5].field_178229_m];
                final float f21 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178287_k[7].field_178229_m];
                final float f22 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[1].field_178229_m];
                final float f23 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[3].field_178229_m];
                final float f24 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[5].field_178229_m];
                final float f25 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178284_l[7].field_178229_m];
                final float f26 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[0].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[1].field_178229_m];
                final float f27 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[2].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[3].field_178229_m];
                final float f28 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[4].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[5].field_178229_m];
                final float f29 = quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[6].field_178229_m] * quadBounds[blockmodelrenderer$enumneighborinfo.field_178285_m[7].field_178229_m];
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178191_g] = f10 * f14 + f11 * f15 + f12 * f16 + f13 * f17;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178200_h] = f10 * f18 + f11 * f19 + f12 * f20 + f13 * f21;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178201_i] = f10 * f22 + f11 * f23 + f12 * f24 + f13 * f25;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178198_j] = f10 * f26 + f11 * f27 + f12 * f28 + f13 * f29;
                final int i4 = this.getAoBrightness(l, i, j2, i3);
                final int j3 = this.getAoBrightness(k, i, i2, i3);
                final int k3 = this.getAoBrightness(k, j, k2, i3);
                final int l3 = this.getAoBrightness(l, j, l2, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178191_g] = this.getVertexBrightness(i4, j3, k3, l3, f14, f15, f16, f17);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178200_h] = this.getVertexBrightness(i4, j3, k3, l3, f18, f19, f20, f21);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178201_i] = this.getVertexBrightness(i4, j3, k3, l3, f22, f23, f24, f25);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178198_j] = this.getVertexBrightness(i4, j3, k3, l3, f26, f27, f28, f29);
            }
            else {
                final float f30 = (f4 + f + f6 + f9) * 0.25f;
                final float f31 = (f3 + f + f5 + f9) * 0.25f;
                final float f32 = (f3 + f2 + f7 + f9) * 0.25f;
                final float f33 = (f4 + f2 + f8 + f9) * 0.25f;
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178191_g] = this.getAoBrightness(l, i, j2, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178200_h] = this.getAoBrightness(k, i, i2, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178201_i] = this.getAoBrightness(k, j, k2, i3);
                this.vertexBrightness[blockmodelrenderer$vertextranslations.field_178198_j] = this.getAoBrightness(l, j, l2, i3);
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178191_g] = f30;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178200_h] = f31;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178201_i] = f32;
                this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.field_178198_j] = f33;
            }
        }
        
        private int getAoBrightness(int p_147778_1_, int p_147778_2_, int p_147778_3_, final int p_147778_4_) {
            if (p_147778_1_ == 0) {
                p_147778_1_ = p_147778_4_;
            }
            if (p_147778_2_ == 0) {
                p_147778_2_ = p_147778_4_;
            }
            if (p_147778_3_ == 0) {
                p_147778_3_ = p_147778_4_;
            }
            return p_147778_1_ + p_147778_2_ + p_147778_3_ + p_147778_4_ >> 2 & 0xFF00FF;
        }
        
        private int getVertexBrightness(final int p_178203_1_, final int p_178203_2_, final int p_178203_3_, final int p_178203_4_, final float p_178203_5_, final float p_178203_6_, final float p_178203_7_, final float p_178203_8_) {
            final int i = (int)((p_178203_1_ >> 16 & 0xFF) * p_178203_5_ + (p_178203_2_ >> 16 & 0xFF) * p_178203_6_ + (p_178203_3_ >> 16 & 0xFF) * p_178203_7_ + (p_178203_4_ >> 16 & 0xFF) * p_178203_8_) & 0xFF;
            final int j = (int)((p_178203_1_ & 0xFF) * p_178203_5_ + (p_178203_2_ & 0xFF) * p_178203_6_ + (p_178203_3_ & 0xFF) * p_178203_7_ + (p_178203_4_ & 0xFF) * p_178203_8_) & 0xFF;
            return i << 16 | j;
        }
    }
}
