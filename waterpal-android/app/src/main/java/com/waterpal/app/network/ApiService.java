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
    Call<ApiResponse<Void>> updateFcmToken(
        @Header("X-User-Id") String userId,
        @Body Map<String, String> body
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
     * 删除好友
     */
    @DELETE("friends/delete/{friendId}")
    Call<ApiResponse<Void>> deleteFriend(@Path("friendId") Long friendId);

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

    /**
     * 标记提醒为已读
     */
    @PUT("reminders/{id}/read")
    Call<ApiResponse<Void>> markReminderRead(@Path("id") Long reminderId);

    /**
     * 标记提醒为未读
     */
    @PUT("reminders/{id}/unread")
    Call<ApiResponse<Void>> markReminderUnread(@Path("id") Long reminderId);

    /**
     * 删除提醒消息
     */
    @DELETE("reminders/{id}")
    Call<ApiResponse<Void>> deleteReminder(@Path("id") Long reminderId);

    /**
     * 获取当前用户信息
     */
    @GET("user/profile")
    Call<ApiResponse<Map<String, Object>>> getUserProfile();

    /**
     * 更新用户信息（昵称、每日目标等）
     */
    @PUT("user/profile")
    Call<ApiResponse<Void>> updateUserProfile(@Body Map<String, Object> updates);
}
