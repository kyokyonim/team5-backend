package com.team5.web_ide.domain.comment.exception;

import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.ErrorCode;

public class CommentException extends ApiException {

    public CommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
