package com.team5.web_ide.domain.admin.service;

import com.team5.web_ide.domain.admin.dto.AdminUserCategory;
import com.team5.web_ide.domain.admin.dto.AdminUserListResponse;
import com.team5.web_ide.domain.admin.dto.AdminUserResponse;
import com.team5.web_ide.domain.admin.dto.AdminUserStatusFilter;
import com.team5.web_ide.domain.admin.dto.AdminUserStatusResponse;
import com.team5.web_ide.domain.admin.exception.AdminUserErrorCode;
import com.team5.web_ide.domain.admin.exception.AdminUserException;
import com.team5.web_ide.domain.admin.repository.AdminUserQueryRepository;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final int NEW_USER_DAYS = 7;

    private final AdminUserQueryRepository adminUserQueryRepository;
    private final UserRepository userRepository;

    public AdminUserListResponse getUsers(
            Long adminId,
            AdminUserCategory category,
            AdminUserStatusFilter status,
            String keyword,
            Integer page,
            Integer size
    ) {
        validateAdmin(adminId);

        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        AdminUserCategory normalizedCategory = category == null ? AdminUserCategory.ALL : category;
        AdminUserStatusFilter normalizedStatus = status == null ? AdminUserStatusFilter.ALL : status;

        Page<User> users = adminUserQueryRepository.findUsers(
                normalizedCategory,
                normalizedStatus,
                keyword,
                LocalDateTime.now().minusDays(NEW_USER_DAYS),
                PageRequest.of(normalizedPage, normalizedSize)
        );

        return new AdminUserListResponse(
                users.getContent().stream()
                        .map(AdminUserResponse::from)
                        .toList(),
                users.getTotalElements(),
                users.getNumber(),
                users.getSize(),
                users.getTotalPages()
        );
    }

    @Transactional
    public AdminUserStatusResponse suspendUser(Long adminId, Long userId) {
        validateAdmin(adminId);
        if (adminId.equals(userId)) {
            throw new AdminUserException(AdminUserErrorCode.ADMIN_SELF_SUSPEND_DENIED);
        }

        User user = findUser(userId);
        if (user.getStatus() == User.Status.BANNED) {
            throw new AdminUserException(AdminUserErrorCode.USER_ALREADY_BANNED);
        }

        user.suspend();
        return AdminUserStatusResponse.suspended(user);
    }

    @Transactional
    public AdminUserStatusResponse activateUser(Long adminId, Long userId) {
        validateAdmin(adminId);

        User user = findUser(userId);
        if (user.getStatus() == User.Status.ACTIVE) {
            throw new AdminUserException(AdminUserErrorCode.USER_ALREADY_ACTIVE);
        }

        user.activate();
        return AdminUserStatusResponse.activated(user);
    }

    private int normalizePage(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return Math.max(page, 0);
    }

    private int normalizeSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        return Math.min(Math.max(size, 1), MAX_SIZE);
    }

    private void validateAdmin(Long adminId) {
        if (adminId == null) {
            throw new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED);
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED));
        if (admin.getStatus() != User.Status.ACTIVE || admin.getRole() != User.Role.ADMIN) {
            throw new ApiException(GlobalErrorCode.AUTH_UNAUTHORIZED);
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AdminUserException(AdminUserErrorCode.USER_NOT_FOUND));
    }
}
