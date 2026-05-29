package com.team5.web_ide.domain.presence.entity;

import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "project_presence",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_project_presence_project_user",
                        columnNames = {"project_id", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Presence extends BaseEntity {

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
    private PresenceStatus status = PresenceStatus.ONLINE;

    @Column(length = 1000)
    private String currentFilePath;

    private Integer cursorLine;

    private Integer cursorColumn;

    @Column(nullable = false)
    private LocalDateTime lastSeenAt;

    @Builder
    public Presence(Project project, User user, PresenceStatus status, String currentFilePath, Integer cursorLine, Integer cursorColumn) {
        this.project = project;
        this.user = user;
        update(status, currentFilePath, cursorLine, cursorColumn);
    }

    public void update(PresenceStatus status, String currentFilePath, Integer cursorLine, Integer cursorColumn) {
        this.status = status == null ? PresenceStatus.ONLINE : status;
        this.currentFilePath = currentFilePath;
        this.cursorLine = cursorLine;
        this.cursorColumn = cursorColumn;
        this.lastSeenAt = LocalDateTime.now();
    }

    public void markOffline() {
        this.status = PresenceStatus.OFFLINE;
        this.lastSeenAt = LocalDateTime.now();
    }

    public enum PresenceStatus {
        ONLINE,
        AWAY,
        OFFLINE
    }
}
