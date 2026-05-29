package com.team5.web_ide.domain.project.dto;

import com.team5.web_ide.domain.project.entity.Project;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectUpdateRequest {

    @NotNull(message = "요청자 ID를 입력해주세요.")
    private Long requesterId;

    @Size(max = 50, message = "프로젝트 이름은 50자 이하여야 합니다.")
    private String projectName;

    private Project.Language language;
}
