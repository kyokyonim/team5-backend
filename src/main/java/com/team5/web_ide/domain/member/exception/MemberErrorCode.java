package com.team5.web_ide.domain.member.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "프로젝트 멤버를 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_ALREADY_EXISTS", "이미 프로젝트에 참여 중인 멤버입니다."),
    MEMBER_ROLE_INVALID(HttpStatus.BAD_REQUEST, "MEMBER_ROLE_INVALID", "멤버 권한이 올바르지 않습니다."),
    MEMBER_OWNER_CANNOT_BE_REMOVED(HttpStatus.BAD_REQUEST, "MEMBER_OWNER_CANNOT_BE_REMOVED", "프로젝트 소유자는 삭제할 수 없습니다."),
    MEMBER_OWNER_ROLE_CANNOT_BE_CHANGED(HttpStatus.BAD_REQUEST, "MEMBER_OWNER_ROLE_CANNOT_BE_CHANGED", "프로젝트 소유자 권한은 변경할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    USER_STATUS_BLOCKED(HttpStatus.FORBIDDEN, "USER_STATUS_BLOCKED", "사용할 수 없는 계정입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
