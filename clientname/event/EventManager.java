package clientname.event;

import java.util.*;
import java.lang.reflect.*;
import java.lang.annotation.*;

public class EventManager
{
    private static final Map<Class<? extends Event>, ArrayList<EventData>> REGISTRY_MAP;
    
    static {
        REGISTRY_MAP = new HashMap<Class<? extends Event>, ArrayList<EventData>>();
    }
    
    private static void sortListValue(final Class<? extends Event> clazz) {
        final ArrayList<EventData> flexableArray = new ArrayList<EventData>();
        byte[] value_ARRAY;
        for (int length = (value_ARRAY = EventPriority.VALUE_ARRAY).length, i = 0; i < length; ++i) {
            final byte b = value_ARRAY[i];
            for (final EventData methodData : EventManager.REGISTRY_MAP.get(clazz)) {
                if (methodData.priority == b) {
                    flexableArray.add(methodData);
                }
            }
        }
        EventManager.REGISTRY_MAP.put(clazz, flexableArray);
    }
    
    private static boolean isMethodBad(final Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventTarget.class);
    }
    
    private static boolean isMethodBad(final Method method, final Class<? extends Event> clazz) {
        return isMethodBad(method) || method.getParameterTypes()[0].equals(clazz);
    }
    
    public static ArrayList<EventData> get(final Class<? extends Event> clazz) {
        return EventManager.REGISTRY_MAP.get(clazz);
    }
    
    public static void cleanMap(final boolean removeOnlyEmptyValues) {
        final Iterator<Map.Entry<Class<? extends Event>, ArrayList<EventData>>> iterator = EventManager.REGISTRY_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            if (!removeOnlyEmptyValues || iterator.next().getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }
    
    public static void unregister(final Object o, final Class<? extends Event> clazz) {
        if (EventManager.REGISTRY_MAP.containsKey(clazz)) {
            for (final EventData methodData : EventManager.REGISTRY_MAP.get(clazz)) {
                if (methodData.source.equals(o)) {
                    EventManager.REGISTRY_MAP.get(clazz).remove(methodData);
                }
            }
        }
        cleanMap(true);
    }
    
    public static void unregister(final Object o) {
        for (final ArrayList<EventData> flexableArray : EventManager.REGISTRY_MAP.values()) {
            for (int i = flexableArray.size() - 1; i >= 0; --i) {
                if (flexableArray.get(i).source.equals(o)) {
                    flexableArray.remove(i);
                }
            }
        }
        cleanMap(true);
    }
    
    public static void register(final Method method, final Object o) {
        final Class<?> clazz = method.getParameterTypes()[0];
        final EventData methodData = new EventData(o, method, method.getAnnotation(EventTarget.class).value());
        if (!methodData.target.isAccessible()) {
            methodData.target.setAccessible(true);
        }
        if (EventManager.REGISTRY_MAP.containsKey(clazz)) {
            if (!EventManager.REGISTRY_MAP.get(clazz).contains(methodData)) {
                EventManager.REGISTRY_MAP.get(clazz).add(methodData);
                sortListValue((Class<? extends Event>)clazz);
            }
        }
        else {
            EventManager.REGISTRY_MAP.put((Class<? extends Event>)clazz, new ArrayList<EventData>(methodData) {
                {
                    this.add(eventData);
                }
            });
        }
    }
    
    public static void register(final Object o, final Class<? extends Event> clazz) {
        Method[] methods;
        for (int length = (methods = o.getClass().getMethods()).length, i = 0; i < length; ++i) {
            final Method method = methods[i];
            if (!isMethodBad(method, clazz)) {
                register(method, o);
            }
        }
    }
    
    public static void register(final Object o) {
        Method[] methods;
        for (int length = (methods = o.getClass().getMethods()).length, i = 0; i < length; ++i) {
            final Method method = methods[i];
            if (!isMethodBad(method)) {
                register(method, o);
            }
        }
    }
}
