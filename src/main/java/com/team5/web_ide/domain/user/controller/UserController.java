package com.team5.web_ide.domain.user.controller;

import com.team5.web_ide.domain.user.dto.UpdateProfileRequestDto;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.domain.user.service.UserService;
import com.team5.web_ide.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyInfo(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));

        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공",
                Map.of(
                        "userId", user.getId(),
                        "email", user.getEmail(),
                        "nickname", user.getNickname(),
                        "profileColor", user.getProfileColor(),
                        "role", user.getRole(),
                        "provider", user.getProvider()
                )));
    }

    // 닉네임 중복확인
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<?>> checkNickname(@RequestParam String nickname) {
        boolean available = !userRepository.existsByNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success("닉네임 중복확인 완료",
                Map.of("available", available)));
    }

    // 프로필 수정
    @PatchMapping("/me/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(
            @RequestParam Long userId,
            @RequestBody UpdateProfileRequestDto dto) {
        User user = userService.updateProfile(userId, dto);
        return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공",
                Map.of(
                        "nickname", user.getNickname(),
                        "profileColor", user.getProfileColor()
                )));
    }
}