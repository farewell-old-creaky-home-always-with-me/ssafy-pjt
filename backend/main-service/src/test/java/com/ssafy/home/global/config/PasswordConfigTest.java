package com.ssafy.home.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

    private final PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder();

    @Test
    @DisplayName("인코딩된 비밀번호에 알고리즘 ID가 포함된다")
    void encodedPasswordIncludesAlgorithmId() {
        // when
        String encodedPassword = passwordEncoder.encode("password1234");

        // then
        assertThat(encodedPassword).startsWith("{bcrypt}");
        assertThat(passwordEncoder.matches("password1234", encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("알고리즘 ID 없는 레거시 bcrypt 비밀번호도 검증된다")
    void legacyBcryptPasswordWithoutAlgorithmIdStillMatches() {
        // given
        String legacyPassword = new BCryptPasswordEncoder().encode("password1234");

        // when / then
        assertThat(passwordEncoder.matches("password1234", legacyPassword)).isTrue();
    }
}
