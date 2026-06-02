package com.team5.web_ide.domain.admin.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminUserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),
    USER_ALREADY_BANNED(HttpStatus.BAD_REQUEST, "USER_ALREADY_BANNED", "이미 정지된 계정입니다."),
    USER_ALREADY_ACTIVE(HttpStatus.BAD_REQUEST, "USER_ALREADY_ACTIVE", "이미 활성 상태인 계정입니다."),
    ADMIN_SELF_SUSPEND_DENIED(HttpStatus.BAD_REQUEST, "ADMIN_SELF_SUSPEND_DENIED", "관리자는 자기 자신을 정지할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
