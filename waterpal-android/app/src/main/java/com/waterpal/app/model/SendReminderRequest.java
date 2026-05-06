package com.waterpal.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 发送提醒请求
 */
public class SendReminderRequest {
    
    @SerializedName("friendId")
    private Long friendId;
    
    @SerializedName("message")
    private String message;
    
    public SendReminderRequest(Long friendId, String message) {
        this.friendId = friendId;
        this.message = message;
    }
}
