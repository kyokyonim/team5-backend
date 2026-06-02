package com.team5.web_ide.domain.invite.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class InviteException extends ApiException {

    public InviteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
