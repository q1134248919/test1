package com.family.assistant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.assistant.common.BusinessException;
import com.family.assistant.common.UserContext;
import com.family.assistant.dto.LoginRequest;
import com.family.assistant.dto.LoginResponse;
import com.family.assistant.dto.RegisterRequest;
import com.family.assistant.dto.UserVO;
import com.family.assistant.entity.User;
import com.family.assistant.interceptor.AuthInterceptor;
import com.family.assistant.mapper.UserMapper;
import com.family.assistant.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redis;

    public LoginResponse register(RegisterRequest req) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setPhone(StringUtils.hasText(req.getPhone()) ? req.getPhone() : "");
        user.setAvatar("");
        userMapper.insert(user);
        return issueToken(user);
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return issueToken(user);
    }

    public void logout(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            redis.delete(AuthInterceptor.TOKEN_KEY + token.substring(7));
        }
    }

    public UserVO currentUser() {
        User user = userMapper.selectById(UserContext.getUserId());
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        return toVO(user);
    }

    private LoginResponse issueToken(User user) {
        String token = jwtUtil.createToken(user.getId(), user.getUsername());
        redis.opsForValue().set(
                AuthInterceptor.TOKEN_KEY + token,
                String.valueOf(user.getId()),
                Duration.ofMillis(jwtUtil.getExpireMs()));
        return new LoginResponse(token, toVO(user));
    }

    private UserVO toVO(User user) {
        return new UserVO(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getPhone(),
                user.getFamilyId() == null ? null : String.valueOf(user.getFamilyId()));
    }
}
