package com.team5.web_ide.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileLockInfo {

    private Long projectId;
    private Long fileId;
    private Long lockedBy;
    private String lockedByNickname;
    private LocalDateTime lockedAt;
}