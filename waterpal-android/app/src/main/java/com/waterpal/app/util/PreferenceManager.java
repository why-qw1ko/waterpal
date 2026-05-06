package com.waterpal.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 */
public class PreferenceManager {
    
    private static final String PREF_NAME = "waterpal_prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NICKNAME = "nickname";
    
    public static void saveToken(Context context, String token) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply();
    }
    
    public static String getToken(Context context) {
        return getPrefs(context).getString(KEY_TOKEN, null);
    }
    
    public static void saveUserId(Context context, Long userId) {
        getPrefs(context).edit().putLong(KEY_USER_ID, userId).apply();
    }
    
    public static Long getUserId(Context context) {
        return getPrefs(context).getLong(KEY_USER_ID, -1);
    }
    
    public static void saveNickname(Context context, String nickname) {
        getPrefs(context).edit().putString(KEY_NICKNAME, nickname).apply();
    }
    
    public static String getNickname(Context context) {
        return getPrefs(context).getString(KEY_NICKNAME, null);
    }
    
    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }
    
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
