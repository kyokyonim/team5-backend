package com.team5.web_ide.domain.admin.service;

import com.team5.web_ide.domain.admin.dto.AdminRecentChatResponse;
import com.team5.web_ide.domain.admin.repository.AdminChatQueryRepository;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChatService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final AdminChatQueryRepository adminChatQueryRepository;
    private final UserRepository userRepository;

    public List<AdminRecentChatResponse> getRecentChats(Long adminId, Integer size) {
        validateAdmin(adminId);
        int normalizedSize = normalizeSize(size);
        return adminChatQueryRepository.findRecentChats(PageRequest.of(0, normalizedSize));
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
}
