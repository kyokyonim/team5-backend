package com.team5.web_ide.domain.activitylog.entity;

import com.team5.web_ide.domain.activitylog.enums.ActivityAction;
import com.team5.web_ide.domain.activitylog.enums.ActivityTargetType;
import com.team5.web_ide.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_logs_created_at", columnList = "created_at"),
                @Index(name = "idx_activity_logs_actor_id", columnList = "actor_id"),
                @Index(name = "idx_activity_logs_project_id", columnList = "project_id"),
                @Index(name = "idx_activity_logs_action_created_at", columnList = "action, created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private ActivityTargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Builder
    public ActivityLog(
            Long actorId,
            ActivityAction action,
            ActivityTargetType targetType,
            Long targetId,
            Long projectId,
            String message,
            String ipAddress,
            String userAgent
    ) {
        this.actorId = actorId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.projectId = projectId;
        this.message = message;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
