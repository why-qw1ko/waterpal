package com.waterpal.app.network;

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
    private static final String BASE_URL = "http://10.0.2.2:8080/api/";
    
    private static Retrofit retrofit = null;
    private static String authToken = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    okhttp3.Request.Builder requestBuilder = chain.request().newBuilder();
                    if (authToken != null) {
                        requestBuilder.addHeader("Authorization", "Bearer " + authToken);
                    }
                    return chain.proceed(requestBuilder.build());
                });
            
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        }
        return retrofit;
    }
    
    public static void setAuthToken(String token) {
        authToken = token;
        // 重置 retrofit 以应用新的 token
        retrofit = null;
    }
    
    public static String getAuthToken() {
        return authToken;
    }
}
