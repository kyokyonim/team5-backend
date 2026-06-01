package com.team5.web_ide.domain.admin.controller;

import com.team5.web_ide.domain.admin.dto.AdminRecentChatResponse;
import com.team5.web_ide.domain.admin.service.AdminChatService;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/chats")
public class AdminChatController {

    private final AdminChatService adminChatService;

    @GetMapping("/recent")
    public ApiResponse<List<AdminRecentChatResponse>> getRecentChats(
            @RequestParam(required = false) Integer size,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "Recent chat activities retrieved successfully.",
                adminChatService.getRecentChats(getCurrentUserId(authentication), size)
        );
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof Integer userId) {
            return userId.longValue();
        }
        throw new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED);
    }
}
