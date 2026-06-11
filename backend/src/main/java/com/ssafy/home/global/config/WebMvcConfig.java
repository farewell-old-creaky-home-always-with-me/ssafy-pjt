package com.ssafy.home.global.config;

import com.ssafy.home.global.auth.LoginMemberIdArgumentResolver;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;

    public WebMvcConfig(
            AuthInterceptor authInterceptor,
            LoginMemberIdArgumentResolver loginMemberIdArgumentResolver
    ) {
        this.authInterceptor = authInterceptor;
        this.loginMemberIdArgumentResolver = loginMemberIdArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberIdArgumentResolver);
    }
}
