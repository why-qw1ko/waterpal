package com.waterpal.server.dto;

import lombok.Data;

/**
 * 发送提醒请求
 */
@Data
public class SendReminderRequest {
    
    private Long friendId;
    
    private String message;
}
