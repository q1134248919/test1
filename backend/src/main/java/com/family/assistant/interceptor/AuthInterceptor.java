package com.family.assistant.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.family.assistant.common.Result;
import com.family.assistant.common.UserContext;
import com.family.assistant.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String TOKEN_KEY = "auth:token:";

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            write401(response);
            return false;
        }
        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.parse(token);
            String cached = redis.opsForValue().get(TOKEN_KEY + token);
            if (cached == null) {
                write401(response);
                return false;
            }
            UserContext.setUserId(Long.parseLong(claims.getSubject()));
            return true;
        } catch (Exception e) {
            write401(response);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void write401(HttpServletResponse response) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(401, "未登录或登录已过期")));
    }
}
