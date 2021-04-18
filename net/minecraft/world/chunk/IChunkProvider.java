package net.minecraft.world.chunk;

import net.minecraft.util.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.*;

public interface IChunkProvider
{
    boolean chunkExists(final int p0, final int p1);
    
    Chunk provideChunk(final int p0, final int p1);
    
    Chunk provideChunk(final BlockPos p0);
    
    void populate(final IChunkProvider p0, final int p1, final int p2);
    
    boolean func_177460_a(final IChunkProvider p0, final Chunk p1, final int p2, final int p3);
    
    boolean saveChunks(final boolean p0, final IProgressUpdate p1);
    
    boolean unloadQueuedChunks();
    
    boolean canSave();
    
    String makeString();
    
    List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(final EnumCreatureType p0, final BlockPos p1);
    
    BlockPos getStrongholdGen(final World p0, final String p1, final BlockPos p2);
    
    int getLoadedChunkCount();
    
    void recreateStructures(final Chunk p0, final int p1, final int p2);
    
    void saveExtraData();
}
