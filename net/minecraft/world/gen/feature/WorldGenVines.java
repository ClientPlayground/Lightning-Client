package net.minecraft.world.gen.feature;

import net.minecraft.world.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;

public class WorldGenVines extends WorldGenerator
{
    @Override
    public boolean generate(final World worldIn, final Random rand, BlockPos position) {
        while (position.getY() < 128) {
            if (worldIn.isAirBlock(position)) {
                EnumFacing[] facings;
                for (int length = (facings = EnumFacing.Plane.HORIZONTAL.facings()).length, i = 0; i < length; ++i) {
                    final EnumFacing enumfacing = facings[i];
                    if (Blocks.vine.canPlaceBlockOnSide(worldIn, position, enumfacing)) {
                        final IBlockState iblockstate = Blocks.vine.getDefaultState().withProperty((IProperty<Comparable>)BlockVine.NORTH, enumfacing == EnumFacing.NORTH).withProperty((IProperty<Comparable>)BlockVine.EAST, enumfacing == EnumFacing.EAST).withProperty((IProperty<Comparable>)BlockVine.SOUTH, enumfacing == EnumFacing.SOUTH).withProperty((IProperty<Comparable>)BlockVine.WEST, enumfacing == EnumFacing.WEST);
                        worldIn.setBlockState(position, iblockstate, 2);
                        break;
                    }
                }
            }
            else {
                position = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
            }
            position = position.up();
        }
        return true;
    }
}