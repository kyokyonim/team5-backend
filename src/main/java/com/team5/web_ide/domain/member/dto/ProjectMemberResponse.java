package com.team5.web_ide.domain.member.dto;

import com.team5.web_ide.domain.member.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectMemberResponse {

    private Long memberId;
    private Long projectId;
    private Long userId;
    private String email;
    private String nickname;
    private String profileColor;
    private ProjectMember.ProjectRole role;

    public static ProjectMemberResponse from(ProjectMember member) {
        return new ProjectMemberResponse(
                member.getId(),
                member.getProject().getId(),
                member.getUser().getId(),
                member.getUser().getEmail(),
                member.getUser().getNickname(),
                member.getUser().getProfileColor(),
                member.getRole()
        );
    }
}
