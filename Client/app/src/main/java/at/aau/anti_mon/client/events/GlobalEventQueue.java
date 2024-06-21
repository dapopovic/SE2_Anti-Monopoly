package at.aau.anti_mon.client.events;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

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

    private GlobalEventQueue() {
        Log.d(DEBUG_TAG, "GlobalEventQueue created");
    }

    private static final class InstanceHolder {
        private static final GlobalEventQueue instance = new GlobalEventQueue();
    }

    public static GlobalEventQueue getInstance() {
        return InstanceHolder.instance;
    }

    public synchronized void enqueueEvent(Object event) {
        Log.d(DEBUG_TAG, "Enqueue Event: " + event);
        if (isEventBusReady) {
            Log.d("isEventBusReady","isEventBusReady: I am here.");
            EventBus.getDefault().post(event);
        } else {
            Log.d("isEventBusReady","isEventBusReady: I am not ready.");
            eventQueue.add(event);
        }
    }

    /**
     * Set the EventBus ready
     */
    public synchronized void setEventBusReady(boolean ready) {
        Log.d(DEBUG_TAG, "EventBus ready: " + ready);
        isEventBusReady = ready;
        if (ready) {
            flushEvents();
        }
    }

    private void flushEvents() {
        Log.d(DEBUG_TAG, "Flush Events");
        while (!eventQueue.isEmpty()) {
            EventBus.getDefault().post(eventQueue.poll());
        }
    }
}