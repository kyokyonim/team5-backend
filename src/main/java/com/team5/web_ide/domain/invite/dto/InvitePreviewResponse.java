package com.team5.web_ide.domain.invite.dto;

import com.team5.web_ide.domain.invite.entity.ProjectInvite;
import com.team5.web_ide.domain.member.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InvitePreviewResponse {

    private Long projectId;
    private String projectName;
    private String inviteeEmail;
    private ProjectMember.ProjectRole role;
    private ProjectInvite.InviteStatus status;
    private LocalDateTime expiresAt;
    private boolean expired;

    public static InvitePreviewResponse from(ProjectInvite invite) {
        return new InvitePreviewResponse(
                invite.getProject().getId(),
                invite.getProject().getProjectName(),
                invite.getInviteeEmail(),
                invite.getRole(),
                invite.getStatus(),
                invite.getExpiresAt(),
                invite.isExpired()
        );
    }
}
