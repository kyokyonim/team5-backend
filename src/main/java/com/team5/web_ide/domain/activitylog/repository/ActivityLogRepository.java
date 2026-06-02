package com.team5.web_ide.domain.activitylog.repository;

import com.team5.web_ide.domain.activitylog.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
