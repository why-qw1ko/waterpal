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
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.activity.MainActivity;
import com.waterpal.app.util.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Firebase 消息服务
 */
public class WaterPalMessagingService extends FirebaseMessagingService {

    private static final String TAG = "WaterPalMessaging";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        // 纯 data 消息 — onMessageReceived 始终被调用（无论前后台）
        String type = message.getData().get("type");
        if ("reminder".equals(type)) {
            String title = message.getData().get("title");
            String body = message.getData().get("body");
            if (title == null) title = "💧 喝水提醒";
            if (body == null) body = "你的朋友提醒你喝水啦！";
            showNotification(title, body);
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
            int notificationId = (int) (System.currentTimeMillis() / 1000);
            manager.notify(notificationId, builder.build());
        }
    }

    /**
     * 上报 FCM Token 到服务器
     */
    private void sendRegistrationToServer(String token) {
        String userId = String.valueOf(PreferenceManager.getUserId(this));
        if (userId.equals("-1")) {
            return;
        }

        java.util.Map<String, String> body = java.util.Collections.singletonMap("fcmToken", token);
        ApiClient.getClient().create(ApiService.class)
            .updateFcmToken(userId, body)
            .enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful()) {
                        android.util.Log.d(TAG, "FCM Token 上报成功");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    android.util.Log.e(TAG, "FCM Token 上报失败: " + t.getMessage());
                }
            });
    }
}
