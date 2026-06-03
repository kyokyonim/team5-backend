package com.team5.web_ide.domain.auth.service;

import com.team5.web_ide.domain.user.entity.User;
import com.team5.web_ide.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    // 토큰 임시 저장 (실제 서비스에서는 Redis 사용 권장)
    private final Map<String, Long> resetTokens = new ConcurrentHashMap<>();

    // 비밀번호 재설정 이메일 발송
    public void sendResetEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다"));

        if (user.getProvider() != User.Provider.LOCAL) {
            throw new IllegalArgumentException("소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다");
        }

        String token = UUID.randomUUID().toString();
        resetTokens.put(token, user.getId());

        String resetLink = "http://localhost:5174/design/minimal/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("team5devlab@gmail.com");
        message.setTo(email);
        message.setSubject("[EFIDE] 비밀번호 재설정");
        message.setText("아래 링크를 클릭하여 비밀번호를 재설정하세요.\n\n" + resetLink + "\n\n링크는 30분간 유효합니다.");

        mailSender.send(message);
    }

    // 비밀번호 재설정
    @Transactional
    public void resetPassword(String token, String newPassword) {

        Long userId = resetTokens.get(token);
        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다"));

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetTokens.remove(token);
    }
}