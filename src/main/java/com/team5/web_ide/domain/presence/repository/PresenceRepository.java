package com.team5.web_ide.domain.presence.repository;

import com.team5.web_ide.domain.presence.entity.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    Optional<Presence> findByProjectIdAndUserId(Long projectId, Long userId);

    List<Presence> findAllByProjectIdAndStatusOrderByLastSeenAtDesc(
            Long projectId,
            Presence.PresenceStatus status
    );
}
