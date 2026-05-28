package com.team5.web_ide.domain.chat.controller;

import com.team5.web_ide.domain.chat.entity.ChatMessage;
import com.team5.web_ide.domain.chat.repository.ChatMessageRepository;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:chat_ws_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
        }
)
class ChatWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private User sender;

    @BeforeEach
    void setUp() {
        chatMessageRepository.deleteAll();
        userRepository.deleteAll();

        sender = userRepository.save(User.builder()
                .email("chat-ws@test.com")
                .passwordHash("dummy")
                .nickname("kimda")
                .provider(User.Provider.LOCAL)
                .profileColor("#FF5733")
                .agreeService(true)
                .agreeFinance(true)
                .build());
    }

    @Test
    @DisplayName("WebSocket chat send stores message and broadcasts response")
    void sendMessage_storesAndBroadcasts() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        String token = jwtUtil.generateAccessToken(sender.getId(), sender.getEmail());
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        StompSession session = stompClient.connectAsync(
                "ws://localhost:" + port + "/ws",
                new WebSocketHttpHeaders(),
                connectHeaders,
                new StompSessionHandlerAdapter() {
                    @Override
                    public void handleTransportError(StompSession session, Throwable exception) {
                        throw new IllegalStateException("WebSocket transport error", exception);
                    }
                }
        ).get(3, TimeUnit.SECONDS);

        CompletableFuture<Map<String, Object>> chatReceived = new CompletableFuture<>();
        CompletableFuture<Object> errorReceived = new CompletableFuture<>();

        session.subscribe("/topic/projects/1/chat", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void handleFrame(StompHeaders headers, Object payload) {
                chatReceived.complete((Map<String, Object>) payload);
            }
        });

        session.subscribe("/user/queue/errors", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Object.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorReceived.complete(payload);
            }
        });

        TimeUnit.MILLISECONDS.sleep(300);

        session.send("/app/projects/1/chat", Map.of("content", "hello websocket"));

        Object firstFrame = CompletableFuture.anyOf(chatReceived, errorReceived).get(5, TimeUnit.SECONDS);
        assertThat(firstFrame)
                .as("Expected chat broadcast, but received websocket error frame: %s", firstFrame)
                .isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) firstFrame;
        assertThat(response.get("senderId")).isEqualTo(sender.getId().intValue());
        assertThat(response.get("senderNickname")).isEqualTo("kimda");
        assertThat(response.get("senderProfileColor")).isEqualTo("#FF5733");
        assertThat(response.get("content")).isEqualTo("hello websocket");

        List<ChatMessage> savedMessages = chatMessageRepository.findByProjectIdOrderByIdDesc(
                1L,
                org.springframework.data.domain.PageRequest.of(0, 10)
        );
        assertThat(savedMessages).hasSize(1);
        assertThat(savedMessages.get(0).getSenderId()).isEqualTo(sender.getId());
        assertThat(savedMessages.get(0).getContent()).isEqualTo("hello websocket");

        session.disconnect();
    }
}
