package com.team5.web_ide.domain.file.dto;

import com.team5.web_ide.domain.file.entity.FileType;
import com.team5.web_ide.domain.file.entity.ProjectFile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileDetailResponse {

    private Long id;
    private Long projectId;
    private Long parentId;
    private String name;
    private FileType type;
    private String path;
    private String language;
    private String content;
    private Long version;

    private LockStatus lockStatus;
    private Boolean editable;
    private LockUserResponse lockedBy;
    private LocalDateTime lockedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FileDetailResponse from(
            ProjectFile file,
            LockStatus lockStatus,
            Boolean editable,
            LockUserResponse lockedBy,
            LocalDateTime lockedAt
    ) {
        return FileDetailResponse.builder()
                .id(file.getId())
                .projectId(file.getProjectId())
                .parentId(file.getParentId())
                .name(file.getName())
                .type(file.getType())
                .path(file.getPath())
                .language(file.getLanguage())
                .content(file.getContent())
                .version(file.getVersion())
                .lockStatus(lockStatus)
                .editable(editable)
                .lockedBy(lockedBy)
                .lockedAt(lockedAt)
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}