package com.ssafy.home.member.service.mail;

public interface PasswordResetMailSender {

    void send(String email, String name, String temporaryPassword);
}
