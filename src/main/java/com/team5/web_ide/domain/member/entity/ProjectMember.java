package com.team5.web_ide.domain.member.entity;

import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // M : 1 관계?? ProjectMember 여러 개 -> Project 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id", nullable = false)
    private Project project;

    // ProjectMember 여러 개 -> User 하나 (한 유저가 여러 프로젝트에 참여할 수 있다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    public enum ProjectRole {
        OWNER,
        EDITOR,
        VIEWER
    }


}
