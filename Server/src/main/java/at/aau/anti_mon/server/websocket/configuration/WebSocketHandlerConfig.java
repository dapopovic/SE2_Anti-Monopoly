package at.aau.anti_mon.server.websocket.configuration;

import at.aau.anti_mon.server.websocket.handler.GameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration for the WebSocket Handler
 */
@Configuration
//@ConfigurationProperties(prefix = "websockethandler")
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public WebSocketHandlerConfig(
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameHandler
                        (
                                eventPublisher
                        ), "/game")
                .setAllowedOrigins("*");
    }


}

