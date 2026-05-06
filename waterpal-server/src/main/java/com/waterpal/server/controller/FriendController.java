package com.waterpal.server.controller;

import com.waterpal.server.dto.ApiResponse;
import com.waterpal.server.repository.FriendVO;
import com.waterpal.server.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友控制器
 */
@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {
    
    private final FriendService friendService;
    
    /**
     * 获取好友列表
     */
    @GetMapping("/list")
    public ApiResponse<List<FriendVO>> getFriendList(
            @RequestHeader("X-User-Id") Long userId) {
        List<FriendVO> friends = friendService.getFriendList(userId);
        return ApiResponse.success(friends);
    }
    
    /**
     * 添加好友（通过手机号）
     */
    @PostMapping("/add")
    public ApiResponse<Void> addFriend(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String friendPhone) {
        // 简化：通过手机号查找用户 ID 后添加
        // 实际项目需要更完整的逻辑
        return ApiResponse.success(null);
    }
}
