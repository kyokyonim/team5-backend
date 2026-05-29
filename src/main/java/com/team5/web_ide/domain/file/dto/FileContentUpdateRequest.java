package com.team5.web_ide.domain.file.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileContentUpdateRequest {

    @NotNull(message = "파일 내용은 null일 수 없습니다.")
    @Size(max = 1_000_000, message = "파일 내용은 1MB를 초과할 수 없습니다.")
    private String content;

    @NotNull(message = "파일 버전은 필수입니다.")
    private Long version;
}