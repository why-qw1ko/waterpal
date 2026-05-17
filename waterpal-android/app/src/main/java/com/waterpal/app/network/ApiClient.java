package com.waterpal.app.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Retrofit 网络请求单例
 */
public class ApiClient {
    
    // 后端 API 地址（模拟器用 10.0.2.2，真机用实际 IP）
    private static final String BASE_URL = "http://192.168.11.4:8080/api/";
    
    private static volatile Retrofit retrofit = null;
    private static volatile String authToken = null;
    private static volatile String currentUserId = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(chain -> {
                            okhttp3.Request original = chain.request();
                            okhttp3.Request.Builder requestBuilder = original.newBuilder();
                            
                            String token = authToken;
                            if (token != null && !token.isEmpty()) {
                                String cleanToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
                                requestBuilder.header("Authorization", "Bearer " + cleanToken);
                            }
                            
                            if (currentUserId != null && !currentUserId.isEmpty()) {
                                requestBuilder.header("X-User-Id", currentUserId);
                            }
                            
                            requestBuilder.header("Accept", "application/json");
                            requestBuilder.header("User-Agent", "WaterPal-Android-App");
                            
                            okhttp3.Response response = chain.proceed(requestBuilder.build());
                            
                            // 如果返回 401 或 403，可能说明 Token 失效或权限变动
                            if (response.code() == 401 || response.code() == 403) {
                                synchronized (ApiClient.class) {
                                    authToken = null;
                                    currentUserId = null;
                                    retrofit = null;
                                    Log.e("ApiClient", "Auth error (" + response.code() + "), clearing credentials");
                                }
                            }
                            
                            return response;
                        })
                        .addInterceptor(logging);
                    
                    retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();
                }
            }
        }
        return retrofit;
    }
    
    public static void setAuthToken(String token, String userId) {
        if (token != null) {
            Log.d("ApiClient", "Setting Auth: " + (token.length() > 10 ? token.substring(0, 10) : token) + ", UID: " + userId);
            authToken = token.trim();
            currentUserId = userId;
        } else {
            Log.d("ApiClient", "Clearing Auth credentials");
            authToken = null;
            currentUserId = null;
        }
        retrofit = null;
    }
    
    /**
     * @deprecated 使用 {@link #setAuthToken(String, String)} 替代
     */
    @Deprecated
    public static void setAuthToken(String token) {
        setAuthToken(token, null);
    }
    
    public static String getAuthToken() {
        return authToken;
    }
}
