package com.team5.web_ide.domain.auth.service;

import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.success-redirect-uri:http://localhost:5173/design/minimal/oauth/callback}")
    private String successRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (userRepository.existsByEmailAndProvider(email, User.Provider.LOCAL)) {
            redirectWithError(response, "이미 이메일로 가입된 계정입니다. 이메일 로그인을 이용해주세요.");
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

        String redirectUrl = UriComponentsBuilder.fromUriString(successRedirectUri)
                .fragment(UriComponentsBuilder.newInstance()
                        .queryParam("accessToken", accessToken)
                        .queryParam("refreshToken", refreshToken)
                        .queryParam("userId", user.getId())
                        .queryParam("nickname", user.getNickname())
                        .queryParam("profileColor", user.getProfileColor())
                        .build()
                        .getQuery())
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String redirectUrl = UriComponentsBuilder.fromUriString(successRedirectUri)
                .fragment(UriComponentsBuilder.newInstance()
                        .queryParam("error", message)
                        .build()
                        .getQuery())
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
