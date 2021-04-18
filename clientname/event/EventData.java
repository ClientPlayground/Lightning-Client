package clientname.event;

import java.lang.reflect.*;

public class EventData
{
    public final Object source;
    public final Method target;
    public final byte priority;
    
    public EventData(final Object source, final Method target, final byte priority) {
        this.source = source;
        this.target = target;
        this.priority = priority;
    }
}
