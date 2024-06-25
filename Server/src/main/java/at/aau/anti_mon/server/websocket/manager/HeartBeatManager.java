package at.aau.anti_mon.server.websocket.manager;

import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.utilities.MessagingUtility;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class HeartBeatManager {

    private final SessionManagementService sessionManagementService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, Long> sessionLastActivityMap = new ConcurrentHashMap<>();

    @Autowired
    public HeartBeatManager(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeatToAllSessions, 0, 15, TimeUnit.SECONDS);
    }

    public void sendHeartbeatToAllSessions() {
        long inactivityThreshold = TimeUnit.SECONDS.toMillis(30); // Beispiel: 30 Sekunden Inaktivität
        new HashMap<>(sessionManagementService.getAllSessions()).forEach((sessionId, session) -> {
            if (session.isOpen()) {
                if (isSessionInactive(session, inactivityThreshold)) {
                    sendHeartBeatMessage(session);
                }
            } else {
                removeClosedSession(sessionId);
            }
        });
    }

    private void sendHeartBeatMessage(@NotNull WebSocketSession session) {
        MessagingUtility.createHeartbeatMessage().send(session);
        updateSessionActivity(session);
    }

    private void updateSessionActivity(WebSocketSession session) {
        sessionLastActivityMap.put(session.getId(), System.currentTimeMillis());
    }

    private boolean isSessionInactive(WebSocketSession session, long inactivityThreshold) {
        Long lastActivityTime = sessionLastActivityMap.get(session.getId());
        return lastActivityTime != null && (System.currentTimeMillis() - lastActivityTime > inactivityThreshold);
    }

    private void removeClosedSession(String sessionId) {
        sessionManagementService.getAllSessions().remove(sessionId);
        sessionManagementService.getUserSessionMap().entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .map(Map.Entry::getKey)
                .findFirst().ifPresent(userId -> sessionManagementService.getUserSessionMap().remove(userId));
        System.out.println("Session mit ID " + sessionId + " wurde entfernt, da sie geschlossen ist.");
    }

    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Set the interrupt flag
            Logger.error("Failed to stop scheduler properly", e);
        }
    }
}