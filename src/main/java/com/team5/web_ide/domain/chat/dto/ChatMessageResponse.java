package com.team5.web_ide.domain.chat.dto;

import com.team5.web_ide.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderNickname;
    private String senderProfileColor;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderNickname(message.getSenderNickname())
                .senderProfileColor(message.getSenderProfileColor())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
