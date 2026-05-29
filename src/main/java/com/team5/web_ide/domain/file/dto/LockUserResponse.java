package com.team5.web_ide.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LockUserResponse {

    private Long userId;
    private String nickname;

    public static LockUserResponse from(FileLockInfo lockInfo) {
        return LockUserResponse.builder()
                .userId(lockInfo.getLockedBy())
                .nickname(lockInfo.getLockedByNickname())
                .build();
    }
}