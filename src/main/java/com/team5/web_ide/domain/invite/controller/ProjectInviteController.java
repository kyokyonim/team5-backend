package com.team5.web_ide.domain.invite.controller;

import com.team5.web_ide.domain.invite.dto.InviteAcceptResponse;
import com.team5.web_ide.domain.invite.dto.InviteCreateRequest;
import com.team5.web_ide.domain.invite.dto.InvitePreviewResponse;
import com.team5.web_ide.domain.invite.dto.InviteResponse;
import com.team5.web_ide.domain.invite.exception.InviteErrorCode;
import com.team5.web_ide.domain.invite.exception.InviteException;
import com.team5.web_ide.domain.invite.service.ProjectInviteService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectInviteController {

    private final ProjectInviteService projectInviteService;

    @PostMapping("/api/projects/{projectId}/invites")
    public ResponseEntity<ApiResponse<InviteResponse>> createInvite(
            @PathVariable Long projectId,
            @Valid @RequestBody InviteCreateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "프로젝트 초대 링크가 발송되었습니다.",
                projectInviteService.createInvite(projectId, getCurrentUserId(authentication), request)
        ));
    }

    @GetMapping("/api/projects/{projectId}/invites")
    public ResponseEntity<ApiResponse<List<InviteResponse>>> getPendingInvites(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "대기 중인 초대 목록 조회 성공",
                projectInviteService.getPendingInvites(projectId, getCurrentUserId(authentication))
        ));
    }

    @GetMapping("/api/invites/{token}")
    public ResponseEntity<ApiResponse<InvitePreviewResponse>> previewInvite(@PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.success(
                "초대 정보 조회 성공",
                projectInviteService.previewInvite(token)
        ));
    }

    @PostMapping("/api/invites/{token}/accept")
    public ResponseEntity<ApiResponse<InviteAcceptResponse>> acceptInvite(
            @PathVariable String token,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "프로젝트 초대를 수락했습니다.",
                projectInviteService.acceptInvite(token, getCurrentUserId(authentication))
        ));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new InviteException(InviteErrorCode.INVITE_UNAUTHORIZED);
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long userId)) {
            throw new InviteException(InviteErrorCode.INVITE_UNAUTHORIZED);
        }
        return userId;
    }
}
