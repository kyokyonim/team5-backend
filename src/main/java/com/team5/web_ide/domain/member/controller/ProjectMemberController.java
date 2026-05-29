package com.team5.web_ide.domain.member.controller;

import com.team5.web_ide.domain.member.dto.MemberAddRequest;
import com.team5.web_ide.domain.member.dto.MemberRoleUpdateRequest;
import com.team5.web_ide.domain.member.dto.ProjectMemberResponse;
import com.team5.web_ide.domain.member.service.ProjectMemberService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody MemberAddRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "프로젝트 멤버가 추가되었습니다.",
                        projectMemberService.addMember(projectId, request)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectMemberResponse>>> getMembers(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long requesterId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "프로젝트 멤버 목록 조회 성공",
                projectMemberService.getMembers(projectId, requesterId)
        ));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<ApiResponse<ProjectMemberResponse>> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @Valid @RequestBody MemberRoleUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "프로젝트 멤버 권한이 변경되었습니다.",
                projectMemberService.updateMemberRole(projectId, memberId, request)
        ));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestParam(required = false) Long requesterId
    ) {
        projectMemberService.removeMember(projectId, memberId, requesterId);
        return ResponseEntity.ok(ApiResponse.success("프로젝트 멤버가 삭제되었습니다."));
    }
}
