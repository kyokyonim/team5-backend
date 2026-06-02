package com.team5.web_ide.domain.invite.exception;

import com.team5.web_ide.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum InviteErrorCode implements ErrorCode {

    INVITE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "INVITE_UNAUTHORIZED", "인증이 필요합니다."),
    INVITE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "INVITE_ACCESS_DENIED", "초대를 발송할 권한이 없습니다."),
    INVITE_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITE_NOT_FOUND", "유효하지 않은 초대 링크입니다."),
    INVITE_EXPIRED(HttpStatus.GONE, "INVITE_EXPIRED", "만료된 초대 링크입니다."),
    INVITE_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "INVITE_ALREADY_ACCEPTED", "이미 수락된 초대입니다."),
    INVITE_EMAIL_MISMATCH(HttpStatus.FORBIDDEN, "INVITE_EMAIL_MISMATCH", "초대된 이메일 계정으로 로그인해야 합니다."),
    INVITE_ALREADY_PENDING(HttpStatus.CONFLICT, "INVITE_ALREADY_PENDING", "이미 대기 중인 초대가 있습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_ALREADY_EXISTS", "이미 프로젝트에 참여 중인 사용자입니다."),
    INVITE_ROLE_INVALID(HttpStatus.BAD_REQUEST, "INVITE_ROLE_INVALID", "초대 권한이 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
