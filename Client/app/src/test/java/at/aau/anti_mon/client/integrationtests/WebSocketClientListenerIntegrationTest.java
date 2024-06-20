package at.aau.anti_mon.client.integrationtests;

import static org.mockito.Mockito.*;

import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.networking.WebSocketClientListener;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class WebSocketClientListenerIntegrationTest {

    @Mock
    private OkHttpClient mockOkHttpClient;

    @Mock
    private WebSocketClient mockClient;

    @Mock
    private WebSocket mockWebSocket;

    @InjectMocks
    private WebSocketClientListener webSocketClientListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockOkHttpClient.newWebSocket(any(Request.class), any(WebSocketClientListener.class))).thenReturn(mockWebSocket);

    }

    @Test
    void testOnOpen() {
        Request mockRequest = new Request.Builder()
                .url("http://localhost")
                .build();

        Response mockResponse = new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(101)  // Switching Protocols
                .message("Connection successful")
                .build();

        webSocketClientListener.onOpen(mockWebSocket, mockResponse);

        verify(mockClient).onOpen();
    }

    @Test
    void testOnMessage() {
        String message = "Test message";

        webSocketClientListener.onMessage(mockWebSocket, message);

        verify(mockClient).handleIncomingMessage(message);
    }

    @Test
    void testOnClosed() {
        webSocketClientListener.onClosed(mockWebSocket, 1000, "Normal closure");

        verify(mockClient).onClose();
    }

    @Test
    void testOnFailureWithResponse() {
        Request mockRequest = new Request.Builder()
                .url("http://localhost")
                .build();

        Response mockResponse = new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(101)  // Switching Protocols
                .message("Error response")
                .build();

        Throwable mockThrowable = new Throwable("Test error");

        webSocketClientListener.onFailure(mockWebSocket, mockThrowable, mockResponse);

        verify(mockClient).onClose();

        // Optional: Überprüfe die Log-Nachricht
        // Hierfür könnte eine Bibliothek wie slf4j-test oder ein spezieller Logger-Test-Hook verwendet werden
    }

    @Test
    void testOnFailureWithoutResponse() {
        Throwable mockThrowable = mock(Throwable.class);
        when(mockThrowable.getMessage()).thenReturn("Test error");

        webSocketClientListener.onFailure(mockWebSocket, mockThrowable, null);

        verify(mockClient).onClose();
    }

    // TODO: Commented reconnection after Error out for testing
    /*@Test
    void testReconnectionLogic() {
        // Mocking necessary components for delay handling
        Mockito.doNothing().when(mockClient).connectToServer();

        // Simulate multiple failures to test reconnection logic
        for (int i = 0; i < 5; i++) {
            Throwable mockThrowable = mock(Throwable.class);
            when(mockThrowable.getMessage()).thenReturn("Test error " + i);

            webSocketClientListener.onFailure(mockWebSocket, mockThrowable, null);
        }

        verify(mockClient, times(5)).connectToServer();
    }
     */
}