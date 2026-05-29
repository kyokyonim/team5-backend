package com.team5.web_ide.domain.file.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileRenameRequest {

    @NotBlank(message = "변경할 이름은 필수입니다.")
    @Size(max = 255, message = "파일 또는 폴더명은 255자를 초과할 수 없습니다.")
    private String name;
}