package com.ssafy.home.member.service.mail;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class JavaMailPasswordResetMailSender implements PasswordResetMailSender {

    private final ObjectProvider<JavaMailSender> javaMailSenderProvider;

    public JavaMailPasswordResetMailSender(ObjectProvider<JavaMailSender> javaMailSenderProvider) {
        this.javaMailSenderProvider = javaMailSenderProvider;
    }

    @Override
    public void send(String email, String name, String temporaryPassword) {
        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
        if (javaMailSender == null) {
            throw new MailSendException("spring.mail.host is required to send password reset email");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[SSAFY Home] 임시 비밀번호 안내");
        message.setText("""
                %s님, 요청하신 임시 비밀번호입니다.

                임시 비밀번호: %s

                로그인 후 비밀번호를 변경해 주세요.
                """.formatted(name, temporaryPassword));
        javaMailSender.send(message);
    }
}
