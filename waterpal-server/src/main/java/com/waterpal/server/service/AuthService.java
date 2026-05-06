package com.waterpal.server.service;

import com.waterpal.server.dto.LoginRequest;
import com.waterpal.server.dto.LoginResponse;
import com.waterpal.server.entity.User;
import com.waterpal.server.repository.UserMapper;
import com.waterpal.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    /**
     * 登录（模拟验证码验证）
     */
    public LoginResponse login(LoginRequest request) {
        // 模拟验证码验证（实际项目应接入短信服务）
        if (!"1234".equals(request.getCode())) {
            throw new RuntimeException("验证码错误");
        }
        
        User user = userMapper.findByPhone(request.getPhone());
        if (user == null) {
            // 自动注册
            user = new User();
            user.setPhone(request.getPhone());
            user.setNickname("用户" + request.getPhone().substring(7));
            user.setDailyGoal(8);
            userMapper.insert(user);
        }
        
        String token = jwtUtil.generateToken(user.getId());
        
        return new LoginResponse(
            user.getId(),
            token,
            user.getNickname(),
            user.getAvatarUrl()
        );
    }
    
    /**
     * 更新 FCM Token
     */
    public void updateFcmToken(Long userId, String fcmToken) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setFcmToken(fcmToken);
            userMapper.updateById(user);
        }
    }
}
