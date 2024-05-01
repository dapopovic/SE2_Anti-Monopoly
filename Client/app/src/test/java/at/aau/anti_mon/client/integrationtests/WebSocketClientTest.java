package at.aau.anti_mon.client.integrationtests;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.AppComponent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.websocketserver.WebsocketHandlerServerImpl;

public class WebSocketClientTest extends AntiMonopolyApplication {
    private static WebsocketHandlerServerImpl server;
    @Inject
    WebSocketClient client;
    @BeforeAll
    static void setUp() {
        server = new WebsocketHandlerServerImpl();
        try {
            server.start();
        } catch (Exception e) {
            fail("Failed to start server");
        }

    }

    @BeforeEach
    void init() {
        TestComponent testComponent = DaggerTestComponent.builder().networkModule(new NetworkModule(this)).build();
        GlobalEventQueue globalEventQueue = new GlobalEventQueue();
        globalEventQueue.setEventBusReady(true);
        setGlobalEventQueue(globalEventQueue);
        testComponent.inject(this);
    }

    @AfterAll
    static void tearDown() {
        try {
            server.stop();
        } catch (Exception e) {
            fail("Failed to stop server");
        }
    }

    @Test
    void testNewCreateGameCommandAndGetPin() {
        String uri = "ws://localhost:8080";
        client.connectToServer(uri);
    }

}
