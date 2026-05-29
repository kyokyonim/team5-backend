package com.team5.web_ide.domain.member.entity;

import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "project_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_member_project_user",
                        columnNames = {"project_id", "user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectRole role;

    @Builder
    public ProjectMember(Project project, User user, ProjectRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public void updateRole(ProjectRole role) {
        this.role = role;
    }

    public enum ProjectRole {
        OWNER,
        EDITOR,
        VIEWER
    }
}
