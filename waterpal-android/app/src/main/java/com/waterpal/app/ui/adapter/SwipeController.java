package com.waterpal.app.ui.adapter;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.waterpal.app.R;

/**
 * 基于 ItemTouchHelper 的左滑控制器。
 * 限距左滑（80dp），滑到位后自动触发操作回调。
 */
public class SwipeController extends ItemTouchHelper.SimpleCallback {

    public interface OnSwipeActionListener {
        void onSwipeAction(int position);
        String getSwipeLabel(int position);
    }

    private final int buttonWidthPx;
    private final OnSwipeActionListener listener;
    private int lastPosition = RecyclerView.NO_POSITION;

    public SwipeController(int buttonWidthDp, float density, OnSwipeActionListener listener) {
        super(0, ItemTouchHelper.LEFT);
        this.buttonWidthPx = (int) (buttonWidthDp * density);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView rv,
                          @NonNull RecyclerView.ViewHolder vh,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
        // onSwiped 时 adapterPosition 可能已失效，用提前保存的位置
        if (lastPosition == RecyclerView.NO_POSITION || listener == null) return;
        listener.onSwipeAction(lastPosition);
        lastPosition = RecyclerView.NO_POSITION;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView rv,
                            @NonNull RecyclerView.ViewHolder vh,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        float clampedDx = Math.max(dX, -buttonWidthPx);

        if (clampedDx < 0) {
            // 提前保存有效位置（此时 adapterPosition 一定有效）
            int pos = vh.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) lastPosition = pos;

            View itemView = vh.itemView;
            float right = itemView.getRight();
            float left = right + clampedDx;
            float top = itemView.getTop();
            float bottom = itemView.getBottom();

            Resources res = itemView.getResources();
            float density = res.getDisplayMetrics().density;
            float cornerRadius = 16 * density;

            // 圆角背景
            Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setColor(0xFF38B2AC);
            RectF rect = new RectF(left, top, right, bottom);
            c.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint);

            // 动态标签
            String label = listener != null && lastPosition != RecyclerView.NO_POSITION
                ? listener.getSwipeLabel(lastPosition) : "已读";

            // 白色对勾图标
            Drawable icon = ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_swipe_read);
            if (icon != null) {
                int iconSize = (int) (24 * density);
                int iconLeft = (int) (right - buttonWidthPx / 2 - iconSize / 2);
                int iconTop = (int) (top + (bottom - top) / 2 - iconSize - 8 * density);
                icon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
                icon.setTint(0xFFFFFFFF);
                icon.draw(c);
            }

            // 文字
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(0xFFFFFFFF);
            textPaint.setTextSize(13 * density);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setTextAlign(Paint.Align.CENTER);
            float textX = right - buttonWidthPx / 2f;
            float textY = top + (bottom - top) / 2f + 16 * density;
            c.drawText(label, textX, textY, textPaint);
        }

        super.onChildDraw(c, rv, vh, clampedDx, dY, actionState, isCurrentlyActive);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder vh) {
        float viewWidth = vh.itemView.getWidth();
        return viewWidth > 0 ? buttonWidthPx / viewWidth : 0.25f;
    }

    @Override
    public float getSwipeEscapeVelocity(@NonNull float defaultValue) {
        return defaultValue * 0.3f;
    }

    @Override
    public float getSwipeVelocityThreshold(@NonNull float defaultValue) {
        return defaultValue;
    }
}
