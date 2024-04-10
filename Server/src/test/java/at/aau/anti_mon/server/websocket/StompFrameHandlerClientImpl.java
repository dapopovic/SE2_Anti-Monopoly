package at.aau.anti_mon.server.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class StompFrameHandlerClientImpl implements StompFrameHandler {
    private final BlockingQueue<String> messagesQueue;

    public StompFrameHandlerClientImpl(BlockingQueue<String> receivedMessagesQueue) {
        messagesQueue = receivedMessagesQueue;
    }

    @Override
    public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleFrame(@NotNull StompHeaders headers, Object payload) {
        // add the new message to the queue of received messages
        messagesQueue.add((String) payload);
    }

}
