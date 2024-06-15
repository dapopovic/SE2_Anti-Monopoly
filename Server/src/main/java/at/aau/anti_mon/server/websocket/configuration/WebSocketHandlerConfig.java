package at.aau.anti_mon.server.websocket.configuration;

import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.websocket.handler.BroadcastWebSocketHandler;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.tinylog.Logger;

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
            LobbyService lobbyService,
    //        SessionManagementService sessionManagementService,
          //  CommandFactory gameCommandFactory,
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        Logger.debug("Register WebSocket handlers");

        registry.addHandler(new GameHandler
                        (
                                //lobbyService,
                        //gameCommandFactory,
                        //        sessionManagementService,
                                eventPublisher
                        ), "/game")
                .setAllowedOrigins("*");

        /**
         * The advantage of using sockJS here is whenever the websocket connection is disconnected or the websocket connection can not be established,
         * then the connection will be downgraded to HTTP and the communication between client and server can still continue.
         */
        registry
                .addHandler(broadcastWebSocketHandler(), "/broadcast")
                .setAllowedOrigins("*")
                .withSockJS()
        ;

    }


    @Bean
    public WebSocketHandler broadcastWebSocketHandler() {

        Logger.debug("Create EchoBroadcastWebSocketHandler");

        return new BroadcastWebSocketHandler();
    }


}

