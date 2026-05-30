package com.team5.web_ide.domain.file.dto;

import com.team5.web_ide.domain.file.entity.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileNameValidateRequest {

    private Long parentId;

    private Long excludeId;

    @NotBlank(message = "파일 또는 폴더명은 필수입니다.")
    @Size(max = 255, message = "파일 또는 폴더명은 255자를 초과할 수 없습니다.")
    private String name;

    @NotNull(message = "파일 또는 폴더 타입은 필수입니다.")
    private FileType type;
}