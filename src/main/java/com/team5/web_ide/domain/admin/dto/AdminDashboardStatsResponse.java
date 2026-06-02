package com.team5.web_ide.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminDashboardStatsResponse {

    private long totalUsers;
    private long activeUsers;
    private long bannedUsers;
    private long activeConnections;
    private long totalProjects;
}
