package com.team5.web_ide.domain.admin.service;

import com.team5.web_ide.config.PresenceProperties;
import com.team5.web_ide.domain.admin.dto.AdminDashboardStatsResponse;
import com.team5.web_ide.domain.admin.dto.AdminPresenceResponse;
import com.team5.web_ide.domain.admin.dto.AdminProjectResponse;
import com.team5.web_ide.domain.admin.dto.AdminUserResponse;
import com.team5.web_ide.domain.admin.exception.AdminErrorCode;
import com.team5.web_ide.domain.admin.exception.AdminException;
import com.team5.web_ide.domain.member.repository.ProjectMemberRepository;
import com.team5.web_ide.domain.presence.repository.PresenceRepository;
import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.project.repository.ProjectRepository;
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
public class AdminService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PresenceRepository presenceRepository;
    private final PresenceProperties presenceProperties;

    public AdminDashboardStatsResponse getDashboardStats(Long adminUserId) {
        validateAdmin(adminUserId);

        LocalDateTime activeSince = LocalDateTime.now()
                .minusSeconds(presenceProperties.getActiveThresholdSeconds());

        return new AdminDashboardStatsResponse(
                userRepository.count(),
                userRepository.countByStatus(User.Status.ACTIVE),
                userRepository.countByStatus(User.Status.BANNED),
                presenceRepository.countByLastSeenAtAfter(activeSince),
                projectRepository.findAllByStatusOrderByUpdatedAtDesc(Project.ProjectStatus.ACTIVE).size()
        );
    }

    public List<AdminUserResponse> getUsers(Long adminUserId) {
        validateAdmin(adminUserId);
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(AdminUserResponse::from)
                .toList();
    }

    public List<AdminProjectResponse> getProjects(Long adminUserId) {
        validateAdmin(adminUserId);
        return projectRepository.findAllByStatusOrderByUpdatedAtDesc(Project.ProjectStatus.ACTIVE)
                .stream()
                .map(project -> AdminProjectResponse.from(
                        project,
                        projectMemberRepository.countByProjectId(project.getId())
                ))
                .toList();
    }

    public List<AdminPresenceResponse> getActivePresences(Long adminUserId) {
        validateAdmin(adminUserId);

        LocalDateTime activeSince = LocalDateTime.now()
                .minusSeconds(presenceProperties.getActiveThresholdSeconds());

        return presenceRepository.findAllByLastSeenAtAfterOrderByLastSeenAtDesc(activeSince)
                .stream()
                .map(AdminPresenceResponse::from)
                .toList();
    }

    public void validateAdmin(Long userId) {
        if (userId == null) {
            throw new AdminException(AdminErrorCode.ADMIN_UNAUTHORIZED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AdminException(AdminErrorCode.USER_NOT_FOUND));

        if (user.getRole() != User.Role.ADMIN) {
            throw new AdminException(AdminErrorCode.ADMIN_FORBIDDEN);
        }
    }
}
