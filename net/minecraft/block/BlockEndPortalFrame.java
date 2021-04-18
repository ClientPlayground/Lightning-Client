package net.minecraft.block;

import com.google.common.base.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.block.state.*;

public class BlockEndPortalFrame extends Block
{
    public static final PropertyDirection FACING;
    public static final PropertyBool EYE;
    
    static {
        FACING = PropertyDirection.create("facing", (Predicate<EnumFacing>)EnumFacing.Plane.HORIZONTAL);
        EYE = PropertyBool.create("eye");
    }
    
    public BlockEndPortalFrame() {
        super(Material.rock, MapColor.greenColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, EnumFacing.NORTH).withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, false));
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.8125f, 1.0f);
    }
    
    @Override
    public void addCollisionBoxesToList(final World worldIn, final BlockPos pos, final IBlockState state, final AxisAlignedBB mask, final List<AxisAlignedBB> list, final Entity collidingEntity) {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.8125f, 1.0f);
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        if (worldIn.getBlockState(pos).getValue((IProperty<Boolean>)BlockEndPortalFrame.EYE)) {
            this.setBlockBounds(0.3125f, 0.8125f, 0.3125f, 0.6875f, 1.0f, 0.6875f);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }
        this.setBlockBoundsForItemRender();
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return null;
    }
    
    @Override
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, placer.getHorizontalFacing().getOpposite()).withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, false);
    }
    
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos).getValue((IProperty<Boolean>)BlockEndPortalFrame.EYE) ? 15 : 0;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty((IProperty<Comparable>)BlockEndPortalFrame.EYE, (meta & 0x4) != 0x0).withProperty((IProperty<Comparable>)BlockEndPortalFrame.FACING, EnumFacing.getHorizontal(meta & 0x3));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue((IProperty<EnumFacing>)BlockEndPortalFrame.FACING).getHorizontalIndex();
        if (state.getValue((IProperty<Boolean>)BlockEndPortalFrame.EYE)) {
            i |= 0x4;
        }
        return i;
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockEndPortalFrame.FACING, BlockEndPortalFrame.EYE });
    }
}
