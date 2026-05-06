package com.waterpal.app.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * 提醒消息模型
 */
public class Reminder {
    
    @SerializedName("id")
    private Long id;
    
    @SerializedName("senderId")
    private Long senderId;
    
    @SerializedName("senderName")
    private String senderName;
    
    @SerializedName("senderAvatar")
    private String senderAvatar;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("isRead")
    private Integer isRead;
    
    @SerializedName("createdAt")
    private Date createdAt;
    
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getSenderAvatar() { return senderAvatar; }
    public String getMessage() { return message; }
    public Integer getIsRead() { return isRead; }
    public Date getCreatedAt() { return createdAt; }
}
