package com.team5.web_ide.domain.chat.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    PROJECT_NOT_FOUND("PROJECT_NOT_FOUND", "Project not found.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found.", HttpStatus.NOT_FOUND),
    CHAT_CONTENT_EMPTY("CHAT_CONTENT_EMPTY", "Message content is required.", HttpStatus.BAD_REQUEST),
    CHAT_CONTENT_TOO_LONG("CHAT_CONTENT_TOO_LONG", "Message must be 2000 characters or fewer.", HttpStatus.BAD_REQUEST),
    CHAT_INVALID_CURSOR("CHAT_INVALID_CURSOR", "before must be greater than 0.", HttpStatus.BAD_REQUEST);

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
