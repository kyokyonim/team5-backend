package com.team5.web_ide.domain.chat.service;

import com.team5.web_ide.domain.chat.dto.ChatMessageListResponse;
import com.team5.web_ide.domain.chat.dto.ChatMessageResponse;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    @Transactional
    public ChatMessageResponse sendMessage(Long projectId, Long senderId, ChatMessageSendRequest request) {
        projectService.findActiveProject(projectId);
        ProjectMember member = projectService.validateProjectMember(projectId, senderId);
        if (member.getRole() == ProjectMember.ProjectRole.VIEWER) {
            throw new ProjectException(ProjectErrorCode.PROJECT_ACCESS_DENIED);
        }

        String content = normalizeContent(request.getContent());

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.USER_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .projectId(projectId)
                .senderId(senderId)
                .senderNickname(sender.getNickname())
                .senderProfileColor(sender.getProfileColor())
                .content(content)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        return ChatMessageResponse.from(saved, sender);
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponse getMessages(Long projectId, Long requesterId, Integer size, Long before) {
        projectService.findActiveProject(projectId);
        projectService.validateProjectMember(projectId, requesterId);

        int normalizedSize = normalizeSize(size);

        if (before != null && before <= 0L) {
            throw new ChatException(ChatErrorCode.CHAT_INVALID_CURSOR);
        }

        Pageable pageable = PageRequest.of(0, normalizedSize + 1);

        List<ChatMessage> messages = before == null
                ? chatMessageRepository.findByProjectIdOrderByIdDesc(projectId, pageable)
                : chatMessageRepository.findByProjectIdAndIdLessThanOrderByIdDesc(projectId, before, pageable);

        boolean hasMore = messages.size() > normalizedSize;
        List<ChatMessage> result = hasMore ? messages.subList(0, normalizedSize) : messages;

        Long nextCursor = hasMore && !result.isEmpty()
                ? result.get(result.size() - 1).getId()
                : null;

        List<ChatMessageResponse> responseMessages = result.stream()
                .map(ChatMessageResponse::from)
                .toList();

        return ChatMessageListResponse.builder()
                .messages(responseMessages)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return 50;
        }
        return Math.min(Math.max(size, 1), 100);
    }

    private String normalizeContent(String content) {
        String normalized = content == null ? null : content.trim();

        if (!StringUtils.hasText(normalized)) {
            throw new ChatException(ChatErrorCode.CHAT_CONTENT_EMPTY);
        }
        if (normalized.length() > 2000) {
            throw new ChatException(ChatErrorCode.CHAT_CONTENT_TOO_LONG);
        }
        return normalized;
    }
}
