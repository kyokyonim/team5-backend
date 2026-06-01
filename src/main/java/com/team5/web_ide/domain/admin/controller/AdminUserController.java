package com.team5.web_ide.domain.admin.controller;

import com.team5.web_ide.domain.admin.dto.AdminUserCategory;
import com.team5.web_ide.domain.admin.dto.AdminUserListResponse;
import com.team5.web_ide.domain.admin.dto.AdminUserStatusFilter;
import com.team5.web_ide.domain.admin.service.AdminUserService;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ApiResponse<AdminUserListResponse> getUsers(
            @RequestParam(required = false) AdminUserCategory category,
            @RequestParam(required = false) AdminUserStatusFilter status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "Admin users retrieved successfully.",
                adminUserService.getUsers(
                        getCurrentUserId(authentication),
                        category,
                        status,
                        keyword,
                        page,
                        size
                )
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
