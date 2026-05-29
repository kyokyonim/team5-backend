package com.team5.web_ide.domain.comment.controller;

import com.team5.web_ide.domain.comment.dto.CommentCreateRequest;
import com.team5.web_ide.domain.comment.dto.CommentResponse;
import com.team5.web_ide.domain.comment.dto.CommentUpdateRequest;
import com.team5.web_ide.domain.comment.exception.CommentErrorCode;
import com.team5.web_ide.domain.comment.exception.CommentException;
import com.team5.web_ide.domain.comment.service.CommentService;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files/{fileId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            @RequestParam(required = false) Boolean resolved,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(ApiResponse.success(
                "댓글 목록 조회 성공",
                commentService.getComments(projectId, fileId, resolved, userId)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            @RequestBody(required = false) CommentCreateRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "댓글이 작성되었습니다.",
                        commentService.createComment(projectId, fileId, userId, request)
                ));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            @PathVariable Long commentId,
            @RequestBody(required = false) CommentUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(ApiResponse.success(
                "댓글이 수정되었습니다.",
                commentService.updateComment(projectId, fileId, commentId, userId, request)
        ));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        Long userId = getCurrentUserId(authentication);
        commentService.deleteComment(projectId, fileId, commentId, userId);

        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CommentException(CommentErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long userId)) {
            throw new CommentException(CommentErrorCode.UNAUTHORIZED);
        }

        return userId;
    }
}
