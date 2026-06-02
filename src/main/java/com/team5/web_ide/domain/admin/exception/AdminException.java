package com.team5.web_ide.domain.admin.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class AdminException extends ApiException {

    public AdminException(ErrorCode errorCode) {
        super(errorCode);
    }
}
