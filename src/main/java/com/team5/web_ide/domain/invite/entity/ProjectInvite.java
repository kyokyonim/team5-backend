package com.team5.web_ide.domain.invite.entity;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import com.team5.web_ide.domain.project.entity.Project;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_invites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectInvite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectMember.ProjectRole role;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InviteStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_id", nullable = false)
    private User invitedBy;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime acceptedAt;

    @Builder
    public ProjectInvite(
            Project project,
            String inviteeEmail,
            ProjectMember.ProjectRole role,
            String token,
            User invitedBy,
            LocalDateTime expiresAt
    ) {
        this.project = project;
        this.inviteeEmail = inviteeEmail.trim().toLowerCase();
        this.role = role;
        this.token = token;
        this.invitedBy = invitedBy;
        this.expiresAt = expiresAt;
        this.status = InviteStatus.PENDING;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAccepted() {
        this.status = InviteStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void markExpired() {
        this.status = InviteStatus.EXPIRED;
    }

    public enum InviteStatus {
        PENDING,
        ACCEPTED,
        EXPIRED,
        CANCELLED
    }
}
