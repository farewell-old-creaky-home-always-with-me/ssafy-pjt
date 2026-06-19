package com.ssafy.home.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/actuator")) {
            return;
        }

        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long executionTime = 0;
        if (startTime != null) {
            executionTime = System.currentTimeMillis() - startTime;
        }

        String method = request.getMethod();
        int status = response.getStatus();

        HttpStatus httpStatus = HttpStatus.resolve(status);
        String statusStr = httpStatus != null 
                ? status + " " + httpStatus.name() 
                : String.valueOf(status);

        log.info("[HTTP] {} {} - {} [Execution Time: {}ms]", method, uri, statusStr, executionTime);
    }
}
