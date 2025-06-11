package me.soldesk.katteproject_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로에 대해
                        .allowedOrigins("http://localhost:8080") // 허용할 Origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메소드
                        .allowCredentials(true); // 쿠키 등 자격 정보 허용
            }
        };
    }
}