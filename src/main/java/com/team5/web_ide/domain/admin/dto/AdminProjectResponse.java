package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminProjectResponse {

    private Long id;
    private String projectName;
    private Project.Language language;
    private Project.ProjectStatus status;
    private Long ownerId;
    private String ownerNickname;
    private String ownerEmail;
    private long memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminProjectResponse from(Project project, long memberCount) {
        return new AdminProjectResponse(
                project.getId(),
                project.getProjectName(),
                project.getLanguage(),
                project.getStatus(),
                project.getOwner().getId(),
                project.getOwner().getNickname(),
                project.getOwner().getEmail(),
                memberCount,
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
