package com.team5.web_ide.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileLockResponse {

    private Long fileId;
    private Boolean locked;
    private Boolean lockedByMe;
    private LockUserResponse lockedBy;
    private LocalDateTime lockedAt;

    public static FileLockResponse from(FileLockInfo lockInfo, Long currentUserId) {
        boolean lockedByMe = lockInfo.getLockedBy().equals(currentUserId);

        return FileLockResponse.builder()
                .fileId(lockInfo.getFileId())
                .locked(true)
                .lockedByMe(lockedByMe)
                .lockedBy(LockUserResponse.from(lockInfo))
                .lockedAt(lockInfo.getLockedAt())
                .build();
    }
}