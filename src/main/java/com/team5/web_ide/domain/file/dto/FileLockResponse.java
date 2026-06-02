package com.team5.web_ide.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileLockResponse {

    private Long fileId;
    private Boolean locked;
    private LockStatus lockStatus;
    private Boolean lockedByMe;
    private LockUserResponse lockedBy;
    private LocalDateTime lockedAt;

    public static FileLockResponse from(FileLockInfo lockInfo, Long currentUserId) {
        boolean lockedByMe = lockInfo.getLockedBy().equals(currentUserId);

        return FileLockResponse.builder()
                .fileId(lockInfo.getFileId())
                .locked(true)
                .lockStatus(lockedByMe ? LockStatus.LOCKED_BY_ME : LockStatus.LOCKED_BY_OTHER)
                .lockedByMe(lockedByMe)
                .lockedBy(LockUserResponse.from(lockInfo))
                .lockedAt(lockInfo.getLockedAt())
                .build();
    }

    public static FileLockResponse lockedByMe(FileLockInfo lockInfo) {
        return FileLockResponse.builder()
                .fileId(lockInfo.getFileId())
                .locked(true)
                .lockStatus(LockStatus.LOCKED_BY_ME)
                .lockedByMe(true)
                .lockedBy(LockUserResponse.from(lockInfo))
                .lockedAt(lockInfo.getLockedAt())
                .build();
    }
}