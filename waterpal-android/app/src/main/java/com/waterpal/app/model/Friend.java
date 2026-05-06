package com.waterpal.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 好友模型
 */
public class Friend {
    
    @SerializedName("friendId")
    private Long friendId;
    
    @SerializedName("nickname")
    private String nickname;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    public Long getFriendId() { return friendId; }
    public String getNickname() { return nickname; }
    public String getAvatarUrl() { return avatarUrl; }
}
