package clientname.event;

public class EventCancelable extends Event
{
    private boolean cancelled;
    
    public EventCancelable() {
        this.cancelled = false;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
