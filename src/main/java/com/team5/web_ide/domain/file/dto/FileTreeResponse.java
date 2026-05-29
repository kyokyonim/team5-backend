package com.team5.web_ide.domain.file.dto;

import com.team5.web_ide.domain.file.entity.FileType;
import com.team5.web_ide.domain.file.entity.ProjectFile;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FileTreeResponse {

    private final Long id;
    private final String name;
    private final FileType type;
    private final String path;
    private final Long parentId;
    private final String language;
    private final Long version;
    private final List<FileTreeResponse> children;

    @Builder
    private FileTreeResponse(
            Long id,
            String name,
            FileType type,
            String path,
            Long parentId,
            String language,
            Long version,
            List<FileTreeResponse> children
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.parentId = parentId;
        this.language = language;
        this.version = version;
        this.children = children != null ? children : new ArrayList<>();
    }

    public static FileTreeResponse from(ProjectFile file) {
        return FileTreeResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .type(file.getType())
                .path(file.getPath())
                .parentId(file.getParentId())
                .language(file.getLanguage())
                .version(file.getVersion())
                .children(new ArrayList<>())
                .build();
    }

    public void addChild(FileTreeResponse child) {
        this.children.add(child);
    }
}