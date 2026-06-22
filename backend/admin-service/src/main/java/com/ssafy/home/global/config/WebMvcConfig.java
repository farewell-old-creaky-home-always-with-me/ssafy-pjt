package com.ssafy.home.global.config;

import com.ssafy.home.global.auth.LoginMemberIdArgumentResolver;
import com.ssafy.home.global.interceptor.AuthInterceptor;
import com.ssafy.home.global.interceptor.HttpLoggingInterceptor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final HttpLoggingInterceptor httpLoggingInterceptor;
    private final AuthInterceptor authInterceptor;
    private final LoginMemberIdArgumentResolver loginMemberIdArgumentResolver;

    public WebMvcConfig(
            HttpLoggingInterceptor httpLoggingInterceptor,
            AuthInterceptor authInterceptor,
            LoginMemberIdArgumentResolver loginMemberIdArgumentResolver
    ) {
        this.httpLoggingInterceptor = httpLoggingInterceptor;
        this.authInterceptor = authInterceptor;
        this.loginMemberIdArgumentResolver = loginMemberIdArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpLoggingInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberIdArgumentResolver);
    }
}
