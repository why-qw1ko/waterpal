package com.waterpal.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 登录请求
 */
public class LoginRequest {
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("code")
    private String code;
    
    public LoginRequest(String phone, String code) {
        this.phone = phone;
        this.code = code;
    }
}
