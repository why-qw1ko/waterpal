package com.waterpal.server.controller;

import com.waterpal.server.dto.ApiResponse;
import com.waterpal.server.dto.LoginRequest;
import com.waterpal.server.dto.LoginResponse;
import com.waterpal.server.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
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
     * 上报 FCM Token（JSON body）
     */
    @PostMapping("/fcm-token")
    public ApiResponse<Void> updateFcmToken(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, String> body) {
        String fcmToken = body.get("fcmToken");
        log.info("收到 FCM Token 上报, userId={}, token={}", userId,
                fcmToken != null ? fcmToken.substring(0, Math.min(20, fcmToken.length())) + "..." : "null");
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("FCM Token 为空, userId={}", userId);
            return ApiResponse.error("fcmToken 不能为空");
        }
        authService.updateFcmToken(userId, fcmToken);
        log.info("FCM Token 保存成功, userId={}", userId);
        return ApiResponse.success(null);
    }
}
