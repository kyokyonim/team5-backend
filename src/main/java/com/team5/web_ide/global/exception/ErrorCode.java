package com.team5.web_ide.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String code();
    String message();
    HttpStatus status();
}
