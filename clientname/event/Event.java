package clientname.event;

import java.util.*;

public class Event
{
    public Event call() {
        final ArrayList<EventData> dataList = EventManager.get(this.getClass());
        if (dataList != null) {
            for (final EventData data : dataList) {
                try {
                    data.target.invoke(data.source, this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }
}
