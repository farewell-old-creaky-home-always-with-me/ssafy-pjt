package com.ssafy.home.global.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        String encodingId = "bcrypt";
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encodingId, bcryptPasswordEncoder);

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(encodingId, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder);
        return passwordEncoder;
    }
}
