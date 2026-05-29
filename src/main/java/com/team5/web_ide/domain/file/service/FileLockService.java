package com.team5.web_ide.domain.file.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.web_ide.domain.file.dto.FileLockInfo;
import com.team5.web_ide.domain.file.exception.FileErrorCode;
import com.team5.web_ide.domain.file.exception.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FileLockService {

    // TODO: 추후 lock 유지 시간을 별도로 정하면 application.properties 설정값으로 분리
    private static final Duration LOCK_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

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
            return newLockInfo;
        }

        FileLockInfo currentLockInfo = getLockInfo(projectId, fileId);

        if (currentLockInfo == null) {
            Boolean retrySuccess = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, value, LOCK_TTL);

            if (Boolean.TRUE.equals(retrySuccess)) {
                addUserLock(projectId, userId, fileId);
                return newLockInfo;
            }

            currentLockInfo = getLockInfo(projectId, fileId);
        }

        if (currentLockInfo != null && currentLockInfo.getLockedBy().equals(userId)) {
            redisTemplate.expire(lockKey, LOCK_TTL);
            redisTemplate.expire(userLockKey(projectId, userId), LOCK_TTL);
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
        }
    }

    public void unlockByUser(Long projectId, Long userId) {
        // TODO: Presence 도메인에서 disconnect 이벤트가 구현되면 이 메서드를 호출하도록 연결
        // Presence 쪽에서 projectId와 userId를 받을 수 있어야 함

        String userLockKey = userLockKey(projectId, userId);
        Set<String> fileIds = redisTemplate.opsForSet().members(userLockKey);

        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }

        for (String fileId : fileIds) {
            redisTemplate.delete(lockKey(projectId, Long.valueOf(fileId)));
        }

        redisTemplate.delete(userLockKey);
    }

    public boolean hasLockedFile(Long projectId, List<Long> fileIds) {
        // TODO: 폴더 삭제/이름 변경 구현 시 FileService에서 하위 파일 id 목록을 조회한 뒤 이 메서드로 검사

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
            // TODO: 필요하면 Redis 직렬화 실패용 GlobalErrorCode 또는 FileErrorCode를 따로 정의
            throw new IllegalStateException("파일 잠금 정보를 Redis에 저장할 수 없습니다.", e);
        }
    }

    private FileLockInfo readValue(String value) {
        try {
            return objectMapper.readValue(value, FileLockInfo.class);
        } catch (JsonProcessingException e) {
            // TODO: 필요하면 Redis 역직렬화 실패용 GlobalErrorCode 또는 FileErrorCode를 따로 정의
            throw new IllegalStateException("파일 잠금 정보를 Redis에서 읽을 수 없습니다.", e);
        }
    }
}