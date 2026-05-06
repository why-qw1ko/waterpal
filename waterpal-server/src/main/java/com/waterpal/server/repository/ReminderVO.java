package com.waterpal.server.repository;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 提醒消息 VO
 */
@Data
public class ReminderVO {
    
    private Long id;
    
    private Long senderId;
    
    private Long receiverId;
    
    private String senderName;
    
    private String receiverName;
    
    private String senderAvatar;
    
    private String receiverAvatar;
    
    private String message;
    
    private Integer isRead;
    
    private LocalDateTime createdAt;
}
