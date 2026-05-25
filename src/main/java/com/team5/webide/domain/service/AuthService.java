package com.team5.webide.domain.user.service;

import com.team5.webide.domain.user.dto.SignupRequestDto;
import com.team5.webide.domain.user.entity.User;
import com.team5.webide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public boolean checkEmail(String email) {
        return !userRepository.existsByEmail(email);
    }
}