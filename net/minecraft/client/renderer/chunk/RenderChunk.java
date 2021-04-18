package net.minecraft.client.renderer.chunk;

import java.util.concurrent.locks.*;
import net.minecraft.tileentity.*;
import java.nio.*;
import com.google.common.collect.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.client.renderer.*;

public class RenderChunk
{
    private World world;
    private final RenderGlobal renderGlobal;
    public static int renderChunksUpdated;
    private BlockPos position;
    public CompiledChunk compiledChunk;
    private final ReentrantLock lockCompileTask;
    private final ReentrantLock lockCompiledChunk;
    private ChunkCompileTaskGenerator compileTask;
    private final Set<TileEntity> field_181056_j;
    private final int index;
    private final FloatBuffer modelviewMatrix;
    private final VertexBuffer[] vertexBuffers;
    public AxisAlignedBB boundingBox;
    private int frameIndex;
    private boolean needsUpdate;
    private EnumMap<EnumFacing, BlockPos> field_181702_p;
    
    public RenderChunk(final World worldIn, final RenderGlobal renderGlobalIn, final BlockPos blockPosIn, final int indexIn) {
        this.compiledChunk = CompiledChunk.DUMMY;
        this.lockCompileTask = new ReentrantLock();
        this.lockCompiledChunk = new ReentrantLock();
        this.compileTask = null;
        this.field_181056_j = (Set<TileEntity>)Sets.newHashSet();
        this.modelviewMatrix = GLAllocation.createDirectFloatBuffer(16);
        this.vertexBuffers = new VertexBuffer[EnumWorldBlockLayer.values().length];
        this.frameIndex = -1;
        this.needsUpdate = true;
        this.field_181702_p = (EnumMap<EnumFacing, BlockPos>)Maps.newEnumMap((Class)EnumFacing.class);
        this.world = worldIn;
        this.renderGlobal = renderGlobalIn;
        this.index = indexIn;
        if (!blockPosIn.equals(this.getPosition())) {
            this.setPosition(blockPosIn);
        }
        if (OpenGlHelper.useVbo()) {
            for (int i = 0; i < EnumWorldBlockLayer.values().length; ++i) {
                this.vertexBuffers[i] = new VertexBuffer(DefaultVertexFormats.BLOCK);
            }
        }
    }
    
    public boolean setFrameIndex(final int frameIndexIn) {
        if (this.frameIndex == frameIndexIn) {
            return false;
        }
        this.frameIndex = frameIndexIn;
        return true;
    }
    
    public VertexBuffer getVertexBufferByLayer(final int layer) {
        return this.vertexBuffers[layer];
    }
    
