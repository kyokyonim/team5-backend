package com.team5.web_ide.domain.presence.service;

import com.team5.web_ide.domain.presence.dto.PresenceResponse;
import com.team5.web_ide.domain.presence.dto.PresenceUpdateRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Transactional
    public PresenceResponse updatePresence(Long projectId, PresenceUpdateRequest request) {
        Project project = projectService.findActiveProject(projectId);
        User user = findActiveUser(request.getUserId());
        projectService.validateProjectMember(projectId, user.getId());

        Presence presence = presenceRepository.findByProjectIdAndUserId(projectId, user.getId())
                .orElseGet(() -> Presence.builder()
                        .project(project)
                        .user(user)
                        .build());

        presence.update(
                request.getStatus(),
                normalizeFilePath(request.getCurrentFilePath()),
                request.getCursorLine(),
                request.getCursorColumn()
        );

        return PresenceResponse.from(presenceRepository.save(presence));
    }

    public List<PresenceResponse> getOnlinePresence(Long projectId, Long requesterId) {
        projectService.findActiveProject(projectId);
        validatePresenceAccess(projectId, requesterId);

        return presenceRepository.findAllByProjectIdAndStatusOrderByLastSeenAtDesc(
                        projectId,
                        Presence.PresenceStatus.ONLINE
                )
                .stream()
                .map(PresenceResponse::from)
                .toList();
    }

    @Transactional
    public PresenceResponse markOffline(Long projectId, Long userId) {
        projectService.findActiveProject(projectId);
        validatePresenceAccess(projectId, userId);

        Presence presence = presenceRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new PresenceException(PresenceErrorCode.PRESENCE_ACCESS_DENIED));
        presence.markOffline();
        return PresenceResponse.from(presence);
    }

    private void validatePresenceAccess(Long projectId, Long userId) {
        if (userId == null) {
            throw new PresenceException(PresenceErrorCode.PRESENCE_ACCESS_DENIED);
        }
        projectService.validateProjectMember(projectId, userId);
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PresenceException(PresenceErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new PresenceException(PresenceErrorCode.USER_STATUS_BLOCKED);
        }
        return user;
    }

    private String normalizeFilePath(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return null;
        }
        return filePath.trim();
    }
}
