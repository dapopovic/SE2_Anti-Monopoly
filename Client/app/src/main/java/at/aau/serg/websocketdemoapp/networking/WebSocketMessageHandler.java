package at.aau.serg.websocketdemoapp.networking;

public interface WebSocketMessageHandler<T> {

    void onMessageReceived(T message);
    
}
