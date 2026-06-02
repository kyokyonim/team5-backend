package com.team5.web_ide.domain.admin.service;

import com.team5.web_ide.domain.admin.repository.AdminChatQueryRepository;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.exception.ApiException;
import com.team5.web_ide.global.exception.GlobalErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminChatServiceTest {

    @Mock
    private AdminChatQueryRepository adminChatQueryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminChatService adminChatService;

    @Test
    void getRecentChats_usesDefaultSize() {
        givenAdmin();
        when(adminChatQueryRepository.findRecentChats(any())).thenReturn(List.of());

        adminChatService.getRecentChats(1L, null);

        Pageable pageable = capturePageable();
        assertThat(pageable.getPageSize()).isEqualTo(20);
    }

    @Test
    void getRecentChats_clampsSizeToRange() {
        givenAdmin();
        when(adminChatQueryRepository.findRecentChats(any())).thenReturn(List.of());

        adminChatService.getRecentChats(1L, 0);
        Pageable minPageable = capturePageable();
        assertThat(minPageable.getPageSize()).isEqualTo(1);

        adminChatService.getRecentChats(1L, 150);
        Pageable maxPageable = capturePageable();
        assertThat(maxPageable.getPageSize()).isEqualTo(100);
    }

    @Test
    void getRecentChats_nonAdmin_throwsUnauthorized() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(User.Role.USER, User.Status.ACTIVE)));

        assertThatThrownBy(() -> adminChatService.getRecentChats(1L, 20))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getErrorCode())
                .isEqualTo(GlobalErrorCode.AUTH_UNAUTHORIZED);
    }

    private void givenAdmin() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(User.Role.ADMIN, User.Status.ACTIVE)));
    }

    private User user(User.Role role, User.Status status) {
        return User.builder()
                .id(1L)
                .email("admin@test.com")
                .nickname("admin")
                .provider(User.Provider.LOCAL)
                .role(role)
                .status(status)
                .agreeService(true)
                .agreeFinance(true)
                .build();
    }

    private Pageable capturePageable() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        org.mockito.Mockito.verify(adminChatQueryRepository, org.mockito.Mockito.atLeastOnce())
                .findRecentChats(captor.capture());
        return captor.getValue();
    }
}
