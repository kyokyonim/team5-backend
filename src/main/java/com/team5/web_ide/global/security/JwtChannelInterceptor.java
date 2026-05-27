package com.team5.web_ide.global.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            // TODO: Replace with JwtUtil validation after auth integration.
            // Example target flow:
            // if (authHeader == null || !authHeader.startsWith("Bearer ")) { ... }
            // Long userId = jwtUtil.getUserId(token);
            Long userId = 1L;

            if (accessor.getSessionAttributes() != null) {
                accessor.getSessionAttributes().put("userId", userId);
            }
            if (accessor.getSessionAttributes() != null) {
                accessor.getSessionAttributes().put("authHeader", authHeader);
            }
        }

        return message;
    }
}
