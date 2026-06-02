package com.team5.web_ide.domain.file.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.team5.web_ide.domain.file.entity.FileType;
import com.team5.web_ide.domain.file.entity.ProjectFile;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonPropertyOrder({
        "id",
        "name",
        "type",
        "parentId",
        "path",
        "language",
        "version",
        "children"
})
public class FileTreeResponse {

    private final Long id;
    private final String name;
    private final FileType type;
    private final Long parentId;
    private final String path;
    private final String language;
    private final Long version;
    private final List<FileTreeResponse> children;

    @Builder
    private FileTreeResponse(
            Long id,
            String name,
            FileType type,
            Long parentId,
            String path,
            String language,
            Long version,
            List<FileTreeResponse> children
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentId = parentId;
        this.path = path;
        this.language = language;
        this.version = version;
        this.children = children != null ? children : new ArrayList<>();
    }

    public static FileTreeResponse from(ProjectFile file) {
        return FileTreeResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .type(file.getType())
                .parentId(file.getParentId())
                .path(file.getPath())
                .language(file.getLanguage())
                .version(file.getVersion())
                .children(new ArrayList<>())
                .build();
    }

    public void addChild(FileTreeResponse child) {
        this.children.add(child);
    }
}