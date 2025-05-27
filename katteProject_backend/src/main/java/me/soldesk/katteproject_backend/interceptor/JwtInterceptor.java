package me.soldesk.katteproject_backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    // 모든 요청 전에 실행됨
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            // TODO: JWT 검증 로직 삽입 (예: 서명 확인, 만료 확인 등)
            return true; // 검증 통과
        }

        // 인증 실패
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Unauthorized - JWT 토큰 없음 또는 유효하지 않음");
        return false;
    }
}