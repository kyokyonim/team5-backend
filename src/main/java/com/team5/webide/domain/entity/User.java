package com.team5.webide.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String passwordHash;

    @Column(nullable = false, length = 6)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(length = 7)
    @Builder.Default
    private String profileColor = "#000000";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Column
    @Builder.Default
    private int loginFailCount = 0;

    @Column(nullable = false)
    private boolean agreeService;

    @Column(nullable = false)
    private boolean agreeFinance;

    @Column
    @Builder.Default
    private boolean agreePrivacy = false;

    @Column
    @Builder.Default
    private int tokenVersion = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        USER, ADMIN
    }

    public enum Provider {
        LOCAL, GOOGLE
    }

    public enum Status {
        ACTIVE, DELETED, BANNED
    }
}