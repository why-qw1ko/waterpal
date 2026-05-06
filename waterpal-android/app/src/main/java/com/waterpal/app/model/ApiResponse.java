package com.waterpal.app.model;

import com.google.gson.annotations.SerializedName;

/**
 * 统一响应包装
 */
public class ApiResponse<T> {
    
    @SerializedName("code")
    private int code;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private T data;
    
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    
    public boolean isSuccess() {
        return code == 200;
    }
}
