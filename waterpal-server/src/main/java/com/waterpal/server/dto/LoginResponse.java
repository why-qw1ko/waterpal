package com.waterpal.server.dto;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    
    private Long userId;
    
    private String token;
    
    private String nickname;
    
    private String avatarUrl;
    
    public LoginResponse() {}
    
    public LoginResponse(Long userId, String token, String nickname, String avatarUrl) {
        this.userId = userId;
        this.token = token;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }
}
