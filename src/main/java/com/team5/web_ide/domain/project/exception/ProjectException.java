package com.team5.web_ide.domain.project.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class ProjectException extends ApiException {

    public ProjectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
