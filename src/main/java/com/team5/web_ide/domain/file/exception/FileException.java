package com.team5.web_ide.domain.file.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class FileException extends ApiException {

    public FileException(ErrorCode errorCode) {
        super(errorCode);
    }
}