package at.aau.anti_mon.client.events;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Test Queue class to handle the event queue
 * --> Problem was that sometimes Events are fired but no EventBus is registered
 */
public class GlobalEventQueue {
    private final Queue<Object> eventQueue = new LinkedList<>();
    private boolean isEventBusReady = false;

    public synchronized void enqueueEvent(Object event) {
        if (isEventBusReady) {
            EventBus.getDefault().post(event);
        } else {
            eventQueue.add(event);
        }
    }

    public synchronized void setEventBusReady(boolean ready) {
        isEventBusReady = ready;
        if (ready) {
            flushEvents();
        }
    }

    private void flushEvents() {
        while (!eventQueue.isEmpty()) {
            EventBus.getDefault().post(eventQueue.poll());
        }
    }
}