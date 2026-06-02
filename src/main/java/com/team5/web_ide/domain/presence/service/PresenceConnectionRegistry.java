package com.team5.web_ide.domain.presence.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class PresenceConnectionRegistry {

    private final ConcurrentMap<String, Set<ProjectUserKey>> sessionProjects = new ConcurrentHashMap<>();
    private final ConcurrentMap<ProjectUserKey, Set<String>> projectUserSessions = new ConcurrentHashMap<>();

    public void register(String sessionId, Long projectId, Long userId) {
        if (sessionId == null || projectId == null || userId == null) {
            return;
        }

        ProjectUserKey key = new ProjectUserKey(projectId, userId);
        Set<ProjectUserKey> projects = sessionProjects.computeIfAbsent(
                sessionId,
                ignored -> ConcurrentHashMap.newKeySet()
        );

        if (projects.add(key)) {
            projectUserSessions.computeIfAbsent(
                    key,
                    ignored -> ConcurrentHashMap.newKeySet()
            ).add(sessionId);
        }
    }

    public List<ProjectUserKey> unregisterSession(String sessionId) {
        if (sessionId == null) {
            return List.of();
        }

        Set<ProjectUserKey> keys = sessionProjects.remove(sessionId);
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        List<ProjectUserKey> disconnectedLastConnections = new ArrayList<>();

        for (ProjectUserKey key : keys) {
            Set<String> sessions = projectUserSessions.get(key);
            if (sessions == null) {
                disconnectedLastConnections.add(key);
                continue;
            }

            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                projectUserSessions.remove(key, sessions);
                disconnectedLastConnections.add(key);
            }
        }

        return disconnectedLastConnections;
    }

    public boolean hasActiveConnection(Long projectId, Long userId) {
        Set<String> sessions = projectUserSessions.get(new ProjectUserKey(projectId, userId));
        return sessions != null && !sessions.isEmpty();
    }

    public record ProjectUserKey(Long projectId, Long userId) {
    }
}
