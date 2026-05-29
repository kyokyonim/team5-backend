package com.team5.web_ide.domain.member.dto;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRoleUpdateRequest {

    @NotNull(message = "요청자 ID를 입력해주세요.")
    private Long requesterId;

    @NotNull(message = "변경할 권한을 입력해주세요.")
    private ProjectMember.ProjectRole role;
}
