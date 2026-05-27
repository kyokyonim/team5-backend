package com.team5.web_ide.domain.project.dto;

import com.team5.web_ide.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String projectName;
    private Project.Language language;
    private Project.ProjectStatus status;
    private Long ownerId;

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getProjectName(),
                project.getLanguage(),
                project.getStatus(),
                project.getOwner().getId()
        );
    }

}
