package com.team5.web_ide.domain.chat.controller;

import com.team5.web_ide.domain.chat.dto.ChatMessageListResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageSendRequest;
import com.team5.web_ide.domain.chat.service.ChatService;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/chats")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

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
    public void sendMessage(
            @DestinationVariable Long projectId,
            @Payload ChatMessageSendRequest request,
            Principal principal
    ) {
        Long userId = extractUserId(principal);
        ChatMessageResponse response = chatService.sendMessage(projectId, userId, request);
        messagingTemplate.convertAndSend("/topic/projects/" + projectId + "/chat", response);
    }

    private Long extractUserId(Principal principal) {
        if (!(principal instanceof Authentication authentication)) {
            throw new IllegalArgumentException("Unauthorized websocket session.");
        }

        Object userId = authentication.getPrincipal();
        if (userId instanceof Long longUserId) {
            return longUserId;
        }
        if (userId instanceof Integer intUserId) {
            return intUserId.longValue();
        }
        throw new IllegalArgumentException("Unauthorized websocket session.");
    }

    @MessageExceptionHandler(ApiException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleWsApiException(ApiException ex) {
        return ApiResponse.fail(ex.getErrorCode().code(), ex.getErrorCode().message());
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleWsIllegalArgumentException(IllegalArgumentException ex) {
        return ApiResponse.fail(GlobalErrorCode.BAD_REQUEST.code(), ex.getMessage());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleWsException(Exception ex) {
        return ApiResponse.fail(
                GlobalErrorCode.INTERNAL_SERVER_ERROR.code(),
                GlobalErrorCode.INTERNAL_SERVER_ERROR.message()
        );
    }
}
