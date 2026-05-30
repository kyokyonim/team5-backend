package com.team5.web_ide.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ChatMessageListResponse {
    private List<ChatMessageResponse> messages;
    private boolean hasMore;
    private Long nextCursor;
}
