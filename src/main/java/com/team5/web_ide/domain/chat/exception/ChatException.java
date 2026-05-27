package com.team5.web_ide.domain.chat.exception;

import com.team5.web_ide.global.exception.ApiException;

public class ChatException extends ApiException {
    public ChatException(ChatErrorCode errorCode) {
        super(errorCode);
    }
}
