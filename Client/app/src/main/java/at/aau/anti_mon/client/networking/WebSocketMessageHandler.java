package at.aau.anti_mon.client.networking;

public interface WebSocketMessageHandler<T> {

    void onMessageReceived(T message);
    
}
