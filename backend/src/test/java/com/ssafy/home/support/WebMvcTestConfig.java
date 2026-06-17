package com.ssafy.home.support;

import com.ssafy.home.global.auth.LoginMemberIdArgumentResolver;
import com.ssafy.home.global.config.WebMvcConfig;
import com.ssafy.home.global.exception.GlobalExceptionHandler;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        GlobalExceptionHandler.class,
        WebMvcConfig.class,
        AuthInterceptor.class,
        LoginMemberIdArgumentResolver.class
})
public class WebMvcTestConfig {
}
