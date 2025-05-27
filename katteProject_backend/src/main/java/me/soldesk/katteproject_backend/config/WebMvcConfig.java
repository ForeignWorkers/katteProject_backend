package me.soldesk.katteproject_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private me.soldesk.katteproject_backend.config.JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")                     // 모든 경로 적용
                .excludePathPatterns("/user/login", "/user", "/swagger-ui/**", "/v3/api-docs/**"); // 로그인 등은 제외
    }
}