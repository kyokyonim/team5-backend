package com.team5.web_ide.domain.admin.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {

    ADMIN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ADMIN_UNAUTHORIZED", "인증이 필요합니다."),
    ADMIN_FORBIDDEN(HttpStatus.FORBIDDEN, "ADMIN_FORBIDDEN", "관리자 권한이 필요합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
