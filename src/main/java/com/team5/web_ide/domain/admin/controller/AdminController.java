package com.team5.web_ide.domain.admin.controller;

import com.team5.web_ide.domain.admin.dto.AdminDashboardStatsResponse;
import com.team5.web_ide.domain.admin.dto.AdminPresenceResponse;
import com.team5.web_ide.domain.admin.dto.AdminProjectResponse;
import com.team5.web_ide.domain.admin.exception.AdminErrorCode;
import com.team5.web_ide.domain.admin.exception.AdminException;
import com.team5.web_ide.domain.admin.service.AdminService;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminDashboardStatsResponse>> getDashboardStats(
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "관리자 대시보드 통계 조회 성공",
                adminService.getDashboardStats(getCurrentUserId(authentication))
        ));
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<AdminProjectResponse>>> getProjects(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "관리자 프로젝트 목록 조회 성공",
                adminService.getProjects(getCurrentUserId(authentication))
        ));
    }

    @GetMapping("/presence")
    public ResponseEntity<ApiResponse<List<AdminPresenceResponse>>> getActivePresences(
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "관리자 활성 접속자 목록 조회 성공",
                adminService.getActivePresences(getCurrentUserId(authentication))
        ));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AdminException(AdminErrorCode.ADMIN_UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long userId)) {
            throw new AdminException(AdminErrorCode.ADMIN_UNAUTHORIZED);
        }
        return userId;
    }
}
