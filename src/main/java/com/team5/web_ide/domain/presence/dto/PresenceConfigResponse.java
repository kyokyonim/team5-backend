package com.team5.web_ide.domain.presence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresenceConfigResponse {

    private long heartbeatIntervalMs;
    private long activeThresholdSeconds;
}
