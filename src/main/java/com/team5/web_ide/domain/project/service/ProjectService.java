package com.team5.web_ide.domain.project.service;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import com.team5.web_ide.domain.member.repository.ProjectMemberRepository;
import com.team5.web_ide.domain.project.dto.ProjectCreateRequest;
import com.team5.web_ide.domain.project.dto.ProjectResponse;
import com.team5.web_ide.domain.project.dto.ProjectUpdateRequest;
import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.project.exception.ProjectErrorCode;
import com.team5.web_ide.domain.project.exception.ProjectException;
import com.team5.web_ide.domain.project.repository.ProjectRepository;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(ProjectCreateRequest request) {
        User owner = findActiveUser(request.getOwnerId());
        Project project = projectRepository.save(Project.builder()
                .projectName(normalizeProjectName(request.getProjectName()))
                .language(request.getLanguage())
                .owner(owner)
                .build());

        projectMemberRepository.save(ProjectMember.builder()
                .project(project)
                .user(owner)
                .role(ProjectMember.ProjectRole.OWNER)
                .build());

        return ProjectResponse.from(project);
    }

    public List<ProjectResponse> getProjects(Long userId) {
        if (userId == null) {
            return projectRepository.findAllByStatusOrderByUpdatedAtDesc(Project.ProjectStatus.ACTIVE)
                    .stream()
                    .map(ProjectResponse::from)
                    .toList();
        }

        findActiveUser(userId);
        return projectMemberRepository.findAllByUserId(userId)
                .stream()
                .map(ProjectMember::getProject)
                .filter(project -> !project.isDeleted())
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse getProject(Long projectId, Long requesterId) {
        Project project = findActiveProject(projectId);
        validateProjectMember(project.getId(), requesterId);
        return ProjectResponse.from(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectUpdateRequest request) {
        Project project = findActiveProject(projectId);
        validateProjectOwner(project.getId(), request.getRequesterId());

        String projectName = request.getProjectName() == null
                ? null
                : normalizeProjectName(request.getProjectName());
        project.update(projectName, request.getLanguage());
        return ProjectResponse.from(project);
    }

    @Transactional
    public void deleteProject(Long projectId, Long requesterId) {
        Project project = findActiveProject(projectId);
        validateProjectOwner(project.getId(), requesterId);
        project.delete();
    }

    public Project findActiveProject(Long projectId) {
        return projectRepository.findByIdAndStatus(projectId, Project.ProjectStatus.ACTIVE)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_NOT_FOUND));
    }

    public ProjectMember validateProjectMember(Long projectId, Long requesterId) {
        if (requesterId == null) {
            throw new ProjectException(ProjectErrorCode.PROJECT_ACCESS_DENIED);
        }
        return projectMemberRepository.findByProjectIdAndUserId(projectId, requesterId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.PROJECT_ACCESS_DENIED));
    }

    public ProjectMember validateProjectOwner(Long projectId, Long requesterId) {
        ProjectMember member = validateProjectMember(projectId, requesterId);
        if (member.getRole() != ProjectMember.ProjectRole.OWNER) {
            throw new ProjectException(ProjectErrorCode.PROJECT_OWNER_REQUIRED);
        }
        return member;
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ProjectException(ProjectErrorCode.USER_NOT_FOUND));
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new ProjectException(ProjectErrorCode.USER_STATUS_BLOCKED);
        }
        return user;
    }

    private String normalizeProjectName(String projectName) {
        String normalized = projectName == null ? "" : projectName.trim();
        if (normalized.isEmpty()) {
            throw new ProjectException(ProjectErrorCode.INVALID_PROJECT_NAME);
        }
        return normalized;
    }
}
