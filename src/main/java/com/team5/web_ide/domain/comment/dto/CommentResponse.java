package com.team5.web_ide.domain.comment.dto;

import com.team5.web_ide.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private Long projectId;
    private Long writerId;
    private String writerNickname;
    private String writerProfileColor;
    private String filePath;
    private Integer lineNumber;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getProject().getId(),
                comment.getWriter().getId(),
                comment.getWriter().getNickname(),
                comment.getWriter().getProfileColor(),
                comment.getFilePath(),
                comment.getLineNumber(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
