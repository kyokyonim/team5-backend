package com.team5.web_ide.domain.invite.dto;

import com.team5.web_ide.domain.member.dto.ProjectMemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteAcceptResponse {

    private Long projectId;
    private String projectName;
    private ProjectMemberResponse member;
}
