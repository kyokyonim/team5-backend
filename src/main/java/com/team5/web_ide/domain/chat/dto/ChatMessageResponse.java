package com.team5.web_ide.domain.chat.dto;

import com.team5.web_ide.domain.chat.entity.ChatMessage;
import com.team5.web_ide.domain.user.entity.User;
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
        return from(message, message.getSender());
    }

    public static ChatMessageResponse from(ChatMessage message, User sender) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderNickname(sender.getNickname())
                .senderProfileColor(sender.getProfileColor())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
