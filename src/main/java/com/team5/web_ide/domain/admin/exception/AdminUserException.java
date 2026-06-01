package com.team5.web_ide.domain.admin.exception;

import com.team5.web_ide.global.exception.ApiException;

public class AdminUserException extends ApiException {

    public AdminUserException(AdminUserErrorCode errorCode) {
        super(errorCode);
    }
}
