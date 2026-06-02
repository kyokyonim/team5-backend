package com.team5.web_ide.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminUserListResponse {

    private List<AdminUserResponse> users;
    private long totalCount;
    private int page;
    private int size;
    private int totalPages;
}
