package com.team5.web_ide.domain.project.entity;

import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    // 프로젝트 이름 : 50자 내외
    @Column(nullable = false,length=50)
    @Enumerated(EnumType.STRING)
    private Language language;

    public enum Language {
        JAVA,
        JAVASCRIPT,
        PYTHON
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public enum ProjectStatus {
        ACTIVE,
        DELETED
    }


}