    public void setPosition(final BlockPos pos) {
        this.stopCompileTask();
        this.position = pos;
        this.boundingBox = new AxisAlignedBB(pos, pos.add(16, 16, 16));
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing enumfacing = values[i];
            this.field_181702_p.put(enumfacing, pos.offset(enumfacing, 16));
        }
        this.initModelviewMatrix();
    }
    
    public void resortTransparency(final float x, final float y, final float z, final ChunkCompileTaskGenerator generator) {
        final CompiledChunk compiledchunk = generator.getCompiledChunk();
        if (compiledchunk.getState() != null && !compiledchunk.isLayerEmpty(EnumWorldBlockLayer.TRANSLUCENT)) {
            this.preRenderBlocks(generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT), this.position);
            generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT).setVertexState(compiledchunk.getState());
            this.postRenderBlocks(EnumWorldBlockLayer.TRANSLUCENT, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT), compiledchunk);
        }
    }
    
    public void rebuildChunk(final float x, final float y, final float z, final ChunkCompileTaskGenerator generator) {
        final CompiledChunk compiledchunk = new CompiledChunk();
        final int i = 1;
        final BlockPos blockpos = this.position;
        final BlockPos blockpos2 = blockpos.add(15, 15, 15);
        generator.getLock().lock();
        IBlockAccess iblockaccess;
        try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
                return;
            }
            iblockaccess = new RegionRenderCache(this.world, blockpos.add(-1, -1, -1), blockpos2.add(1, 1, 1), 1);
            generator.setCompiledChunk(compiledchunk);
        }
        finally {
            generator.getLock().unlock();
        }
        generator.getLock().unlock();
        final VisGraph lvt_10_1_ = new VisGraph();
        final HashSet lvt_11_1_ = Sets.newHashSet();
        if (!iblockaccess.extendedLevelsInChunkCache()) {
            ++RenderChunk.renderChunksUpdated;
            final boolean[] aboolean = new boolean[EnumWorldBlockLayer.values().length];
            final BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
            for (final BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos2)) {
                final IBlockState iblockstate = iblockaccess.getBlockState(blockpos$mutableblockpos);
                final Block block = iblockstate.getBlock();
                if (block.isOpaqueCube()) {
                    lvt_10_1_.func_178606_a(blockpos$mutableblockpos);
                }
                if (block.hasTileEntity()) {
                    final TileEntity tileentity = iblockaccess.getTileEntity(new BlockPos(blockpos$mutableblockpos));
                    final TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getSpecialRenderer(tileentity);
                    if (tileentity != null && tileentityspecialrenderer != null) {
                        compiledchunk.addTileEntity(tileentity);
                        if (tileentityspecialrenderer.func_181055_a()) {
                            lvt_11_1_.add(tileentity);
                        }
                    }
                }
                final EnumWorldBlockLayer enumworldblocklayer1 = block.getBlockLayer();
                final int j = enumworldblocklayer1.ordinal();
                if (block.getRenderType() != -1) {
                    final WorldRenderer worldrenderer = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);
                    if (!compiledchunk.isLayerStarted(enumworldblocklayer1)) {
                        compiledchunk.setLayerStarted(enumworldblocklayer1);
                        this.preRenderBlocks(worldrenderer, blockpos);
                    }
                    final boolean[] array = aboolean;
                    final int n = j;
                    array[n] |= blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, iblockaccess, worldrenderer);
                }
            }
            EnumWorldBlockLayer[] values;
            for (int length = (values = EnumWorldBlockLayer.values()).length, k = 0; k < length; ++k) {
                final EnumWorldBlockLayer enumworldblocklayer2 = values[k];
                if (aboolean[enumworldblocklayer2.ordinal()]) {
                    compiledchunk.setLayerUsed(enumworldblocklayer2);
                }
                if (compiledchunk.isLayerStarted(enumworldblocklayer2)) {
                    this.postRenderBlocks(enumworldblocklayer2, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumworldblocklayer2), compiledchunk);
                }
            }
        }
        compiledchunk.setVisibility(lvt_10_1_.computeVisibility());
        this.lockCompileTask.lock();
        try {
            final Set<TileEntity> set = (Set<TileEntity>)Sets.newHashSet((Iterable)lvt_11_1_);
            final Set<TileEntity> set2 = (Set<TileEntity>)Sets.newHashSet((Iterable)this.field_181056_j);
            set.removeAll(this.field_181056_j);
            set2.removeAll(lvt_11_1_);
            this.field_181056_j.clear();
            this.field_181056_j.addAll(lvt_11_1_);
            this.renderGlobal.func_181023_a(set2, set);
        }
        finally {
            this.lockCompileTask.unlock();
        }
        this.lockCompileTask.unlock();
    }
    
    protected void finishCompileTask() {
        this.lockCompileTask.lock();
        try {
            if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
                this.compileTask.finish();
                this.compileTask = null;
            }
        }
        finally {
            this.lockCompileTask.unlock();
        }
        this.lockCompileTask.unlock();
    }
    
    public ReentrantLock getLockCompileTask() {
        return this.lockCompileTask;
    }
    
    public ChunkCompileTaskGenerator makeCompileTaskChunk() {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;
        try {
            this.finishCompileTask();
            this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
            chunkcompiletaskgenerator = this.compileTask;
        }
        finally {
            this.lockCompileTask.unlock();
        }
        this.lockCompileTask.unlock();
        return chunkcompiletaskgenerator;
    }
    
    public ChunkCompileTaskGenerator makeCompileTaskTransparency() {
        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;
        try {
            if (this.compileTask == null || this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
                if (this.compileTask != null && this.compileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE) {
                    this.compileTask.finish();
                    this.compileTask = null;
                }
                (this.compileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY)).setCompiledChunk(this.compiledChunk);
                chunkcompiletaskgenerator = this.compileTask;
                return chunkcompiletaskgenerator;
            }
            chunkcompiletaskgenerator = null;
        }
        finally {
            this.lockCompileTask.unlock();
        }
        this.lockCompileTask.unlock();
        return chunkcompiletaskgenerator;
    }
    
    private void preRenderBlocks(final WorldRenderer worldRendererIn, final BlockPos pos) {
        worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
        worldRendererIn.setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
    }
    
    private void postRenderBlocks(final EnumWorldBlockLayer layer, final float x, final float y, final float z, final WorldRenderer worldRendererIn, final CompiledChunk compiledChunkIn) {
        if (layer == EnumWorldBlockLayer.TRANSLUCENT && !compiledChunkIn.isLayerEmpty(layer)) {
            worldRendererIn.func_181674_a(x, y, z);
            compiledChunkIn.setState(worldRendererIn.func_181672_a());
        }
        worldRendererIn.finishDrawing();
    }
    
    private void initModelviewMatrix() {
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        final float f = 1.000001f;
        GlStateManager.translate(-8.0f, -8.0f, -8.0f);
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0f, 8.0f, 8.0f);
        GlStateManager.getFloat(2982, this.modelviewMatrix);
        GlStateManager.popMatrix();
    }
    
    public void multModelviewMatrix() {
        GlStateManager.multMatrix(this.modelviewMatrix);
    }
    
    public CompiledChunk getCompiledChunk() {
        return this.compiledChunk;
    }
    
    public void setCompiledChunk(final CompiledChunk compiledChunkIn) {
        this.lockCompiledChunk.lock();
        try {
            this.compiledChunk = compiledChunkIn;
        }
        finally {
            this.lockCompiledChunk.unlock();
        }
        this.lockCompiledChunk.unlock();
    }
    
    public void stopCompileTask() {
        this.finishCompileTask();
        this.compiledChunk = CompiledChunk.DUMMY;
    }
    
    public void deleteGlResources() {
        this.stopCompileTask();
        this.world = null;
        for (int i = 0; i < EnumWorldBlockLayer.values().length; ++i) {
            if (this.vertexBuffers[i] != null) {
                this.vertexBuffers[i].deleteGlBuffers();
            }
        }
    }
    
    public BlockPos getPosition() {
        return this.position;
    }
    
    public void setNeedsUpdate(final boolean needsUpdateIn) {
        this.needsUpdate = needsUpdateIn;
    }
    
    public boolean isNeedsUpdate() {
        return this.needsUpdate;
    }
    
    public BlockPos func_181701_a(final EnumFacing p_181701_1_) {
        return this.field_181702_p.get(p_181701_1_);
    }
}
