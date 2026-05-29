package com.team5.web_ide.domain.presence.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PresenceErrorCode implements ErrorCode {

    PRESENCE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "PRESENCE_UNAUTHORIZED", "인증이 필요합니다."),
    PRESENCE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PRESENCE_ACCESS_DENIED", "프로젝트 활성 상태를 갱신할 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    USER_STATUS_BLOCKED(HttpStatus.FORBIDDEN, "USER_STATUS_BLOCKED", "사용할 수 없는 계정입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
