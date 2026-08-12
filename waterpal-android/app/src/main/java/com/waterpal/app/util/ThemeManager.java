package com.waterpal.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Window;

/**
 * 主题管理工具类 —— 全面管理全局色调
 */
public class ThemeManager {

    public static final int DEFAULT_PRIMARY = 0xFF03A9F4;

    // ========== 颜色存取 ==========

    public static int getPrimaryColor(Context context) {
        return PreferenceManager.getPrimaryColor(context);
    }

    public static void setPrimaryColor(Context context, int color) {
        PreferenceManager.savePrimaryColor(context, color);
    }

    // ========== 颜色计算 ==========

    public static int darken(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    public static int lighten(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) + (255 - Color.red(color)) * factor);
        int g = Math.round(Color.green(color) + (255 - Color.green(color)) * factor);
        int b = Math.round(Color.blue(color) + (255 - Color.blue(color)) * factor);
        return Color.argb(a, Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    public static int alpha(int color, float alpha) {
        int a = Math.round(255 * alpha);
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
    }

    // ========== 主题 Drawable 生成 ==========

    /** 头像圆形渐变背景 */
    public static GradientDrawable avatarCircle(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setColors(new int[]{color, lighten(color, 0.3f)});
        return gd;
    }

    /** 实心圆角按钮渐变 */
    public static GradientDrawable gradientButton(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setCornerRadius(48f);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setColors(new int[]{color, lighten(color, 0.2f)});
        return gd;
    }

    /** 未读小红点（大小由 View 决定） */
    public static GradientDrawable unreadDot(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(color);
        return gd;
    }

    /** 空心圆（减号按钮） */
    public static GradientDrawable outlineCircle(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.WHITE);
        gd.setStroke(3, color);
        return gd;
    }

    /** Logo 阴影 */
    public static GradientDrawable logoShadow(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setColors(new int[]{color, lighten(color, 0.25f)});
        return gd;
    }

    /** 色块预览（设置页） */
    public static GradientDrawable colorSwatch(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(color);
        return gd;
    }

    // ========== 全局应用 ==========

    /**
     * 在 Activity setContentView 之前调用
     */
    public static void applyTheme(Activity activity) {
        int color = getPrimaryColor(activity);
        Window window = activity.getWindow();
        if (window != null) {
            window.setStatusBarColor(darken(color, 0.85f));
        }
    }

    /**
     * 全面应用主题色到 Activity 的各处 UI
     */
    public static void applyToActivity(Activity activity, com.google.android.material.appbar.MaterialToolbar toolbar,
                                        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav) {
        int color = getPrimaryColor(activity);
        int dark = darken(color, 0.85f);

        // 状态栏
        if (activity.getWindow() != null) {
            activity.getWindow().setStatusBarColor(dark);
        }

        // Toolbar
        toolbar.setBackgroundColor(color);

        // 底部导航
        if (bottomNav != null) {
            int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
            };
            int[] colors = new int[]{color, 0xFFA0AEC0};
            bottomNav.setItemIconTintList(new ColorStateList(states, colors));
            bottomNav.setItemTextColor(new ColorStateList(states, colors));
            // 去除图标后方圆角阴影
            bottomNav.setItemActiveIndicatorEnabled(false);
        }
    }
}
