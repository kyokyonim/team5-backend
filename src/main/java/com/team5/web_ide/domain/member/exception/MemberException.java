package com.team5.web_ide.domain.member.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class MemberException extends ApiException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
