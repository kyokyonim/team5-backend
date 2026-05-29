package com.team5.web_ide.domain.project.controller;

import com.team5.web_ide.domain.project.dto.ProjectCreateRequest;
import com.team5.web_ide.domain.project.dto.ProjectResponse;
import com.team5.web_ide.domain.project.dto.ProjectUpdateRequest;
import com.team5.web_ide.domain.project.service.ProjectService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody ProjectCreateRequest request
    ) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("프로젝트가 생성되었습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(
            @RequestParam(required = false) Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "프로젝트 목록 조회 성공",
                projectService.getProjects(userId)
        ));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long requesterId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "프로젝트 조회 성공",
                projectService.getProject(projectId, requesterId)
        ));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "프로젝트가 수정되었습니다.",
                projectService.updateProject(projectId, request)
        ));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long requesterId
    ) {
        projectService.deleteProject(projectId, requesterId);
        return ResponseEntity.ok(ApiResponse.success("프로젝트가 삭제되었습니다."));
    }
}
