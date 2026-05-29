package com.team5.web_ide.domain.comment.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다"),
    PROJECT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 프로젝트에 접근 권한이 없습니다"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "파일이 존재하지 않습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "댓글이 존재하지 않습니다"),
    COMMENT_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "댓글 내용이 비어있습니다"),
    COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "댓글은 1000자를 초과할 수 없습니다"),
    INVALID_LINE_NUMBER(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "라인 번호가 올바르지 않습니다"),
    COMMENT_UPDATE_DENIED(HttpStatus.FORBIDDEN, "FORBIDDEN", "본인 댓글만 수정할 수 있습니다"),
    COMMENT_DELETE_DENIED(HttpStatus.FORBIDDEN, "FORBIDDEN", "본인 댓글만 삭제할 수 있습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
