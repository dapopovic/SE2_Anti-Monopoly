package at.aau.anti_mon.client.utilities;

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
    private static GlobalEventQueue instance;

    private GlobalEventQueue() {
        Log.d(DEBUG_TAG, "GlobalEventQueue created");
    }

    public static synchronized GlobalEventQueue getInstance() {
        if (instance == null) {
            instance = new GlobalEventQueue();
        }
        return instance;
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
     * @param ready
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