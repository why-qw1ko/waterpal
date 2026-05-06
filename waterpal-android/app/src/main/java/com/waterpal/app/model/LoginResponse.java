package com.waterpal.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 登录响应
 */
public class LoginResponse {
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("nickname")
    private String nickname;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    public Long getUserId() { return userId; }
    public String getToken() { return token; }
    public String getNickname() { return nickname; }
    public String getAvatarUrl() { return avatarUrl; }
}
