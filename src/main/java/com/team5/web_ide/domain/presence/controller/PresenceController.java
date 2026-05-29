package com.team5.web_ide.domain.presence.controller;

import com.team5.web_ide.domain.presence.dto.PresenceResponse;
import com.team5.web_ide.domain.presence.dto.PresenceUpdateRequest;
import com.team5.web_ide.domain.presence.service.PresenceService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @PutMapping
    public ResponseEntity<ApiResponse<PresenceResponse>> updatePresence(
            @PathVariable Long projectId,
            @Valid @RequestBody PresenceUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "접속 상태가 갱신되었습니다.",
                presenceService.updatePresence(projectId, request)
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PresenceResponse>>> getOnlinePresence(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long requesterId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "접속자 목록 조회 성공",
                presenceService.getOnlinePresence(projectId, requesterId)
        ));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<PresenceResponse>> markOffline(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "접속 상태가 오프라인으로 변경되었습니다.",
                presenceService.markOffline(projectId, userId)
        ));
    }
}
