package com.team5.web_ide.controller;
import com.team5.web_ide.domain.user.entity.User;

import com.team5.web_ide.domain.user.dto.LoginRequestDto;
import com.team5.web_ide.domain.user.dto.LoginResponseDto;
import com.team5.web_ide.domain.user.dto.SignupRequestDto;
import com.team5.web_ide.domain.user.service.AuthService;
import com.team5.web_ide.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
