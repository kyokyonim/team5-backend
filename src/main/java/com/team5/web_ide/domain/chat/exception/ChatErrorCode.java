package com.team5.web_ide.domain.chat.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND", "존재하지 않는 프로젝트입니다.", HttpStatus.NOT_FOUND),
    CHAT_CONTENT_EMPTY("CHAT_CONTENT_EMPTY", "메시지 내용은 필수입니다.", HttpStatus.BAD_REQUEST),
    CHAT_CONTENT_TOO_LONG("CHAT_CONTENT_TOO_LONG", "메시지는 2,000자를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHAT_INVALID_CURSOR("CHAT_INVALID_CURSOR", "before는 1 이상의 값이어야 합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
