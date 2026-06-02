package com.team5.web_ide.domain.invite.dto;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InviteCreateRequest {

    @NotBlank(message = "초대할 이메일을 입력해 주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "멤버 권한을 지정해 주세요.")
    private ProjectMember.ProjectRole role;
}
