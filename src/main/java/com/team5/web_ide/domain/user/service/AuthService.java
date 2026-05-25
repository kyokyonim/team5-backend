package com.team5.web_ide.domain.user.service;

import com.team5.web_ide.domain.user.dto.LoginResponseDto;
import com.team5.web_ide.domain.user.dto.SignupRequestDto;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 회원가입
    @Transactional
    public User signup(SignupRequestDto dto) {

        // 이메일 중복 검사
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        // 닉네임 자동 생성 (이메일 앞부분 영어만 최대 6글자)
        String nickname = dto.getEmail()
                .split("@")[0]
                .replaceAll("[^a-zA-Z]", "")
                .toLowerCase();
        if (nickname.length() > 6) {
            nickname = nickname.substring(0, 6);
        }
        if (nickname.isEmpty()) {
            nickname = "user";
        }

        // 비밀번호 암호화
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        // User 저장
        User user = User.builder()
                .email(dto.getEmail())
                .passwordHash(passwordHash)
                .nickname(nickname)
                .provider(User.Provider.LOCAL)
                .agreeService(dto.isAgreeService())
                .agreeFinance(dto.isAgreeFinance())
                .agreePrivacy(dto.isAgreePrivacy())
                .build();

        return userRepository.save(user);
    }

    // 이메일 중복검사
    public boolean checkEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 로그인
    @Transactional
    public LoginResponseDto login(String email, String password) {

        // 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다"));

        // 상태 확인
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new IllegalArgumentException("사용할 수 없는 계정입니다");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다");
        }

        // 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileColor(user.getProfileColor())
                .build();
    }
}