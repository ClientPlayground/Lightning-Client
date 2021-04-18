package net.minecraft.block.state;

import com.google.common.base.*;
import net.minecraft.block.properties.*;
import java.util.*;
import net.minecraft.block.*;
import com.google.common.collect.*;

public abstract class BlockStateBase implements IBlockState
{
    private static final Joiner COMMA_JOINER;
    private static final Function<Map.Entry<IProperty, Comparable>, String> MAP_ENTRY_TO_STRING;
    
    static {
        COMMA_JOINER = Joiner.on(',');
        MAP_ENTRY_TO_STRING = (Function)new Function<Map.Entry<IProperty, Comparable>, String>() {
            public String apply(final Map.Entry<IProperty, Comparable> p_apply_1_) {
                if (p_apply_1_ == null) {
                    return "<NULL>";
                }
                final IProperty iproperty = p_apply_1_.getKey();
                return String.valueOf(iproperty.getName()) + "=" + iproperty.getName(p_apply_1_.getValue());
            }
        };
    }
    
    @Override
    public <T extends Comparable<T>> IBlockState cycleProperty(final IProperty<T> property) {
        return this.withProperty(property, (Comparable)cyclePropertyValue((Collection<V>)property.getAllowedValues(), (V)this.getValue((IProperty<T>)property)));
    }
    
    protected static <T> T cyclePropertyValue(final Collection<T> values, final T currentValue) {
        final Iterator<T> iterator = values.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(currentValue)) {
                if (iterator.hasNext()) {
                    return iterator.next();
                }
                return values.iterator().next();
            }
        }
        return iterator.next();
    }
    
    @Override
    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(Block.blockRegistry.getNameForObject(this.getBlock()));
        if (!this.getProperties().isEmpty()) {
            stringbuilder.append("[");
            BlockStateBase.COMMA_JOINER.appendTo(stringbuilder, Iterables.transform((Iterable)this.getProperties().entrySet(), (Function)BlockStateBase.MAP_ENTRY_TO_STRING));
            stringbuilder.append("]");
        }
        return stringbuilder.toString();
    }
}
