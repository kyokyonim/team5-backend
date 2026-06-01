package com.team5.web_ide.domain.activitylog.service;

import com.team5.web_ide.domain.activitylog.entity.ActivityLog;
import com.team5.web_ide.domain.activitylog.enums.ActivityAction;
import com.team5.web_ide.domain.activitylog.enums.ActivityTargetType;
import com.team5.web_ide.domain.activitylog.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public ActivityLog record(
            Long actorId,
            ActivityAction action,
            ActivityTargetType targetType,
            Long targetId,
            Long projectId,
            String message,
            String ipAddress,
            String userAgent
    ) {
        return activityLogRepository.save(ActivityLog.builder()
                .actorId(actorId)
                .action(action)
                .targetType(targetType)
                .targetId(targetId)
                .projectId(projectId)
                .message(message)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build());
    }
}
