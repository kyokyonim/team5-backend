package com.team5.web_ide.domain.file.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.web_ide.domain.file.dto.FileLockEventResponse;
import com.team5.web_ide.domain.file.dto.FileLockInfo;
import com.team5.web_ide.domain.file.exception.FileErrorCode;
import com.team5.web_ide.domain.file.exception.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileLockService {
    private static final Duration LOCK_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public FileLockInfo lock(Long projectId, Long fileId, Long userId, String nickname) {
        String lockKey = lockKey(projectId, fileId);

        FileLockInfo newLockInfo = new FileLockInfo(
                projectId,
                fileId,
                userId,
                nickname,
                LocalDateTime.now()
        );

        String value = writeValueAsString(newLockInfo);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, value, LOCK_TTL);

        if (Boolean.TRUE.equals(success)) {
            addUserLock(projectId, userId, fileId);
            publishLocked(projectId, newLockInfo);
            return newLockInfo;
        }

        FileLockInfo currentLockInfo = getLockInfo(projectId, fileId);

        if (currentLockInfo == null) {
            Boolean retrySuccess = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, value, LOCK_TTL);

            if (Boolean.TRUE.equals(retrySuccess)) {
                addUserLock(projectId, userId, fileId);
                publishLocked(projectId, newLockInfo);
                return newLockInfo;
            }

            currentLockInfo = getLockInfo(projectId, fileId);
        }

        if (currentLockInfo != null && currentLockInfo.getLockedBy().equals(userId)) {
            redisTemplate.expire(lockKey, LOCK_TTL);
            redisTemplate.expire(userLockKey(projectId, userId), LOCK_TTL);

            // 같은 사용자가 이미 잡은 lock을 갱신하는 경우이므로 새 LOCKED 이벤트는 보내지 않음
            return currentLockInfo;
        }

        throw new FileException(FileErrorCode.FILE_LOCKED);
    }

    public FileLockInfo getLockInfo(Long projectId, Long fileId) {
        String value = redisTemplate.opsForValue().get(lockKey(projectId, fileId));

        if (value == null) {
            return null;
        }

        return readValue(value);
    }

    public boolean isLocked(Long projectId, Long fileId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey(projectId, fileId)));
    }

    public boolean isLockedByMe(Long projectId, Long fileId, Long userId) {
        FileLockInfo lockInfo = getLockInfo(projectId, fileId);

        return lockInfo != null && lockInfo.getLockedBy().equals(userId);
    }

    public void validateLockOwner(Long projectId, Long fileId, Long userId) {
        FileLockInfo lockInfo = getLockInfo(projectId, fileId);

        if (lockInfo == null) {
            throw new FileException(FileErrorCode.FILE_LOCK_REQUIRED);
        }

        if (!lockInfo.getLockedBy().equals(userId)) {
            throw new FileException(FileErrorCode.FILE_LOCK_OWNER_MISMATCH);
        }
    }

    public void validateNotLockedByOther(Long projectId, Long fileId, Long userId) {
        FileLockInfo lockInfo = getLockInfo(projectId, fileId);

        if (lockInfo == null) {
            return;
        }

        if (!lockInfo.getLockedBy().equals(userId)) {
            throw new FileException(FileErrorCode.FILE_LOCKED);
        }
    }

    public void unlock(Long projectId, Long fileId) {
        FileLockInfo lockInfo = getLockInfo(projectId, fileId);

        redisTemplate.delete(lockKey(projectId, fileId));

        if (lockInfo != null) {
            redisTemplate.opsForSet().remove(
                    userLockKey(projectId, lockInfo.getLockedBy()),
                    String.valueOf(fileId)
            );

            publishUnlocked(projectId, fileId);
        }
    }

    public void unlockByUser(Long projectId, Long userId) {
        // Presence 도메인에서 사용자가 프로젝트에서 나가거나 disconnect될 때 호출
        String userLockKey = userLockKey(projectId, userId);
        Set<String> fileIds = redisTemplate.opsForSet().members(userLockKey);

        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        for (String fileIdValue : fileIds) {
            Long fileId = Long.valueOf(fileIdValue);

            redisTemplate.delete(lockKey(projectId, fileId));
            publishUnlocked(projectId, fileId);
        }

        redisTemplate.delete(userLockKey);
    }

    public boolean hasLockedFile(Long projectId, List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            return false;
        }

        return fileIds.stream()
                .anyMatch(fileId -> isLocked(projectId, fileId));
    }

    private void addUserLock(Long projectId, Long userId, Long fileId) {
        String userLockKey = userLockKey(projectId, userId);

        redisTemplate.opsForSet().add(userLockKey, String.valueOf(fileId));
        redisTemplate.expire(userLockKey, LOCK_TTL);
    }

    private void publishLocked(Long projectId, FileLockInfo lockInfo) {
        publishLockEvent(projectId, FileLockEventResponse.locked(lockInfo));
    }

    private void publishUnlocked(Long projectId, Long fileId) {
        publishLockEvent(projectId, FileLockEventResponse.unlocked(projectId, fileId));
    }

    private void publishLockEvent(Long projectId, FileLockEventResponse event) {
        messagingTemplate.convertAndSend(
                "/topic/projects/" + projectId + "/files/locks",
                event
        );
    }

    private String lockKey(Long projectId, Long fileId) {
        return "file-lock:" + projectId + ":" + fileId;
    }

    private String userLockKey(Long projectId, Long userId) {
        return "user-file-locks:" + projectId + ":" + userId;
    }

    private String writeValueAsString(FileLockInfo lockInfo) {
        try {
            return objectMapper.writeValueAsString(lockInfo);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("파일 잠금 정보를 Redis에 저장할 수 없습니다.", e);
        }
    }

    private FileLockInfo readValue(String value) {
        try {
            return objectMapper.readValue(value, FileLockInfo.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("파일 잠금 정보를 Redis에서 읽을 수 없습니다.", e);
        }
    }
}