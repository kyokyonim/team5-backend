package com.team5.web_ide.domain.project.dto;

import com.team5.web_ide.domain.project.entity.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ProjectCreateRequest {

    @NotBlank(message = "메세지를 입력하세요.")
    private String projectName;

    @NotBlank(message = "언어를 선택해주세요.")
    private Project.Language language;


}
