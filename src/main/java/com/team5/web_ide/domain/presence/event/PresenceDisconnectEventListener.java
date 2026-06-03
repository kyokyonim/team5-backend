package com.team5.web_ide.domain.presence.event;

import com.team5.web_ide.domain.presence.service.PresenceConnectionRegistry;
import com.team5.web_ide.domain.presence.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceDisconnectEventListener {

    private final PresenceConnectionRegistry presenceConnectionRegistry;
    private final PresenceService presenceService;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();

        presenceConnectionRegistry.unregisterSession(sessionId)
                .forEach(key -> {
                    try {
                        presenceService.disconnectCurrentUser(key.projectId(), key.userId());
                    } catch (RuntimeException exception) {
                        log.warn(
                                "Presence disconnect cleanup failed. projectId={}, userId={}",
                                key.projectId(),
                                key.userId(),
                                exception
                        );
                    }
                });
    }
}
