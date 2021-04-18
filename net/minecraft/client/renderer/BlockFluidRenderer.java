package net.minecraft.client.renderer;

import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.util.*;
import net.minecraft.block.properties.*;

public class BlockFluidRenderer
{
    private TextureAtlasSprite[] atlasSpritesLava;
    private TextureAtlasSprite[] atlasSpritesWater;
    
    public BlockFluidRenderer() {
        this.atlasSpritesLava = new TextureAtlasSprite[2];
        this.atlasSpritesWater = new TextureAtlasSprite[2];
        this.initAtlasSprites();
    }
    
    protected void initAtlasSprites() {
        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        this.atlasSpritesLava[0] = texturemap.getAtlasSprite("minecraft:blocks/lava_still");
        this.atlasSpritesLava[1] = texturemap.getAtlasSprite("minecraft:blocks/lava_flow");
        this.atlasSpritesWater[0] = texturemap.getAtlasSprite("minecraft:blocks/water_still");
        this.atlasSpritesWater[1] = texturemap.getAtlasSprite("minecraft:blocks/water_flow");
    }
    
    public boolean renderFluid(final IBlockAccess blockAccess, final IBlockState blockStateIn, final BlockPos blockPosIn, final WorldRenderer worldRendererIn) {
        final BlockLiquid blockliquid = (BlockLiquid)blockStateIn.getBlock();
        blockliquid.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
        final TextureAtlasSprite[] atextureatlassprite = (blockliquid.getMaterial() == Material.lava) ? this.atlasSpritesLava : this.atlasSpritesWater;
        final int i = blockliquid.colorMultiplier(blockAccess, blockPosIn);
        final float f = (i >> 16 & 0xFF) / 255.0f;
        final float f2 = (i >> 8 & 0xFF) / 255.0f;
        final float f3 = (i & 0xFF) / 255.0f;
        final boolean flag = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
        final boolean flag2 = blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
        final boolean[] aboolean = { blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH), blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH), blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST), blockliquid.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST) };
        if (!flag && !flag2 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3]) {
            return false;
        }
        boolean flag3 = false;
        final float f4 = 0.5f;
        final float f5 = 1.0f;
        final float f6 = 0.8f;
        final float f7 = 0.6f;
        final Material material = blockliquid.getMaterial();
        float f8 = this.getFluidHeight(blockAccess, blockPosIn, material);
        float f9 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
        float f10 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
        float f11 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
        final double d0 = blockPosIn.getX();
        final double d2 = blockPosIn.getY();
        final double d3 = blockPosIn.getZ();
        final float f12 = 0.001f;
        if (flag) {
            flag3 = true;
            TextureAtlasSprite textureatlassprite = atextureatlassprite[0];
            final float f13 = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);
            if (f13 > -999.0f) {
                textureatlassprite = atextureatlassprite[1];
            }
            f8 -= f12;
            f9 -= f12;
            f10 -= f12;
            f11 -= f12;
            float f14;
            float f15;
            float f16;
            float f17;
            float f18;
            float f19;
            float f20;
            float f21;
            if (f13 < -999.0f) {
                f14 = textureatlassprite.getInterpolatedU(0.0);
                f15 = textureatlassprite.getInterpolatedV(0.0);
                f16 = f14;
                f17 = textureatlassprite.getInterpolatedV(16.0);
                f18 = textureatlassprite.getInterpolatedU(16.0);
                f19 = f17;
                f20 = f18;
                f21 = f15;
            }
            else {
                final float f22 = MathHelper.sin(f13) * 0.25f;
                final float f23 = MathHelper.cos(f13) * 0.25f;
                final float f24 = 8.0f;
                f14 = textureatlassprite.getInterpolatedU(8.0f + (-f23 - f22) * 16.0f);
                f15 = textureatlassprite.getInterpolatedV(8.0f + (-f23 + f22) * 16.0f);
                f16 = textureatlassprite.getInterpolatedU(8.0f + (-f23 + f22) * 16.0f);
                f17 = textureatlassprite.getInterpolatedV(8.0f + (f23 + f22) * 16.0f);
                f18 = textureatlassprite.getInterpolatedU(8.0f + (f23 + f22) * 16.0f);
                f19 = textureatlassprite.getInterpolatedV(8.0f + (f23 - f22) * 16.0f);
                f20 = textureatlassprite.getInterpolatedU(8.0f + (f23 - f22) * 16.0f);
                f21 = textureatlassprite.getInterpolatedV(8.0f + (-f23 - f22) * 16.0f);
            }
            final int k2 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn);
            final int l2 = k2 >> 16 & 0xFFFF;
            final int i2 = k2 & 0xFFFF;
            final float f25 = f5 * f;
            final float f26 = f5 * f2;
            final float f27 = f5 * f3;
            worldRendererIn.pos(d0 + 0.0, d2 + f8, d3 + 0.0).color(f25, f26, f27, 1.0f).tex(f14, f15).lightmap(l2, i2).endVertex();
            worldRendererIn.pos(d0 + 0.0, d2 + f9, d3 + 1.0).color(f25, f26, f27, 1.0f).tex(f16, f17).lightmap(l2, i2).endVertex();
            worldRendererIn.pos(d0 + 1.0, d2 + f10, d3 + 1.0).color(f25, f26, f27, 1.0f).tex(f18, f19).lightmap(l2, i2).endVertex();
            worldRendererIn.pos(d0 + 1.0, d2 + f11, d3 + 0.0).color(f25, f26, f27, 1.0f).tex(f20, f21).lightmap(l2, i2).endVertex();
            if (blockliquid.func_176364_g(blockAccess, blockPosIn.up())) {
                worldRendererIn.pos(d0 + 0.0, d2 + f8, d3 + 0.0).color(f25, f26, f27, 1.0f).tex(f14, f15).lightmap(l2, i2).endVertex();
                worldRendererIn.pos(d0 + 1.0, d2 + f11, d3 + 0.0).color(f25, f26, f27, 1.0f).tex(f20, f21).lightmap(l2, i2).endVertex();
                worldRendererIn.pos(d0 + 1.0, d2 + f10, d3 + 1.0).color(f25, f26, f27, 1.0f).tex(f18, f19).lightmap(l2, i2).endVertex();
                worldRendererIn.pos(d0 + 0.0, d2 + f9, d3 + 1.0).color(f25, f26, f27, 1.0f).tex(f16, f17).lightmap(l2, i2).endVertex();
            }
        }
        if (flag2) {
            final float f28 = atextureatlassprite[0].getMinU();
            final float f29 = atextureatlassprite[0].getMaxU();
            final float f30 = atextureatlassprite[0].getMinV();
            final float f31 = atextureatlassprite[0].getMaxV();
            final int l3 = blockliquid.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
            final int i3 = l3 >> 16 & 0xFFFF;
            final int j2 = l3 & 0xFFFF;
            worldRendererIn.pos(d0, d2, d3 + 1.0).color(f4, f4, f4, 1.0f).tex(f28, f31).lightmap(i3, j2).endVertex();
            worldRendererIn.pos(d0, d2, d3).color(f4, f4, f4, 1.0f).tex(f28, f30).lightmap(i3, j2).endVertex();
            worldRendererIn.pos(d0 + 1.0, d2, d3).color(f4, f4, f4, 1.0f).tex(f29, f30).lightmap(i3, j2).endVertex();
            worldRendererIn.pos(d0 + 1.0, d2, d3 + 1.0).color(f4, f4, f4, 1.0f).tex(f29, f31).lightmap(i3, j2).endVertex();
            flag3 = true;
        }
        for (int i4 = 0; i4 < 4; ++i4) {
            int j3 = 0;
            int k3 = 0;
            if (i4 == 0) {
                --k3;
            }
            if (i4 == 1) {
                ++k3;
            }
            if (i4 == 2) {
                --j3;
            }
            if (i4 == 3) {
                ++j3;
            }
            final BlockPos blockpos = blockPosIn.add(j3, 0, k3);
            final TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
            if (aboolean[i4]) {
                float f32;
                float f33;
                double d4;
                double d5;
                double d6;
                double d7;
                if (i4 == 0) {
                    f32 = f8;
                    f33 = f11;
                    d4 = d0;
                    d5 = d0 + 1.0;
                    d6 = d3 + f12;
                    d7 = d3 + f12;
                }
                else if (i4 == 1) {
                    f32 = f10;
                    f33 = f9;
                    d4 = d0 + 1.0;
                    d5 = d0;
                    d6 = d3 + 1.0 - f12;
                    d7 = d3 + 1.0 - f12;
                }
                else if (i4 == 2) {
                    f32 = f9;
                    f33 = f8;
                    d4 = d0 + f12;
                    d5 = d0 + f12;
                    d6 = d3 + 1.0;
                    d7 = d3;
                }
                else {
                    f32 = f11;
                    f33 = f10;
                    d4 = d0 + 1.0 - f12;
                    d5 = d0 + 1.0 - f12;
                    d6 = d3;
                    d7 = d3 + 1.0;
                }
                flag3 = true;
                final float f34 = textureatlassprite2.getInterpolatedU(0.0);
                final float f35 = textureatlassprite2.getInterpolatedU(8.0);
                final float f36 = textureatlassprite2.getInterpolatedV((1.0f - f32) * 16.0f * 0.5f);
                final float f37 = textureatlassprite2.getInterpolatedV((1.0f - f33) * 16.0f * 0.5f);
                final float f38 = textureatlassprite2.getInterpolatedV(8.0);
                final int m = blockliquid.getMixedBrightnessForBlock(blockAccess, blockpos);
                final int k4 = m >> 16 & 0xFFFF;
                final int l4 = m & 0xFFFF;
                final float f39 = (i4 < 2) ? f6 : f7;
                final float f40 = f5 * f39 * f;
                final float f41 = f5 * f39 * f2;
                final float f42 = f5 * f39 * f3;
                worldRendererIn.pos(d4, d2 + f32, d6).color(f40, f41, f42, 1.0f).tex(f34, f36).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d5, d2 + f33, d7).color(f40, f41, f42, 1.0f).tex(f35, f37).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d5, d2 + 0.0, d7).color(f40, f41, f42, 1.0f).tex(f35, f38).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d4, d2 + 0.0, d6).color(f40, f41, f42, 1.0f).tex(f34, f38).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d4, d2 + 0.0, d6).color(f40, f41, f42, 1.0f).tex(f34, f38).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d5, d2 + 0.0, d7).color(f40, f41, f42, 1.0f).tex(f35, f38).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d5, d2 + f33, d7).color(f40, f41, f42, 1.0f).tex(f35, f37).lightmap(k4, l4).endVertex();
                worldRendererIn.pos(d4, d2 + f32, d6).color(f40, f41, f42, 1.0f).tex(f34, f36).lightmap(k4, l4).endVertex();
            }
        }
        return flag3;
    }
    
    private float getFluidHeight(final IBlockAccess blockAccess, final BlockPos blockPosIn, final Material blockMaterial) {
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < 4; ++j) {
            final BlockPos blockpos = blockPosIn.add(-(j & 0x1), 0, -(j >> 1 & 0x1));
            if (blockAccess.getBlockState(blockpos.up()).getBlock().getMaterial() == blockMaterial) {
                return 1.0f;
            }
            final IBlockState iblockstate = blockAccess.getBlockState(blockpos);
            final Material material = iblockstate.getBlock().getMaterial();
            if (material != blockMaterial) {
                if (!material.isSolid()) {
                    ++f;
                    ++i;
                }
            }
            else {
                final int k = iblockstate.getValue((IProperty<Integer>)BlockLiquid.LEVEL);
                if (k >= 8 || k == 0) {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10.0f;
                    i += 10;
                }
                f += BlockLiquid.getLiquidHeightPercent(k);
                ++i;
            }
        }
        return 1.0f - f / i;
    }
}
