package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminProjectMemberResponse {

    private Long userId;
    private String nickname;
    private String email;
    private ProjectMember.ProjectRole role;

    public static AdminProjectMemberResponse from(ProjectMember projectMember) {
        return new AdminProjectMemberResponse(
                projectMember.getUser().getId(),
                projectMember.getUser().getNickname(),
                projectMember.getUser().getEmail(),
                projectMember.getRole()
        );
    }
}
