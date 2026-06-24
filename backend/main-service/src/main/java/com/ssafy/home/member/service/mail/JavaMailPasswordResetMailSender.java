package com.ssafy.home.member.service.mail;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JavaMailPasswordResetMailSender implements PasswordResetMailSender {

    private final JavaMailSenderImpl javaMailSender;

    public JavaMailPasswordResetMailSender(
            @Value("${spring.mail.host:}") String host,
            @Value("${spring.mail.port:587}") int port,
            @Value("${spring.mail.username:}") String username,
            @Value("${spring.mail.password:}") String password,
            @Value("${spring.mail.properties.mail.smtp.auth:true}") boolean auth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") boolean starttlsEnable
    ) {
        this.javaMailSender = new JavaMailSenderImpl();
        this.javaMailSender.setHost(host);
        this.javaMailSender.setPort(port);
        this.javaMailSender.setUsername(username);
        this.javaMailSender.setPassword(password);

        Properties properties = this.javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", Boolean.toString(auth));
        properties.put("mail.smtp.starttls.enable", Boolean.toString(starttlsEnable));
    }

    @Override
    public void send(String email, String name, String temporaryPassword) {
        if (!StringUtils.hasText(javaMailSender.getHost())) {
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
