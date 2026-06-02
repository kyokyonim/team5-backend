package com.team5.web_ide.domain.admin.dto;

import com.team5.web_ide.domain.presence.entity.Presence;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminPresenceResponse {

    private Long presenceId;
    private Long projectId;
    private String projectName;
    private Long userId;
    private String nickname;
    private String email;
    private String profileColor;
    private LocalDateTime lastSeenAt;

    public static AdminPresenceResponse from(Presence presence) {
        return new AdminPresenceResponse(
                presence.getId(),
                presence.getProject().getId(),
                presence.getProject().getProjectName(),
                presence.getUser().getId(),
                presence.getUser().getNickname(),
                presence.getUser().getEmail(),
                presence.getUser().getProfileColor(),
                presence.getLastSeenAt()
        );
    }
}
