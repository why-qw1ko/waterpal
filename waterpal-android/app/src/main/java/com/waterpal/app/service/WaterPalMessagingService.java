package com.waterpal.app.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.waterpal.app.R;
import com.waterpal.app.WaterPalApplication;
import com.waterpal.app.ui.activity.MainActivity;

/**
 * Firebase 消息服务
 */
public class WaterPalMessagingService extends FirebaseMessagingService {
    
    private static final String TAG = "WaterPalMessaging";
    
    @Override
    public void onMessageReceived(RemoteMessage message) {
        // 处理接收到的消息
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            showNotification(title, body);
        }
        
        // 处理数据 payload
        if (!message.getData().isEmpty()) {
            String type = message.getData().get("type");
            if ("reminder".equals(type)) {
                // 收到喝水提醒
                String title = "💧 喝水提醒";
                String body = message.getData().get("message");
                if (body == null) body = "你的朋友提醒你喝水啦！";
                showNotification(title, body);
            }
        }
    }
    
    @Override
    public void onNewToken(String token) {
        // Token 刷新时上报到服务器
        sendRegistrationToServer(token);
    }
    
    /**
     * 显示通知
     */
    private void showNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, WaterPalApplication.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);
        
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }
    
    /**
     * 上报 FCM Token 到服务器
     */
    private void sendRegistrationToServer(String token) {
        // TODO: 调用 API 上报 token
        // 可以在应用启动时或 token 变化时调用
    }
}
