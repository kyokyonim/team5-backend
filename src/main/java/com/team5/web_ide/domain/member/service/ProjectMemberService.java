package com.team5.web_ide.domain.member.service;

import com.team5.web_ide.domain.member.dto.MemberAddRequest;
import com.team5.web_ide.domain.member.dto.MemberRoleUpdateRequest;
import com.team5.web_ide.domain.member.dto.ProjectMemberResponse;
import com.team5.web_ide.domain.member.entity.ProjectMember;
import com.team5.web_ide.domain.member.exception.MemberErrorCode;
import com.team5.web_ide.domain.member.exception.MemberException;
import com.team5.web_ide.domain.member.repository.ProjectMemberRepository;
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
public class ProjectMemberService {

    private final ProjectService projectService;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectMemberResponse addMember(Long projectId, MemberAddRequest request) {
        Project project = projectService.findActiveProject(projectId);
        projectService.validateProjectOwner(project.getId(), request.getRequesterId());

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, request.getUserId())) {
            throw new MemberException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }

        ProjectMember.ProjectRole role = resolveAssignableRole(request.getRole());
        User user = findActiveUser(request.getUserId());

        ProjectMember member = projectMemberRepository.save(ProjectMember.builder()
                .project(project)
                .user(user)
                .role(role)
                .build());

        return ProjectMemberResponse.from(member);
    }

    public List<ProjectMemberResponse> getMembers(Long projectId, Long requesterId) {
        projectService.findActiveProject(projectId);
        projectService.validateProjectMember(projectId, requesterId);

        return projectMemberRepository.findAllByProjectIdOrderByIdAsc(projectId)
                .stream()
                .map(ProjectMemberResponse::from)
                .toList();
    }

    @Transactional
    public ProjectMemberResponse updateMemberRole(Long projectId, Long memberId, MemberRoleUpdateRequest request) {
        projectService.findActiveProject(projectId);
        projectService.validateProjectOwner(projectId, request.getRequesterId());

        ProjectMember member = findMember(projectId, memberId);
        if (member.getRole() == ProjectMember.ProjectRole.OWNER) {
            throw new MemberException(MemberErrorCode.MEMBER_OWNER_ROLE_CANNOT_BE_CHANGED);
        }

        member.updateRole(resolveAssignableRole(request.getRole()));
        return ProjectMemberResponse.from(member);
    }

    @Transactional
    public void removeMember(Long projectId, Long memberId, Long requesterId) {
        projectService.findActiveProject(projectId);
        projectService.validateProjectOwner(projectId, requesterId);

        ProjectMember member = findMember(projectId, memberId);
        if (member.getRole() == ProjectMember.ProjectRole.OWNER) {
            throw new MemberException(MemberErrorCode.MEMBER_OWNER_CANNOT_BE_REMOVED);
        }

        projectMemberRepository.delete(member);
    }

    private ProjectMember findMember(Long projectId, Long memberId) {
        return projectMemberRepository.findById(memberId)
                .filter(member -> member.getProject().getId().equals(projectId))
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new MemberException(MemberErrorCode.USER_STATUS_BLOCKED);
        }
        return user;
    }

    private ProjectMember.ProjectRole resolveAssignableRole(ProjectMember.ProjectRole role) {
        ProjectMember.ProjectRole resolvedRole = role == null ? ProjectMember.ProjectRole.EDITOR : role;
        if (resolvedRole == ProjectMember.ProjectRole.OWNER) {
            throw new MemberException(MemberErrorCode.MEMBER_OWNER_ROLE_CANNOT_BE_CHANGED);
        }
        return resolvedRole;
    }
}
