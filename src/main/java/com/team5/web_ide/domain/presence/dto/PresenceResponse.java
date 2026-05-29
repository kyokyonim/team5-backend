package com.team5.web_ide.domain.presence.dto;

import com.team5.web_ide.domain.presence.entity.Presence;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PresenceResponse {

    private Long presenceId;
    private Long projectId;
    private Long userId;
    private String nickname;
    private String profileColor;
    private LocalDateTime lastSeenAt;

    public static PresenceResponse from(Presence presence) {
        return new PresenceResponse(
                presence.getId(),
                presence.getProject().getId(),
                presence.getUser().getId(),
                presence.getUser().getNickname(),
                presence.getUser().getProfileColor(),
                presence.getLastSeenAt()
        );
    }
}
