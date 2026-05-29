package com.team5.web_ide.domain.project.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProjectErrorCode implements ErrorCode {

    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_NOT_FOUND", "존재하지 않는 프로젝트입니다."),
    PROJECT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PROJECT_ACCESS_DENIED", "프로젝트 접근 권한이 없습니다."),
    PROJECT_OWNER_REQUIRED(HttpStatus.FORBIDDEN, "PROJECT_OWNER_REQUIRED", "프로젝트 소유자만 수행할 수 있습니다."),
    INVALID_PROJECT_NAME(HttpStatus.BAD_REQUEST, "INVALID_PROJECT_NAME", "프로젝트 이름이 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    USER_STATUS_BLOCKED(HttpStatus.FORBIDDEN, "USER_STATUS_BLOCKED", "사용할 수 없는 계정입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
