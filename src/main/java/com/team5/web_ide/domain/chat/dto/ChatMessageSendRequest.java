package com.team5.web_ide.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageSendRequest {
    @NotBlank(message = "채팅 내용을 입력해주세요.")
    @Size(max = 2000, message = "채팅은 2,000자 이하로만 입력할 수 있습니다.")
    private String content;
}
