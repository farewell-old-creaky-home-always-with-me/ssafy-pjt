package com.ssafy.home.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

    private final PasswordEncoder passwordEncoder = new PasswordConfig().passwordEncoder();

    @Test
    void encodedPasswordIncludesAlgorithmId() {
        String encodedPassword = passwordEncoder.encode("password1234");

        assertThat(encodedPassword).startsWith("{bcrypt}");
        assertThat(passwordEncoder.matches("password1234", encodedPassword)).isTrue();
    }

    @Test
    void legacyBcryptPasswordWithoutAlgorithmIdStillMatches() {
        String legacyPassword = new BCryptPasswordEncoder().encode("password1234");

        assertThat(passwordEncoder.matches("password1234", legacyPassword)).isTrue();
    }
}
