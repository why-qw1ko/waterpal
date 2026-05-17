package com.waterpal.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.waterpal.app.network.ApiClient;
import com.waterpal.app.util.PreferenceManager;

/**
 * Application 入口
 */
public class WaterPalApplication extends Application {
    
    public static final String CHANNEL_ID = "water_reminder";
    public static final String CHANNEL_NAME = "喝水提醒";
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        
        // 初始化网络请求 Token
        String token = PreferenceManager.getToken(this);
        long userId = PreferenceManager.getUserId(this);
        if (token != null && userId != -1) {
            ApiClient.setAuthToken(token, String.valueOf(userId));
        }
    }
    
    /**
     * 创建通知渠道（Android 8.0+）
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("朋友发送的喝水提醒通知");
            channel.enableVibration(true);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
