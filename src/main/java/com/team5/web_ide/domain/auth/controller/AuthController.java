package com.team5.web_ide.domain.auth.controller;

import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.auth.dto.LoginRequestDto;
import com.team5.web_ide.domain.auth.dto.LoginResponseDto;
import com.team5.web_ide.domain.auth.dto.SignupRequestDto;
import com.team5.web_ide.domain.auth.service.AuthService;
import com.team5.web_ide.domain.auth.service.PasswordResetService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.team5.web_ide.domain.auth.dto.ForgotPasswordRequestDto;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignupRequestDto dto) {
        User user = authService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.",
                        Map.of(
                                "userId", user.getId(),
                                "email", user.getEmail(),
                                "nickname", user.getNickname()
                        )));
    }

    // 이메일 중복검사
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<?>> checkEmail(@RequestParam String email) {
        boolean available = authService.checkEmail(email);
        return ResponseEntity.ok(ApiResponse.success("이메일 중복검사 완료",
                Map.of("available", available)));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDto dto) {
        LoginResponseDto response = authService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(Authentication authentication) {
        if (authentication != null) {
            Long userId = (Long) authentication.getPrincipal();
            authService.logout(userId);
        }
        return ResponseEntity.ok(ApiResponse.success("로그아웃 되었습니다.", null));
    }

    // 비밀번호 찾기 (이메일 발송)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestBody ForgotPasswordRequestDto dto) {
        passwordResetService.sendResetEmail(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 이메일을 발송했습니다.", null));
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody Map<String, String> body) {
        passwordResetService.resetPassword(body.get("token"), body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 재설정되었습니다.", null));
    }
}