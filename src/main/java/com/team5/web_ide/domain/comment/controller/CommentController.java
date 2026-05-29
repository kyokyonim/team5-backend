package com.team5.web_ide.domain.comment.controller;

import com.team5.web_ide.domain.comment.dto.CommentCreateRequest;
import com.team5.web_ide.domain.comment.dto.CommentResponse;
import com.team5.web_ide.domain.comment.dto.CommentUpdateRequest;
import com.team5.web_ide.domain.comment.service.CommentService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long projectId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "댓글이 작성되었습니다.",
                        commentService.createComment(projectId, request)
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false) String filePath
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "댓글 목록 조회 성공",
                commentService.getComments(projectId, requesterId, filePath)
        ));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long projectId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "댓글이 수정되었습니다.",
                commentService.updateComment(projectId, commentId, request)
        ));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long projectId,
            @PathVariable Long commentId,
            @RequestParam(required = false) Long requesterId
    ) {
        commentService.deleteComment(projectId, commentId, requesterId);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다."));
    }
}
