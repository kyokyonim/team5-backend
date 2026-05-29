package com.team5.web_ide.domain.member.dto;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberAddRequest {

    @NotNull(message = "요청자 ID를 입력해주세요.")
    private Long requesterId;

    @NotNull(message = "추가할 유저 ID를 입력해주세요.")
    private Long userId;

    private ProjectMember.ProjectRole role = ProjectMember.ProjectRole.EDITOR;
}
