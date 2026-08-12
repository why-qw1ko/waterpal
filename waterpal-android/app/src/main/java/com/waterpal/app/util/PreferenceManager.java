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
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_DAILY_GOAL = "daily_goal";
    private static final String KEY_PRIMARY_COLOR = "primary_color";
    private static final String KEY_SHOW_AVATARS = "show_avatars";

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

    public static void savePhone(Context context, String phone) {
        getPrefs(context).edit().putString(KEY_PHONE, phone).apply();
    }

    public static String getPhone(Context context) {
        return getPrefs(context).getString(KEY_PHONE, null);
    }

    public static void saveNickname(Context context, String nickname) {
        getPrefs(context).edit().putString(KEY_NICKNAME, nickname).apply();
    }

    public static String getNickname(Context context) {
        return getPrefs(context).getString(KEY_NICKNAME, null);
    }

    public static void saveDailyGoal(Context context, int goal) {
        getPrefs(context).edit().putInt(KEY_DAILY_GOAL, goal).apply();
    }

    public static int getDailyGoal(Context context) {
        return getPrefs(context).getInt(KEY_DAILY_GOAL, 8);
    }

    /**
     * 主题色（int ARGB 值）
     */
    public static void savePrimaryColor(Context context, int color) {
        getPrefs(context).edit().putInt(KEY_PRIMARY_COLOR, color).apply();
    }

    public static int getPrimaryColor(Context context) {
        return getPrefs(context).getInt(KEY_PRIMARY_COLOR, 0xFF03A9F4);
    }

    /**
     * 是否显示好友头像
     */
    public static void setShowAvatars(Context context, boolean show) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_AVATARS, show).apply();
    }

    public static boolean isShowAvatars(Context context) {
        return getPrefs(context).getBoolean(KEY_SHOW_AVATARS, true);
    }

    public static void clear(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
