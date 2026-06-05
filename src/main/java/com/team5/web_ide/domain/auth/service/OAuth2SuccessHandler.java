package com.team5.web_ide.domain.auth.service;

import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import com.team5.web_ide.global.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final int NICKNAME_MAX_LENGTH = 6;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.oauth2.success-redirect-uri:http://localhost:5173/design/minimal/oauth/callback}")
    private String successRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            if (email == null || email.isBlank()) {
                redirectWithError(response, "구글 계정의 이메일 정보를 가져오지 못했습니다.");
                return;
            }

            User user = userRepository.findByEmail(email)
                    .map(existingUser -> {
                        if (existingUser.getProvider() != User.Provider.GOOGLE) {
                            throw new LocalAccountAlreadyExistsException();
                        }
                        return existingUser;
                    })
                    .orElseGet(() -> userRepository.save(User.builder()
                            .email(email)
                            .nickname(createAvailableNickname(email))
                            .provider(User.Provider.GOOGLE)
                            .agreeService(true)
                            .agreeFinance(true)
                            .agreePrivacy(false)
                            .build()));

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
        } catch (LocalAccountAlreadyExistsException ex) {
            redirectWithError(response, "이미 이메일로 가입된 계정입니다. 이메일 로그인을 이용해주세요.");
        } catch (Exception ex) {
            log.error("Google OAuth success handling failed", ex);
            redirectWithError(response, "구글 로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private String createAvailableNickname(String email) {
        String baseNickname = email.split("@")[0]
                .replaceAll("[^a-zA-Z0-9_]", "")
                .toLowerCase();

        if (baseNickname.isEmpty()) {
            baseNickname = "user";
        }

        if (baseNickname.length() > NICKNAME_MAX_LENGTH) {
            baseNickname = baseNickname.substring(0, NICKNAME_MAX_LENGTH);
        }

        String nickname = baseNickname;
        int suffix = 1;
        while (userRepository.existsByNickname(nickname)) {
            String suffixText = String.valueOf(suffix++);
            int prefixLength = Math.max(1, NICKNAME_MAX_LENGTH - suffixText.length());
            nickname = baseNickname.substring(0, Math.min(baseNickname.length(), prefixLength)) + suffixText;
        }

        return nickname;
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

    private static class LocalAccountAlreadyExistsException extends RuntimeException {
    }
}
