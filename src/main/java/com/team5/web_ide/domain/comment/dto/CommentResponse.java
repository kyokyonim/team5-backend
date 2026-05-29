package com.team5.web_ide.domain.comment.dto;

import com.team5.web_ide.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private Long projectId;
    private Long fileId;
    private Long userId;
    private String nickname;
    private String profileColor;
    private Integer lineNumber;
    private String content;
    private boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getProject().getId(),
                comment.getFileId(),
                comment.getWriter().getId(),
                comment.getWriter().getNickname(),
                comment.getWriter().getProfileColor(),
                comment.getLineNumber(),
                comment.getContent(),
                comment.isResolved(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
