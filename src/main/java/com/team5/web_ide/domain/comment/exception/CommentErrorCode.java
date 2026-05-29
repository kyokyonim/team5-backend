package com.team5.web_ide.domain.comment.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글을 찾을 수 없습니다."),
    COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "COMMENT_ACCESS_DENIED", "댓글에 접근할 권한이 없습니다."),
    COMMENT_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "COMMENT_CONTENT_EMPTY", "댓글 내용을 입력해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    USER_STATUS_BLOCKED(HttpStatus.FORBIDDEN, "USER_STATUS_BLOCKED", "사용할 수 없는 계정입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
