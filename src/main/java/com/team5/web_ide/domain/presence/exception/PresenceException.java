package com.team5.web_ide.domain.presence.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class PresenceException extends ApiException {

    public PresenceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
