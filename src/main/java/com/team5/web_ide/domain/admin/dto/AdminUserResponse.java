package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AdminUserResponse {

    private Long userId;
    private String nickname;
    private String email;
    private User.Status status;
    private LocalDate joinedAt;

    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt().toLocalDate()
        );
    }
}
