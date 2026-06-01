package com.team5.web_ide.domain.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.web_ide.domain.auth.dto.LoginResponseDto;
import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.response.ApiResponse;
import com.team5.web_ide.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (userRepository.existsByEmailAndProvider(email, User.Provider.LOCAL)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            String json = objectMapper.writeValueAsString(
                    ApiResponse.fail("GOOGLE_LOGIN_FAILED", "이미 이메일로 가입된 계정입니다. 이메일 로그인을 이용해주세요.")
            );
            response.getWriter().write(json);
            return;
        }

        User user = userRepository.findByEmailAndProvider(email, User.Provider.GOOGLE)
                .orElseGet(() -> {
                    String nickname = email.split("@")[0]
                            .replaceAll("[^a-zA-Z0-9_]", "")
                            .toLowerCase();
                    if (nickname.length() > 6) nickname = nickname.substring(0, 6);
                    if (nickname.isEmpty()) nickname = "user";

                    return userRepository.save(User.builder()
                            .email(email)
                            .nickname(nickname)
                            .provider(User.Provider.GOOGLE)
                            .agreeService(true)
                            .agreeFinance(true)
                            .agreePrivacy(false)
                            .build());
                });

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        LoginResponseDto loginResponse = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileColor(user.getProfileColor())
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        String json = objectMapper.writeValueAsString(
                ApiResponse.success("구글 로그인 성공", loginResponse)
        );
        response.getWriter().write(json);
    }
}