package com.team5.web_ide.domain.file.entity;

import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "project_files",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_file_parent_name",
                        columnNames = {"project_id", "parent_id", "name"}
                )
        }
)
public class ProjectFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;

    @Column(nullable = false, length = 1000)
    private String path;

    @Lob
    private String content;

    private String language;

    @Column(nullable = false)
    private Long version = 1L;

    @Builder
    public ProjectFile(
            Long projectId,
            Long parentId,
            String name,
            FileType type,
            String path,
            String content,
            String language
    ) {
        this.projectId = projectId;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.path = path;
        this.content = content;
        this.language = language;
        this.version = 1L;
    }

    public boolean isFile() {
        return this.type == FileType.FILE;
    }

    public boolean isFolder() {
        return this.type == FileType.FOLDER;
    }

    public void updateContent(String content) {
        this.content = content;
        this.version++;
    }

    public void rename(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void updatePath(String path) {
        this.path = path;
    }
}