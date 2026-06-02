package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminUserStatusResponse {

    private Long userId;
    private String nickname;
    private User.Status status;
    private String message;

    public static AdminUserStatusResponse suspended(User user) {
        return new AdminUserStatusResponse(
                user.getId(),
                user.getNickname(),
                user.getStatus(),
                "계정이 정지되었습니다."
        );
    }

    public static AdminUserStatusResponse activated(User user) {
        return new AdminUserStatusResponse(
                user.getId(),
                user.getNickname(),
                user.getStatus(),
                "계정이 활성화되었습니다."
        );
    }
}
