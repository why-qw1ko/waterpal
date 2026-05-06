package com.waterpal.app.network;

import com.waterpal.app.model.*;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * API 接口定义
 */
public interface ApiService {
    
    /**
     * 登录
     */
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    
    /**
     * 上报 FCM Token
     */
    @POST("auth/fcm-token")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ApiResponse<Void>> updateFcmToken(
        @Header("X-User-Id") String userId,
        @Field("fcmToken") String fcmToken
    );
    
    /**
     * 获取好友列表
     */
    @GET("friends/list")
    Call<ApiResponse<List<Friend>>> getFriendList();
    
    /**
     * 添加好友
     */
    @FormUrlEncoded
    @POST("friends/add")
    Call<ApiResponse<Void>> addFriend(@Field("friendPhone") String friendPhone);
    
    /**
     * 发送喝水提醒
     */
    @POST("reminders/send")
    Call<ApiResponse<Void>> sendReminder(@Body SendReminderRequest request);
    
    /**
     * 获取收到的提醒
     */
    @GET("reminders/received")
    Call<ApiResponse<List<Reminder>>> getReceivedReminders();
    
    /**
     * 获取发送的提醒
     */
    @GET("reminders/sent")
    Call<ApiResponse<List<Reminder>>> getSentReminders();
}
