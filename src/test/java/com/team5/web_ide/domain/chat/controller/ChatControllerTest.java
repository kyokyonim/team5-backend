package com.team5.web_ide.domain.chat.controller;

import com.team5.web_ide.domain.chat.dto.ChatMessageListResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageResponse;
import com.team5.web_ide.domain.chat.exception.ChatErrorCode;
import com.team5.web_ide.domain.chat.exception.ChatException;
import com.team5.web_ide.domain.chat.service.ChatService;
import com.team5.web_ide.global.exception.GlobalExceptionHandler;
import com.team5.web_ide.global.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@Import({
        GlobalExceptionHandler.class,
        ChatControllerTest.TestSecurityConfig.class
})
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("채팅 조회 API는 성공 응답 구조를 반환한다")
    void getMessages_success() throws Exception {
        ChatMessageResponse message = ChatMessageResponse.builder()
                .id(1L)
                .senderId(1L)
                .senderNickname("kimda")
                .senderProfileColor("#FF5733")
                .content("hello")
                .createdAt(LocalDateTime.now())
                .build();
        ChatMessageListResponse payload = ChatMessageListResponse.builder()
                .messages(List.of(message))
                .hasMore(false)
                .nextCursor(null)
                .build();
        when(chatService.getMessages(eq(1L), eq(1L), eq(50), eq(null))).thenReturn(payload);

        mockMvc.perform(get("/api/projects/1/chats")
                        .param("size", "50")
                        .with(authentication(new UsernamePasswordAuthenticationToken(1L, null, List.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.messages[0].id").value(1))
                .andExpect(jsonPath("$.data.hasMore").value(false));
    }

    @Test
    @DisplayName("채팅 조회 API는 커서 오류를 400으로 반환한다")
    void getMessages_invalidCursor_returns400() throws Exception {
        when(chatService.getMessages(eq(1L), eq(1L), eq(50), eq(0L)))
                .thenThrow(new ChatException(ChatErrorCode.CHAT_INVALID_CURSOR));

        mockMvc.perform(get("/api/projects/1/chats")
                        .param("size", "50")
                        .param("before", "0")
                        .with(authentication(new UsernamePasswordAuthenticationToken(1L, null, List.of()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("CHAT_INVALID_CURSOR"));
    }

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

            return http.build();
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(null) {
                @Override
                protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain
                ) throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }
}
