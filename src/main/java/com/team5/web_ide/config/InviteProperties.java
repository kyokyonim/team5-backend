package com.team5.web_ide.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.invite")
public class InviteProperties {

    private String baseUrl = "http://localhost:5173";
    private long expirationHours = 72L;
    private String fromEmail = "noreply@efide.local";
}
