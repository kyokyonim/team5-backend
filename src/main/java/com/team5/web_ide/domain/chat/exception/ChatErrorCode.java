package com.team5.web_ide.domain.chat.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found."),
    CHAT_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "CHAT_CONTENT_EMPTY", "Message content is required."),
    CHAT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "CHAT_CONTENT_TOO_LONG", "Message must be 2000 characters or fewer."),
    CHAT_INVALID_CURSOR(HttpStatus.BAD_REQUEST, "CHAT_INVALID_CURSOR", "before must be greater than 0.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
