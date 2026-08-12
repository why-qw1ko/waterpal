package com.waterpal.server.service;

import com.google.firebase.messaging.*;
import com.waterpal.server.entity.Reminder;
import com.waterpal.server.repository.ReminderMapper;
import com.waterpal.server.repository.ReminderVO;
import com.waterpal.server.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提醒服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderMapper reminderMapper;
    private final UserMapper userMapper;

    /**
     * 发送喝水提醒
     */
    public void sendReminder(Long senderId, Long receiverId, String message) {
        Reminder reminder = new Reminder();
        reminder.setSenderId(senderId);
        reminder.setReceiverId(receiverId);
        reminder.setMessage(message != null ? message : "该喝水啦！💧");
        reminder.setIsRead(0);
        reminderMapper.insert(reminder);

        sendPushNotification(receiverId, message);
    }

    /**
     * 获取收到的提醒
     */
    public List<ReminderVO> getReceivedReminders(Long userId) {
        return reminderMapper.getReceivedReminders(userId);
    }

    /**
     * 获取发送的提醒
     */
    public List<ReminderVO> getSentReminders(Long userId) {
        return reminderMapper.getSentReminders(userId);
    }

    /**
     * 标记提醒为已读
     */
    public void markAsRead(Long userId, Long reminderId) {
        Reminder reminder = reminderMapper.selectById(reminderId);
        if (reminder == null || !reminder.getReceiverId().equals(userId)) {
            throw new RuntimeException("提醒不存在或无权限");
        }
        reminder.setIsRead(1);
        reminderMapper.updateById(reminder);
    }

    /**
     * 标记提醒为未读
     */
    public void markAsUnread(Long userId, Long reminderId) {
        Reminder reminder = reminderMapper.selectById(reminderId);
        if (reminder == null || !reminder.getReceiverId().equals(userId)) {
            throw new RuntimeException("提醒不存在或无权限");
        }
        reminder.setIsRead(0);
        reminderMapper.updateById(reminder);
    }

    /**
     * 删除提醒
     */
    public void deleteReminder(Long userId, Long reminderId) {
        Reminder reminder = reminderMapper.selectById(reminderId);
        if (reminder == null || !reminder.getReceiverId().equals(userId)) {
            throw new RuntimeException("提醒不存在或无权限");
        }
        reminderMapper.deleteById(reminderId);
    }

    /**
     * 发送 Firebase 推送通知
     */
    private void sendPushNotification(Long userId, String message) {
        try {
            var user = userMapper.selectById(userId);
            if (user == null || user.getFcmToken() == null || user.getFcmToken().isBlank()) {
                log.warn("用户 {} 未注册 FCM Token", userId);
                return;
            }

            Message pushMessage = Message.builder()
                .setToken(user.getFcmToken())
                .putData("type", "reminder")
                .putData("title", "💧 喝水提醒")
                .putData("body", message != null ? message : "你的朋友提醒你喝水啦！")
                .putData("senderId", String.valueOf(userId))
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .build())
                .build();

            String response = FirebaseMessaging.getInstance().send(pushMessage);
            log.info("推送成功：{}", response);

        } catch (Exception e) {
            log.error("推送失败, userId={}", userId, e);
        }
    }
}
