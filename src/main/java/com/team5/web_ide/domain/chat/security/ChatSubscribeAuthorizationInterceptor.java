package com.team5.web_ide.domain.chat.security;

import com.team5.web_ide.domain.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ChatSubscribeAuthorizationInterceptor implements ChannelInterceptor {

    private static final Pattern CHAT_TOPIC_PATTERN = Pattern.compile("^/topic/projects/(\\d+)/chat$");

    private final ProjectService projectService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            return message;
        }

        Long projectId = extractChatProjectId(accessor.getDestination());
        if (projectId == null) {
            return message;
        }

        Long userId = extractUserId(accessor);
        projectService.findActiveProject(projectId);
        projectService.validateProjectMember(projectId, userId);
        return message;
    }

    private Long extractChatProjectId(String destination) {
        if (destination == null) {
            return null;
        }

        Matcher matcher = CHAT_TOPIC_PATTERN.matcher(destination);
        if (!matcher.matches()) {
            return null;
        }
        return Long.valueOf(matcher.group(1));
    }

    private Long extractUserId(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof Authentication authentication)) {
            throw new MessageDeliveryException("Authentication is required.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        if (principal instanceof Integer userId) {
            return userId.longValue();
        }
        throw new MessageDeliveryException("Authentication is required.");
    }
}
