package com.waterpal.server.controller;

import com.waterpal.server.dto.ApiResponse;
import com.waterpal.server.entity.User;
import com.waterpal.server.repository.FriendVO;
import com.waterpal.server.repository.UserMapper;
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
    private final UserMapper userMapper;

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

        // 不能添加自己
        User targetUser = userMapper.findByPhone(friendPhone);
        if (targetUser == null) {
            throw new RuntimeException("该手机号未注册 WaterPal");
        }
        if (targetUser.getId().equals(userId)) {
            throw new RuntimeException("不能添加自己为好友");
        }

        friendService.addFriend(userId, targetUser.getId());
        return ApiResponse.success(null);
    }

    /**
     * 删除好友
     */
    @DeleteMapping("/delete/{friendId}")
    public ApiResponse<Void> deleteFriend(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long friendId) {
        friendService.deleteFriend(userId, friendId);
        return ApiResponse.success(null);
    }
}
