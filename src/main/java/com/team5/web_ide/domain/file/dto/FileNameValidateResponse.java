package com.team5.web_ide.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileNameValidateResponse {

    private Boolean valid;
    private String reason;

    public static FileNameValidateResponse valid() {
        return FileNameValidateResponse.builder()
                .valid(true)
                .reason(null)
                .build();
    }

    public static FileNameValidateResponse invalid(String reason) {
        return FileNameValidateResponse.builder()
                .valid(false)
                .reason(reason)
                .build();
    }
}