package com.waterpal.server.controller;

import com.waterpal.server.dto.ApiResponse;
import com.waterpal.server.dto.SendReminderRequest;
import com.waterpal.server.repository.ReminderVO;
import com.waterpal.server.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提醒控制器
 */
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {
    
    private final ReminderService reminderService;
    
    /**
     * 发送喝水提醒
     */
    @PostMapping("/send")
    public ApiResponse<Void> sendReminder(
            @RequestHeader("X-User-Id") Long senderId,
            @RequestBody SendReminderRequest request) {
        reminderService.sendReminder(senderId, request.getFriendId(), request.getMessage());
        return ApiResponse.success(null);
    }
    
    /**
     * 获取收到的提醒
     */
    @GetMapping("/received")
    public ApiResponse<List<ReminderVO>> getReceivedReminders(
            @RequestHeader("X-User-Id") Long userId) {
        List<ReminderVO> reminders = reminderService.getReceivedReminders(userId);
        return ApiResponse.success(reminders);
    }
    
    /**
     * 获取发送的提醒
     */
    @GetMapping("/sent")
    public ApiResponse<List<ReminderVO>> getSentReminders(
            @RequestHeader("X-User-Id") Long userId) {
        List<ReminderVO> reminders = reminderService.getSentReminders(userId);
        return ApiResponse.success(reminders);
    }
}
