package com.team5.web_ide.domain.invite.dto;

import com.team5.web_ide.domain.invite.entity.ProjectInvite;
import com.team5.web_ide.domain.member.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InviteResponse {

    private Long inviteId;
    private Long projectId;
    private String projectName;
    private String inviteeEmail;
    private ProjectMember.ProjectRole role;
    private ProjectInvite.InviteStatus status;
    private String inviteUrl;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    public static InviteResponse from(ProjectInvite invite, String inviteUrl) {
        return new InviteResponse(
                invite.getId(),
                invite.getProject().getId(),
                invite.getProject().getProjectName(),
                invite.getInviteeEmail(),
                invite.getRole(),
                invite.getStatus(),
                inviteUrl,
                invite.getExpiresAt(),
                invite.getCreatedAt()
        );
    }
}
