package com.team5.web_ide.domain.presence.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PresenceConnectionRegistryTest {

    private final PresenceConnectionRegistry registry = new PresenceConnectionRegistry();

    @Test
    @DisplayName("same project user unlocks only after the last session disconnects")
    void unregisterSessionReturnsOnlyLastConnection() {
        registry.register("session-1", 1L, 10L);
        registry.register("session-2", 1L, 10L);

        List<PresenceConnectionRegistry.ProjectUserKey> firstDisconnected =
                registry.unregisterSession("session-1");

        assertThat(firstDisconnected).isEmpty();
        assertThat(registry.hasActiveConnection(1L, 10L)).isTrue();

        List<PresenceConnectionRegistry.ProjectUserKey> lastDisconnected =
                registry.unregisterSession("session-2");

        assertThat(lastDisconnected)
                .containsExactly(new PresenceConnectionRegistry.ProjectUserKey(1L, 10L));
        assertThat(registry.hasActiveConnection(1L, 10L)).isFalse();
    }

    @Test
    @DisplayName("duplicate subscriptions in one session are counted once")
    void registerIgnoresDuplicateProjectUserInOneSession() {
        registry.register("session-1", 1L, 10L);
        registry.register("session-1", 1L, 10L);

        List<PresenceConnectionRegistry.ProjectUserKey> disconnected =
                registry.unregisterSession("session-1");

        assertThat(disconnected)
                .containsExactly(new PresenceConnectionRegistry.ProjectUserKey(1L, 10L));
    }
}
