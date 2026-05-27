package com.team5.web_ide.domain.chat.controller;

import com.team5.web_ide.domain.chat.dto.ChatMessageListResponse;
import com.team5.web_ide.domain.chat.service.ChatService;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
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
}
