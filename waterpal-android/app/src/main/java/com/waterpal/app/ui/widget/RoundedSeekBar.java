package com.waterpal.app.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 圆角药丸形 SeekBar —— 支持渐变色进度条，两头全圆角。
 */
public class RoundedSeekBar extends View {

    public interface OnSeekBarChangeListener {
        void onProgressChanged(RoundedSeekBar seekBar, int progress, boolean fromUser);
        void onStartTrackingTouch(RoundedSeekBar seekBar);
        void onStopTrackingTouch(RoundedSeekBar seekBar);
    }

    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_TRACK_HEIGHT_DP = 8;
    private static final int DEFAULT_THUMB_RADIUS_DP = 12;

    private int max = DEFAULT_MAX;
    private int progress = 0;

    private int trackColor = 0xFFE2E8F0;
    private int progressStartColor = 0xFF38B2AC;
    private int progressEndColor = 0xFF38B2AC;
    private int thumbColor = Color.WHITE;
    private int thumbStrokeColor = 0xFFE2E8F0;
    private float thumbStrokeWidthPx;

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float trackHeightPx;
    private float thumbRadiusPx;
    private float density;

    private OnSeekBarChangeListener listener;
    private boolean isTracking;

    public RoundedSeekBar(Context context) {
        super(context);
        init(context);
    }

    public RoundedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        density = context.getResources().getDisplayMetrics().density;
        trackHeightPx = DEFAULT_TRACK_HEIGHT_DP * density;
        thumbRadiusPx = DEFAULT_THUMB_RADIUS_DP * density;
        thumbStrokeWidthPx = 1.5f * density;

        trackPaint.setStyle(Paint.Style.FILL);
        trackPaint.setColor(trackColor);

        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(progressStartColor);

        thumbFillPaint.setStyle(Paint.Style.FILL);
        thumbFillPaint.setColor(thumbColor);

        thumbStrokePaint.setStyle(Paint.Style.STROKE);
        thumbStrokePaint.setStrokeWidth(thumbStrokeWidthPx);
        thumbStrokePaint.setColor(thumbStrokeColor);
    }

    // ==================== 公开 API ====================

    public void setMax(int max) {
        this.max = Math.max(0, max);
        if (progress > this.max) setProgress(this.max);
        invalidate();
    }

    public int getMax() { return max; }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(progress, max));
        invalidate();
    }

    public int getProgress() { return progress; }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) { this.listener = l; }

    public void setTrackColor(int color) {
        this.trackColor = color;
        trackPaint.setColor(color);
        invalidate();
    }

    /** 设置单色进度 */
    public void setProgressColor(int color) {
        this.progressStartColor = color;
        this.progressEndColor = color;
        progressPaint.setShader(null);
        progressPaint.setColor(color);
        invalidate();
    }

    /** 设置渐变进度（左→右） */
    public void setProgressGradient(int startColor, int endColor) {
        this.progressStartColor = startColor;
        this.progressEndColor = endColor;
        // Shader 在 onDraw 中根据实际尺寸创建
        progressPaint.setShader(null);
        invalidate();
    }

    public void setThumbColor(int color) {
        this.thumbColor = color;
        thumbFillPaint.setColor(color);
        invalidate();
    }

    public void setThumbStrokeColor(int color) {
        this.thumbStrokeColor = color;
        thumbStrokePaint.setColor(color);
        invalidate();
    }

    // ==================== 测量 ====================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minH = (int) (thumbRadiusPx * 2) + getPaddingTop() + getPaddingBottom();
        int h = resolveSize(minH, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h);
    }

    // ==================== 绘制 ====================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float pl = getPaddingLeft();
        float pr = getPaddingRight();
        float trackW = getWidth() - pl - pr;
        float cy = getHeight() / 2f;

        float trackLeft = pl + thumbRadiusPx;
        float trackRight = pl + trackW - thumbRadiusPx;
        float trackTop = cy - trackHeightPx / 2;
        float trackBottom = cy + trackHeightPx / 2;
        float cr = trackHeightPx / 2f;

        // 轨道背景（药丸形）
        canvas.drawRoundRect(trackLeft, trackTop, trackRight, trackBottom, cr, cr, trackPaint);

        // 进度填充（药丸形 + 渐变支持）
        float progressRatio = max > 0 ? (float) progress / max : 0;
        float progressRight = trackLeft + (trackRight - trackLeft) * progressRatio;

        if (progressRatio > 0) {
            float actualRight = Math.max(progressRight, trackLeft + cr * 2);

            if (actualRight > trackLeft) {
                // 如果是渐变色，根据实际绘制区域更新 shader
                if (progressStartColor != progressEndColor) {
                    progressPaint.setShader(new LinearGradient(
                        trackLeft, 0, trackRight, 0,
                        progressStartColor, progressEndColor,
                        Shader.TileMode.CLAMP));
                }
                canvas.drawRoundRect(trackLeft, trackTop, actualRight, trackBottom, cr, cr, progressPaint);
            }
        }

        // 滑块（圆形）
        float thumbX = trackLeft + (trackRight - trackLeft) * progressRatio;
        canvas.drawCircle(thumbX, cy, thumbRadiusPx, thumbFillPaint);
        canvas.drawCircle(thumbX, cy, thumbRadiusPx - thumbStrokeWidthPx / 2, thumbStrokePaint);
    }

    // ==================== 触摸 ====================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pl = getPaddingLeft();
        float trackW = getWidth() - pl - getPaddingRight();
        float trackLeft = pl + thumbRadiusPx;
        float trackRight = pl + trackW - thumbRadiusPx;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                float thumbX = trackLeft + (trackRight - trackLeft) * (max > 0 ? (float) progress / max : 0);
                float dx = Math.abs(event.getX() - thumbX);
                if (dx <= thumbRadiusPx * 2.5f) {
                    isTracking = true;
                    if (listener != null) listener.onStartTrackingTouch(this);
                    updateProgressFromX(event.getX(), trackLeft, trackRight, true);
                    return true;
                }
                if (event.getX() >= trackLeft - thumbRadiusPx && event.getX() <= trackRight + thumbRadiusPx) {
                    isTracking = true;
                    if (listener != null) listener.onStartTrackingTouch(this);
                    updateProgressFromX(event.getX(), trackLeft, trackRight, true);
                    return true;
                }
                return false;

            case MotionEvent.ACTION_MOVE:
                if (isTracking) {
                    updateProgressFromX(event.getX(), trackLeft, trackRight, true);
                    return true;
                }
                return false;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isTracking) {
                    isTracking = false;
                    if (listener != null) listener.onStopTrackingTouch(this);
                    return true;
                }
                return false;
        }
        return super.onTouchEvent(event);
    }

    private void updateProgressFromX(float x, float trackLeft, float trackRight, boolean fromUser) {
        float ratio = (x - trackLeft) / (trackRight - trackLeft);
        ratio = Math.max(0, Math.min(1, ratio));
        int newProgress = Math.round(ratio * max);
        if (newProgress != progress) {
            progress = newProgress;
            if (listener != null) listener.onProgressChanged(this, progress, fromUser);
            invalidate();
        }
    }
}
