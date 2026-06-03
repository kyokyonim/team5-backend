package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AdminProjectDetailResponse {

    private Long id;
    private String projectName;
    private Project.Language language;
    private Project.ProjectStatus status;
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int memberCount;
    private List<AdminProjectMemberResponse> members;

    public static AdminProjectDetailResponse from(
            Project project,
            List<AdminProjectMemberResponse> members
    ) {
        return new AdminProjectDetailResponse(
                project.getId(),
                project.getProjectName(),
                project.getLanguage(),
                project.getStatus(),
                project.getOwner().getId(),
                project.getOwner().getNickname(),
                project.getOwner().getEmail(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                members.size(),
                members
        );
    }
}
