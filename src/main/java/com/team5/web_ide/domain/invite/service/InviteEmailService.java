package com.team5.web_ide.domain.invite.service;

import com.team5.web_ide.config.InviteProperties;
import com.team5.web_ide.domain.invite.entity.ProjectInvite;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InviteEmailService {

    private final InviteProperties inviteProperties;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public void sendInviteEmail(ProjectInvite invite, String inviteUrl) {
        String subject = "[Egg-flow] 프로젝트 초대: " + invite.getProject().getProjectName();
        String body = """
                안녕하세요,

                '%s' 프로젝트에 %s 권한으로 초대되었습니다.
                아래 링크를 눌러 참여를 완료해 주세요.

                %s

                링크 유효기간: %s 까지
                """.formatted(
                invite.getProject().getProjectName(),
                invite.getRole().name(),
                inviteUrl,
                invite.getExpiresAt()
        );

        log.info("[Invite] to={} url={}", invite.getInviteeEmail(), inviteUrl);

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(inviteProperties.getFromEmail());
            message.setTo(invite.getInviteeEmail());
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("[Invite] 메일 발송 실패 — 링크는 로그에 기록됨: {}", ex.getMessage());
        }
    }
}
