package com.team5.web_ide.domain.invite.service;

import com.team5.web_ide.config.InviteProperties;
import com.team5.web_ide.domain.invite.dto.InviteAcceptResponse;
import com.team5.web_ide.domain.invite.dto.InviteCreateRequest;
import com.team5.web_ide.domain.invite.dto.InvitePreviewResponse;
import com.team5.web_ide.domain.invite.dto.InviteResponse;
import com.team5.web_ide.domain.invite.entity.ProjectInvite;
import com.team5.web_ide.domain.invite.exception.InviteErrorCode;
import com.team5.web_ide.domain.invite.exception.InviteException;
import com.team5.web_ide.domain.invite.repository.ProjectInviteRepository;
import com.team5.web_ide.domain.member.dto.ProjectMemberResponse;
import com.team5.web_ide.domain.member.entity.ProjectMember;
import com.team5.web_ide.domain.member.repository.ProjectMemberRepository;
import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.project.service.ProjectService;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectInviteService {

    private final ProjectInviteRepository projectInviteRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final InviteProperties inviteProperties;
    private final InviteEmailService inviteEmailService;

    @Transactional
    public InviteResponse createInvite(Long projectId, Long inviterId, InviteCreateRequest request) {
        Project project = projectService.findActiveProject(projectId);
        projectService.validateProjectOwner(projectId, inviterId);

        String email = request.getEmail().trim().toLowerCase();
        ProjectMember.ProjectRole role = resolveInviteRole(request.getRole());

        userRepository.findByEmail(email).ifPresent(user -> {
            if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
                throw new InviteException(InviteErrorCode.MEMBER_ALREADY_EXISTS);
            }
        });

        if (projectInviteRepository.existsByProjectIdAndInviteeEmailAndStatus(
                projectId, email, ProjectInvite.InviteStatus.PENDING)) {
            throw new InviteException(InviteErrorCode.INVITE_ALREADY_PENDING);
        }

        User inviter = findActiveUser(inviterId);
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(inviteProperties.getExpirationHours());

        ProjectInvite invite = projectInviteRepository.save(ProjectInvite.builder()
                .project(project)
                .inviteeEmail(email)
                .role(role)
                .token(token)
                .invitedBy(inviter)
                .expiresAt(expiresAt)
                .build());

        String inviteUrl = buildInviteUrl(token);
        inviteEmailService.sendInviteEmail(invite, inviteUrl);

        return InviteResponse.from(invite, inviteUrl);
    }

    @Transactional
    public InvitePreviewResponse previewInvite(String token) {
        ProjectInvite invite = findPendingInvite(token);
        if (invite.isExpired()) {
            invite.markExpired();
            projectInviteRepository.save(invite);
            throw new InviteException(InviteErrorCode.INVITE_EXPIRED);
        }
        return InvitePreviewResponse.from(invite);
    }

    @Transactional
    public InviteAcceptResponse acceptInvite(String token, Long userId) {
        ProjectInvite invite = findPendingInvite(token);

        if (invite.isExpired()) {
            invite.markExpired();
            projectInviteRepository.save(invite);
            throw new InviteException(InviteErrorCode.INVITE_EXPIRED);
        }

        User user = findActiveUser(userId);
        if (!invite.getInviteeEmail().equalsIgnoreCase(user.getEmail())) {
            throw new InviteException(InviteErrorCode.INVITE_EMAIL_MISMATCH);
        }

        Long projectId = invite.getProject().getId();
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            invite.markAccepted();
            projectInviteRepository.save(invite);
            ProjectMember existing = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                    .orElseThrow(() -> new InviteException(InviteErrorCode.USER_NOT_FOUND));
            return new InviteAcceptResponse(
                    projectId,
                    invite.getProject().getProjectName(),
                    ProjectMemberResponse.from(existing)
            );
        }

        ProjectMember member = projectMemberRepository.save(ProjectMember.builder()
                .project(invite.getProject())
                .user(user)
                .role(invite.getRole())
                .build());

        invite.markAccepted();
        projectInviteRepository.save(invite);

        return new InviteAcceptResponse(
                projectId,
                invite.getProject().getProjectName(),
                ProjectMemberResponse.from(member)
        );
    }

    public List<InviteResponse> getPendingInvites(Long projectId, Long requesterId) {
        projectService.findActiveProject(projectId);
        projectService.validateProjectOwner(projectId, requesterId);

        return projectInviteRepository
                .findAllByProjectIdAndStatusOrderByCreatedAtDesc(projectId, ProjectInvite.InviteStatus.PENDING)
                .stream()
                .map(invite -> InviteResponse.from(invite, buildInviteUrl(invite.getToken())))
                .toList();
    }

    private ProjectInvite findPendingInvite(String token) {
        ProjectInvite invite = projectInviteRepository.findByToken(token)
                .orElseThrow(() -> new InviteException(InviteErrorCode.INVITE_NOT_FOUND));

        if (invite.getStatus() == ProjectInvite.InviteStatus.ACCEPTED) {
            throw new InviteException(InviteErrorCode.INVITE_ALREADY_ACCEPTED);
        }
        if (invite.getStatus() != ProjectInvite.InviteStatus.PENDING) {
            throw new InviteException(InviteErrorCode.INVITE_NOT_FOUND);
        }
        return invite;
    }

    private String buildInviteUrl(String token) {
        String base = inviteProperties.getBaseUrl().replaceAll("/$", "");
        return base + "/invite/" + token;
    }

    private ProjectMember.ProjectRole resolveInviteRole(ProjectMember.ProjectRole role) {
        if (role == null || role == ProjectMember.ProjectRole.OWNER) {
            throw new InviteException(InviteErrorCode.INVITE_ROLE_INVALID);
        }
        return role;
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InviteException(InviteErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new InviteException(InviteErrorCode.INVITE_UNAUTHORIZED);
        }
        return user;
    }
}
