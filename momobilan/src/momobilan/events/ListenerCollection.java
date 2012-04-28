/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package momobilan.events;

import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Andhika
 */
public class ListenerCollection extends ConcurrentHashMap<String,ConcurrentLinkedQueue<EventListener>> {
    public void addEventListener(String eventName, TrackListener eventListener) {
        if (containsKey(eventName)) {
            ConcurrentLinkedQueue<EventListener> existing = get(eventName);
            existing.add(eventListener);
            replace(eventName, existing); // not sure whether this is necessary
        }
        else {
            ConcurrentLinkedQueue<EventListener> queue = new ConcurrentLinkedQueue<EventListener>();
            queue.add(eventListener);
            putIfAbsent(eventName, queue);
        }
    }
    
    public void fireEvent(String eventName, TrackEvent eventObject) {
        if (containsKey(eventName)) {
            Class[] parameterTypes = {EventListener.class};
            Object[] args = {eventObject};
            
            for (EventListener listener : get(eventName)) {
                Class listenerClass = listener.getClass();
                try {
                    Method method = listenerClass.getMethod(eventName, parameterTypes);
                    method.invoke(listener, args);
                } catch (Exception e) {
                    // several exceptions might happen, but do nothing.
                    // actually we might want to do something... but not yet.
                }
            }
        }
    }
}
