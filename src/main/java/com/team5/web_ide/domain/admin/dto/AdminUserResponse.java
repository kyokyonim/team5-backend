package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminUserResponse {

    private Long id;
    private String email;
    private String nickname;
    private User.Role role;
    private User.Status status;
    private User.Provider provider;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}
