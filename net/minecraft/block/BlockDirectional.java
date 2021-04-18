package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.util.*;
import com.google.common.base.*;
import net.minecraft.block.material.*;

public abstract class BlockDirectional extends Block
{
    public static final PropertyDirection FACING;
    
    static {
        FACING = PropertyDirection.create("facing", (Predicate<EnumFacing>)EnumFacing.Plane.HORIZONTAL);
    }
    
    protected BlockDirectional(final Material materialIn) {
        super(materialIn);
    }
    
    protected BlockDirectional(final Material p_i46398_1_, final MapColor p_i46398_2_) {
        super(p_i46398_1_, p_i46398_2_);
    }
}
