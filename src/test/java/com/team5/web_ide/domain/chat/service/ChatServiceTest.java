package com.team5.web_ide.domain.chat.service;

import com.team5.web_ide.domain.chat.dto.ChatMessageListResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageSendRequest;
import com.team5.web_ide.domain.chat.entity.ChatMessage;
import com.team5.web_ide.domain.chat.exception.ChatErrorCode;
import com.team5.web_ide.domain.chat.exception.ChatException;
import com.team5.web_ide.domain.chat.repository.ChatMessageRepository;
import com.team5.web_ide.domain.member.entity.ProjectMember;
import com.team5.web_ide.domain.project.exception.ProjectErrorCode;
import com.team5.web_ide.domain.project.exception.ProjectException;
import com.team5.web_ide.domain.project.service.ProjectService;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ChatService chatService;

    @Test
    @DisplayName("before가 0 이하이면 CHAT_INVALID_CURSOR 예외를 던진다")
    void getMessages_invalidBefore_throwsException() {
        assertThatThrownBy(() -> chatService.getMessages(1L, 1L, 50, 0L))
                .isInstanceOf(ChatException.class)
                .extracting(ex -> ((ChatException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.CHAT_INVALID_CURSOR);
    }

    @Test
    @DisplayName("size+1 조회 결과로 hasMore와 nextCursor를 계산한다")
    void getMessages_cursorPagination_works() {
        List<ChatMessage> rows = List.of(
                message(105L, "m1"),
                message(104L, "m2"),
                message(103L, "m3")
        );
        when(chatMessageRepository.findByProjectIdOrderByIdDesc(eq(1L), any()))
                .thenReturn(rows);

        ChatMessageListResponse result = chatService.getMessages(1L, 1L, 2, null);

        assertThat(result.isHasMore()).isTrue();
        assertThat(result.getNextCursor()).isEqualTo(104L);
        assertThat(result.getMessages()).hasSize(2);
    }

    @Test
    @DisplayName("공백 메시지는 CHAT_CONTENT_EMPTY 예외를 던진다")
    void sendMessage_blankContent_throwsException() {
        givenCanSend(ProjectMember.ProjectRole.EDITOR);
        ChatMessageSendRequest request = sendRequest("   ");

        assertThatThrownBy(() -> chatService.sendMessage(1L, 1L, request))
                .isInstanceOf(ChatException.class)
                .extracting(ex -> ((ChatException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.CHAT_CONTENT_EMPTY);
    }

    @Test
    @DisplayName("null 메시지는 CHAT_CONTENT_EMPTY 예외를 던진다")
    void sendMessage_nullContent_throwsException() {
        givenCanSend(ProjectMember.ProjectRole.EDITOR);
        ChatMessageSendRequest request = sendRequest(null);

        assertThatThrownBy(() -> chatService.sendMessage(1L, 1L, request))
                .isInstanceOf(ChatException.class)
                .extracting(ex -> ((ChatException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.CHAT_CONTENT_EMPTY);
    }

    @Test
    @DisplayName("2000자를 초과하면 CHAT_CONTENT_TOO_LONG 예외를 던진다")
    void sendMessage_tooLong_throwsException() {
        givenCanSend(ProjectMember.ProjectRole.EDITOR);
        ChatMessageSendRequest request = sendRequest("a".repeat(2001));

        assertThatThrownBy(() -> chatService.sendMessage(1L, 1L, request))
                .isInstanceOf(ChatException.class)
                .extracting(ex -> ((ChatException) ex).getErrorCode())
                .isEqualTo(ChatErrorCode.CHAT_CONTENT_TOO_LONG);
    }

    @Test
    @DisplayName("메시지는 앞뒤 공백을 제거해 저장하고 발신자 스냅샷을 채운다")
    void sendMessage_persistsTrimmedContentAndSnapshot() {
        givenCanSend(ProjectMember.ProjectRole.EDITOR);
        ChatMessageSendRequest request = sendRequest("  raw content  ");
        User sender = User.builder()
                .id(1L)
                .email("chat1@test.com")
                .nickname("kimda")
                .provider(User.Provider.LOCAL)
                .profileColor("#FF5733")
                .agreeService(true)
                .agreeFinance(true)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> {
                    ChatMessage arg = invocation.getArgument(0);
                    return ChatMessage.builder()
                            .id(200L)
                            .projectId(arg.getProjectId())
                            .senderId(arg.getSenderId())
                            .content(arg.getContent())
                            .createdAt(LocalDateTime.now())
                            .build();
                });

        chatService.sendMessage(1L, 1L, request);

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        ChatMessage saved = captor.getValue();
        assertThat(saved.getContent()).isEqualTo("raw content");
        assertThat(response.getSenderNickname()).isEqualTo("kimda");
        assertThat(response.getSenderProfileColor()).isEqualTo("#FF5733");
    }

    @Test
    @DisplayName("trim 후 2000자 이하면 저장할 수 있다")
    void sendMessage_contentWithOuterSpacesAllowedWhenTrimmedLengthIsValid() {
        givenCanSend(ProjectMember.ProjectRole.EDITOR);
        ChatMessageSendRequest request = sendRequest(" " + "a".repeat(2000) + " ");
        User sender = User.builder()
                .id(1L)
                .email("chat1@test.com")
                .nickname("kimda")
                .provider(User.Provider.LOCAL)
                .profileColor("#FF5733")
                .agreeService(true)
                .agreeFinance(true)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(chatMessageRepository.save(any(ChatMessage.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        chatService.sendMessage(1L, 1L, request);

        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        assertThat(captor.getValue().getContent()).hasSize(2000);
    }

    @Test
    @DisplayName("VIEWER는 채팅 메시지를 전송할 수 없다")
    void sendMessage_viewerRole_throwsAccessDenied() {
        givenCanSend(ProjectMember.ProjectRole.VIEWER);
        ChatMessageSendRequest request = sendRequest("hello");

        assertThatThrownBy(() -> chatService.sendMessage(1L, 1L, request))
                .isInstanceOf(ProjectException.class)
                .extracting(ex -> ((ProjectException) ex).getErrorCode())
                .isEqualTo(ProjectErrorCode.PROJECT_ACCESS_DENIED);
    }

    private void givenCanSend(ProjectMember.ProjectRole role) {
        when(projectService.validateProjectMember(1L, 1L))
                .thenReturn(ProjectMember.builder()
                        .role(role)
                        .build());
    }

    private ChatMessageSendRequest sendRequest(String content) {
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }

    private ChatMessage message(Long id, String content) {
        return ChatMessage.builder()
                .id(id)
                .projectId(1L)
                .senderId(1L)
                .sender(user("kimda", "#FF5733"))
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private User user(String nickname, String profileColor) {
        return User.builder()
                .id(1L)
                .email("chat1@test.com")
                .nickname(nickname)
                .provider(User.Provider.LOCAL)
                .profileColor(profileColor)
                .agreeService(true)
                .agreeFinance(true)
                .build();
    }
}
