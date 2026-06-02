package com.team5.web_ide.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "presence")
public class PresenceProperties {

    /** 프론트엔드 heartbeat 권장 주기 (ms) */
    private long heartbeatIntervalMs = 30_000L;

    /** 이 시간(초) 이내 lastSeenAt이면 "활성 접속"으로 간주 */
    private long activeThresholdSeconds = 90L;
}
