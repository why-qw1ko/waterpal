package com.waterpal.server.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.waterpal.server.dto.ApiResponse;
import com.waterpal.server.entity.User;
import com.waterpal.server.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> getProfile(
            @RequestHeader("X-User-Id") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("phone", user.getPhone());
        profile.put("nickname", user.getNickname());
        profile.put("avatarUrl", user.getAvatarUrl());
        profile.put("dailyGoal", user.getDailyGoal() != null ? user.getDailyGoal() : 8);

        return ApiResponse.success(profile);
    }

    /**
     * 更新用户信息（昵称、每日目标等）
     */
    @PutMapping("/profile")
    public ApiResponse<Void> updateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody Map<String, Object> updates) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", userId);

        if (updates.containsKey("nickname")) {
            String nickname = (String) updates.get("nickname");
            if (nickname != null && !nickname.trim().isEmpty()) {
                wrapper.set("nickname", nickname.trim());
            }
        }

        if (updates.containsKey("dailyGoal")) {
            Object goalObj = updates.get("dailyGoal");
            int goal = goalObj instanceof Number ? ((Number) goalObj).intValue() : 8;
            if (goal >= 1 && goal <= 20) {
                wrapper.set("daily_goal", goal);
            }
        }

        if (updates.containsKey("avatarUrl")) {
            String avatarUrl = (String) updates.get("avatarUrl");
            wrapper.set("avatar_url", avatarUrl);
        }

        userMapper.update(null, wrapper);
        return ApiResponse.success(null);
    }
}
