package com.team5.web_ide.domain.chat.controller;

import com.team5.web_ide.domain.chat.dto.ChatMessageListResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageSendRequest;
import com.team5.web_ide.domain.chat.service.ChatService;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ApiResponse<ChatMessageListResponse> getMessages(
            @PathVariable Long projectId,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long before
    ) {
        ChatMessageListResponse response = chatService.getMessages(projectId, size, before);
        return ApiResponse.success("Chat messages retrieved successfully.", response);
    }

    @MessageMapping("/projects/{projectId}/chat")
    @SendTo("/topic/projects/{projectId}/chat")
    public ChatMessageResponse sendMessage(
            @DestinationVariable Long projectId,
            @Payload ChatMessageSendRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        Long userId = extractUserId(headerAccessor);
        return chatService.sendMessage(projectId, userId, request);
    }

    private Long extractUserId(SimpMessageHeaderAccessor headerAccessor) {
        Object userId = headerAccessor.getSessionAttributes() == null
                ? null
                : headerAccessor.getSessionAttributes().get("userId");

        if (userId instanceof Long longUserId) {
            return longUserId;
        }
        if (userId instanceof Integer intUserId) {
            return intUserId.longValue();
        }
        throw new IllegalArgumentException("Unauthorized websocket session.");
    }
}
