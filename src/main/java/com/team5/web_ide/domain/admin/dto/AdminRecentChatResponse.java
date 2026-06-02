package com.team5.web_ide.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminRecentChatResponse {
    private Long messageId;
    private Long projectId;
    private String projectName;
    private Long senderId;
    private String senderNickname;
    private String senderProfileColor;
    private LocalDateTime createdAt;
}
