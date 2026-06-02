package com.team5.web_ide.domain.presence.service;

import com.team5.web_ide.config.PresenceProperties;
import com.team5.web_ide.domain.presence.dto.PresenceConfigResponse;
import com.team5.web_ide.domain.presence.dto.PresenceResponse;
import com.team5.web_ide.domain.presence.entity.Presence;
import com.team5.web_ide.domain.presence.exception.PresenceErrorCode;
import com.team5.web_ide.domain.presence.exception.PresenceException;
import com.team5.web_ide.domain.presence.repository.PresenceRepository;
import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.project.service.ProjectService;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final PresenceProperties presenceProperties;

    public PresenceConfigResponse getConfig() {
        return new PresenceConfigResponse(
                presenceProperties.getHeartbeatIntervalMs(),
                presenceProperties.getActiveThresholdSeconds()
        );
    }

    @Transactional
    public PresenceResponse activateCurrentUser(Long projectId, Long userId) {
        Project project = projectService.findActiveProject(projectId);
        User user = findActiveUser(userId);
        projectService.validateProjectMember(projectId, userId);

        Presence presence = presenceRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseGet(() -> Presence.builder()
                        .project(project)
                        .user(user)
                        .build());

        presence.activate();
        return PresenceResponse.from(presenceRepository.save(presence));
    }

    public List<PresenceResponse> getActiveUsers(Long projectId, Long requesterId) {
        projectService.findActiveProject(projectId);
        projectService.validateProjectMember(projectId, requesterId);

        LocalDateTime activeSince = LocalDateTime.now()
                .minusSeconds(presenceProperties.getActiveThresholdSeconds());

        return presenceRepository
                .findAllByProjectIdAndLastSeenAtAfterOrderByLastSeenAtDesc(projectId, activeSince)
                .stream()
                .map(PresenceResponse::from)
                .toList();
    }

    public long countActiveConnections() {
        LocalDateTime activeSince = LocalDateTime.now()
                .minusSeconds(presenceProperties.getActiveThresholdSeconds());
        return presenceRepository.countByLastSeenAtAfter(activeSince);
    }

    public List<PresenceResponse> getAllActiveConnections() {
        LocalDateTime activeSince = LocalDateTime.now()
                .minusSeconds(presenceProperties.getActiveThresholdSeconds());

        return presenceRepository.findAllByLastSeenAtAfterOrderByLastSeenAtDesc(activeSince)
                .stream()
                .map(PresenceResponse::from)
                .toList();
    }

    private User findActiveUser(Long userId) {
        if (userId == null) {
            throw new PresenceException(PresenceErrorCode.PRESENCE_UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PresenceException(PresenceErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new PresenceException(PresenceErrorCode.USER_STATUS_BLOCKED);
        }

        return user;
    }
}
