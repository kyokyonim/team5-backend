package com.team5.web_ide.domain.file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileCreateRequest {

    private Long parentId;

    @NotBlank(message = "파일명은 필수입니다.")
    @Size(max = 255, message = "파일명은 255자를 초과할 수 없습니다.")
    private String name;

    private String content;

    @Size(max = 50, message = "언어 정보는 50자를 초과할 수 없습니다.")
    private String language;
}
