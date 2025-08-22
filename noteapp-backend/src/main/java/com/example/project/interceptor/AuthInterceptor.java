package com.example.project.interceptor;

import com.example.project.common.ex.BusinessException;
import com.example.project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException("未登录");
        }

        // 2. 提取token
        String token = authHeader.substring(7);
        if (token.isEmpty()) {
            throw new BusinessException("未登录");
        }

        // 3. 验证token
        jwtUtils.validateToken(token);

        // 4. 获取用户ID并存入request
        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("X-User-Id", userId);

        return true;
    }
}
