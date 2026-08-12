package com.waterpal.app.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.waterpal.app.databinding.ActivitySettingsBinding;
import com.waterpal.app.databinding.DialogColorPickerBinding;
import com.waterpal.app.ui.widget.RoundedSeekBar;
import com.waterpal.app.util.PreferenceManager;
import com.waterpal.app.util.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding b;
    private int currentColor;

    // 12 个预置配色
    private static final int[] PRESET_COLORS = {
        0xFFFF6B6B, 0xFFF783AC, 0xFFFFA94D, 0xFF339AF0,
        0xFF20C997, 0xFF9775FA, 0xFF40C057, 0xFFE64980,
        0xFF1C7ED6, 0xFFF59F00, 0xFF6366F1, 0xFF74C0FC,
    };
    private static final String[] PRESET_NAMES = {
        "珊瑚橙", "蜜桃粉", "暖阳橘", "海洋蓝",
        "青碧绿", "薰衣草", "森林绿", "玫瑰红",
        "深海蓝", "琥珀金", "紫罗兰", "天空蓝",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        b = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        currentColor = ThemeManager.getPrimaryColor(this);
        applyColorToUI(currentColor);
        buildPresetSwatches();

        b.toolbarSettings.setNavigationOnClickListener(v -> finish());
        b.btnColorPicker.setOnClickListener(v -> showColorPickerDialog());
        b.viewSelectedColor.setOnClickListener(v -> showColorPickerDialog());
        setupAvatarToggle();
    }

    // ==================== 预设色块（设置页面） ====================

    private void buildPresetSwatches() {
        LinearLayout container = b.containerSwatches;
        container.removeAllViews();

        int swatchSize = (int) (44 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);

        for (int row = 0; row < 3; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.bottomMargin = margin;
            rowLayout.setLayoutParams(rp);

            for (int col = 0; col < 4; col++) {
                final int idx = row * 4 + col;
                final int color = PRESET_COLORS[idx];

                FrameLayout swatch = new FrameLayout(this);
                LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(swatchSize, swatchSize);
                sp.leftMargin = margin;
                sp.rightMargin = margin;
                swatch.setLayoutParams(sp);

                View circle = new View(this);
                FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                circle.setLayoutParams(cp);
                GradientDrawable gd = new GradientDrawable();
                gd.setShape(GradientDrawable.OVAL);
                gd.setColor(color);
                circle.setBackground(gd);
                swatch.addView(circle);

                swatch.setClickable(true);
                swatch.setFocusable(true);
                swatch.setBackground(null);
                swatch.setOnClickListener(v -> {
                    currentColor = color;
                    ThemeManager.setPrimaryColor(this, currentColor);
                    applyColorToUI(currentColor);
                    Toast.makeText(this, "主题色已更新", Toast.LENGTH_SHORT).show();
                });

                rowLayout.addView(swatch);
            }
            container.addView(rowLayout);
        }
    }

    // ==================== HSV 色盘弹窗 ====================

    private void showColorPickerDialog() {
        DialogColorPickerBinding d = DialogColorPickerBinding.inflate(LayoutInflater.from(this));

        float[] initHsv = new float[3];
        Color.colorToHSV(currentColor, initHsv);

        final int[] selectedColor = {currentColor};
        final float[] hsv = {initHsv[0], initHsv[1], initHsv[2]};

        updatePreview(d, selectedColor[0]);

        // ---------- 色相条：彩虹渐变 ----------
        d.sbHue.setMax(360);
        d.sbHue.setProgress((int) hsv[0]);
        d.sbHue.setTrackColor(0xFFE2E8F0);
        d.sbHue.setProgressGradient(
            Color.HSVToColor(new float[]{0, 1f, 1f}),
            Color.HSVToColor(new float[]{360, 1f, 1f}));

        // ---------- 饱和度条：灰 → 彩色渐变 ----------
        d.sbSaturation.setMax(100);
        d.sbSaturation.setProgress((int) (hsv[1] * 100));
        d.sbSaturation.setTrackColor(0xFFF0F0F0);
        d.sbSaturation.setProgressGradient(
            Color.HSVToColor(new float[]{hsv[0], 0f, 1f}),
            Color.HSVToColor(new float[]{hsv[0], 1f, 1f}));

        // ---------- 亮度条：黑 → 彩色渐变 ----------
        d.sbBrightness.setMax(100);
        d.sbBrightness.setProgress((int) (hsv[2] * 100));
        d.sbBrightness.setTrackColor(0xFF333333);
        d.sbBrightness.setProgressGradient(
            Color.HSVToColor(new float[]{hsv[0], hsv[1], 0f}),
            Color.HSVToColor(new float[]{hsv[0], hsv[1], 1f}));

        // ---------- 色相滑块 ----------
        d.sbHue.setOnSeekBarChangeListener(new RoundedSeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(RoundedSeekBar sb, int v, boolean fromUser) {
                if (fromUser) {
                    hsv[0] = v;
                    selectedColor[0] = Color.HSVToColor(hsv);
                    updatePreview(d, selectedColor[0]);
                    updateBarColors(d, hsv);
                }
            }
            @Override public void onStartTrackingTouch(RoundedSeekBar sb) { }
            @Override public void onStopTrackingTouch(RoundedSeekBar sb) { }
        });

        // ---------- 饱和度滑块 ----------
        d.sbSaturation.setOnSeekBarChangeListener(new RoundedSeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(RoundedSeekBar sb, int v, boolean fromUser) {
                if (fromUser) {
                    hsv[1] = v / 100f;
                    selectedColor[0] = Color.HSVToColor(hsv);
                    updatePreview(d, selectedColor[0]);
                    updateBarColors(d, hsv);
                }
            }
            @Override public void onStartTrackingTouch(RoundedSeekBar sb) { }
            @Override public void onStopTrackingTouch(RoundedSeekBar sb) { }
        });

        // ---------- 亮度滑块 ----------
        d.sbBrightness.setOnSeekBarChangeListener(new RoundedSeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(RoundedSeekBar sb, int v, boolean fromUser) {
                if (fromUser) {
                    hsv[2] = v / 100f;
                    selectedColor[0] = Color.HSVToColor(hsv);
                    updatePreview(d, selectedColor[0]);
                    // 亮度条渐变色不需要更新（黑色→彩色 不受亮度值影响）
                }
            }
            @Override public void onStartTrackingTouch(RoundedSeekBar sb) { }
            @Override public void onStopTrackingTouch(RoundedSeekBar sb) { }
        });

        new MaterialAlertDialogBuilder(this)
            .setTitle("选择主题色")
            .setView(d.getRoot())
            .setPositiveButton("确定", (dia, which) -> {
                currentColor = selectedColor[0];
                ThemeManager.setPrimaryColor(this, currentColor);
                applyColorToUI(currentColor);
                Toast.makeText(this, "主题色已更新", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    /** 色相/饱和度变化时同步更新 bar 渐变色 */
    private void updateBarColors(DialogColorPickerBinding d, float[] hsv) {
        // 色相条：彩虹渐变不变
        // 饱和度条：灰 → 当前色相的全饱和色
        d.sbSaturation.setProgressGradient(
            Color.HSVToColor(new float[]{hsv[0], 0f, 1f}),
            Color.HSVToColor(new float[]{hsv[0], 1f, 1f}));
        // 亮度条：黑 → 当前色
        d.sbBrightness.setProgressGradient(
            Color.HSVToColor(new float[]{hsv[0], hsv[1], 0f}),
            Color.HSVToColor(new float[]{hsv[0], hsv[1], 1f}));
    }

    // ==================== 预览 ====================

    private void updatePreview(DialogColorPickerBinding d, int color) {
        ((GradientDrawable) d.viewColorPreview.getBackground()).setColor(color);
        d.tvHexValue.setText(String.format("#%06X", 0xFFFFFF & color));
    }

    // ==================== UI ====================

    private void applyColorToUI(int color) {
        if (getWindow() != null) getWindow().setStatusBarColor(ThemeManager.darken(color, 0.85f));
        b.toolbarSettings.setBackgroundColor(color);
        ((GradientDrawable) b.viewSelectedColor.getBackground()).setColor(color);
    }

    private void setupAvatarToggle() {
        b.swShowAvatars.setChecked(PreferenceManager.isShowAvatars(this));
        b.swShowAvatars.setOnCheckedChangeListener((btn, checked) -> {
            PreferenceManager.setShowAvatars(this, checked);
            Toast.makeText(this, checked ? "已开启好友头像" : "已关闭，使用文字头像", Toast.LENGTH_SHORT).show();
        });
    }
}
