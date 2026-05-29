package com.team5.web_ide.domain.file.dto;

import com.team5.web_ide.domain.file.entity.FileType;
import com.team5.web_ide.domain.file.entity.ProjectFile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileCreateResponse {

    private Long id;
    private String name;
    private FileType type;
    private String path;
    private Long parentId;
    private String language;
    private Long version;

    public static FileCreateResponse from(ProjectFile file) {
        return FileCreateResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .type(file.getType())
                .path(file.getPath())
                .parentId(file.getParentId())
                .language(file.getLanguage())
                .version(file.getVersion())
                .build();
    }
}
