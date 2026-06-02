package com.team5.web_ide.domain.file.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileLockEventResponse {

    private String type; // LOCKED, UNLOCKED
    private Long projectId;
    private Long fileId;
    private LockUserResponse lockedBy;
    private LocalDateTime lockedAt;

    public static FileLockEventResponse locked(FileLockInfo lockInfo) {
        return FileLockEventResponse.builder()
                .type("LOCKED")
                .projectId(lockInfo.getProjectId())
                .fileId(lockInfo.getFileId())
                .lockedBy(LockUserResponse.from(lockInfo))
                .lockedAt(lockInfo.getLockedAt())
                .build();
    }

    public static FileLockEventResponse unlocked(Long projectId, Long fileId) {
        return FileLockEventResponse.builder()
                .type("UNLOCKED")
                .projectId(projectId)
                .fileId(fileId)
                .lockedBy(null)
                .lockedAt(null)
                .build();
    }
}