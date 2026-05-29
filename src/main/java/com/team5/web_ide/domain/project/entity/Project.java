package com.team5.web_ide.domain.project.entity;

import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String projectName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Builder
    public Project(String projectName, Language language, User owner) {
        this.projectName = projectName;
        this.language = language;
        this.owner = owner;
        this.status = ProjectStatus.ACTIVE;
    }

    public void update(String projectName, Language language) {
        if (projectName != null) {
            this.projectName = projectName;
        }
        if (language != null) {
            this.language = language;
        }
    }

    public void delete() {
        this.status = ProjectStatus.DELETED;
    }

    public boolean isDeleted() {
        return this.status == ProjectStatus.DELETED;
    }

    public enum Language {
        JAVA,
        JAVASCRIPT,
        PYTHON
    }

    public enum ProjectStatus {
        ACTIVE,
        DELETED
    }
}
