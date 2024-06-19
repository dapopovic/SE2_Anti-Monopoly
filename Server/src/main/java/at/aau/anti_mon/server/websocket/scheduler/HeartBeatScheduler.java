package at.aau.anti_mon.server.websocket.scheduler;


import at.aau.anti_mon.server.websocket.manager.HeartBeatManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

/**
 * This Class is responsible for sending a heartbeat message to all active sessions every 25 seconds
 */
@Component
public class HeartBeatScheduler {

    private final HeartBeatManager heartbeatManager;

    public HeartBeatScheduler(HeartBeatManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    @PostConstruct
    public void init() {
        heartbeatManager.start();
        Logger.info("HeartBeatManager started");
    }

    @PreDestroy
    public void destroy() {
        heartbeatManager.stop();
        Logger.info("HeartBeatManager stopped");

    }

}