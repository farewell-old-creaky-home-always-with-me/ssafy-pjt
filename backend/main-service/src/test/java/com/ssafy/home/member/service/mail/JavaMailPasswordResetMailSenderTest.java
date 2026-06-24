package com.ssafy.home.member.service.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class JavaMailPasswordResetMailSenderTest {

    @Mock
    private ObjectProvider<JavaMailSender> javaMailSenderProvider;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    @DisplayName("Spring Boot가 구성한 JavaMailSender로 비밀번호 재설정 메일을 발송한다")
    void sendUsesBootConfiguredJavaMailSender() {
        // given
        given(javaMailSenderProvider.getIfAvailable()).willReturn(javaMailSender);
        JavaMailPasswordResetMailSender passwordResetMailSender =
                new JavaMailPasswordResetMailSender(javaMailSenderProvider);

        // when
        passwordResetMailSender.send("user@example.com", "홍길동", "temp-password");

        // then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertThat(message.getTo()).containsExactly("user@example.com");
        assertThat(message.getSubject()).isEqualTo("[SSAFY Home] 임시 비밀번호 안내");
        assertThat(message.getText())
                .contains("홍길동")
                .contains("temp-password");
    }

    @Test
    @DisplayName("메일 설정이 없으면 발송 시점에 예외가 발생한다")
    void sendThrowsWhenJavaMailSenderIsMissing() {
        // given
        given(javaMailSenderProvider.getIfAvailable()).willReturn(null);
        JavaMailPasswordResetMailSender passwordResetMailSender =
                new JavaMailPasswordResetMailSender(javaMailSenderProvider);

        // when / then
        assertThatThrownBy(() -> passwordResetMailSender.send("user@example.com", "홍길동", "temp-password"))
                .isInstanceOf(MailSendException.class)
                .hasMessage("spring.mail.host is required to send password reset email");
    }
}
