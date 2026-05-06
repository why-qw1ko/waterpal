package com.waterpal.server.controller;

import com.waterpal.server.dto.ApiResponse;
import com.waterpal.server.dto.LoginRequest;
import com.waterpal.server.dto.LoginResponse;
import com.waterpal.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }
    
    /**
     * 上报 FCM Token
     */
    @PostMapping("/fcm-token")
    public ApiResponse<Void> updateFcmToken(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String fcmToken) {
        authService.updateFcmToken(userId, fcmToken);
        return ApiResponse.success(null);
    }
}
