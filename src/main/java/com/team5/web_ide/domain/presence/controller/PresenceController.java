package com.team5.web_ide.domain.presence.controller;

import com.team5.web_ide.domain.presence.dto.PresenceResponse;
import com.team5.web_ide.domain.presence.exception.PresenceErrorCode;
import com.team5.web_ide.domain.presence.exception.PresenceException;
import com.team5.web_ide.domain.presence.service.PresenceService;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @PutMapping
    public ResponseEntity<ApiResponse<PresenceResponse>> activateCurrentUser(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(ApiResponse.success(
                "현재 사용자가 활성 상태로 갱신되었습니다.",
                presenceService.activateCurrentUser(projectId, userId)
        ));
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new PresenceException(PresenceErrorCode.PRESENCE_UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long userId)) {
            throw new PresenceException(PresenceErrorCode.PRESENCE_UNAUTHORIZED);
        }

        return userId;
    }
}
