package at.aau.anti_mon.server;

import at.aau.anti_mon.server.websocket.configuration.SchedulingConfig;
import at.aau.anti_mon.server.websocket.configuration.WebSocketBrokerConfig;
import at.aau.anti_mon.server.websocket.configuration.WebSocketHandlerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SchedulingConfig.class, WebSocketHandlerConfig.class, WebSocketBrokerConfig.class })
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
