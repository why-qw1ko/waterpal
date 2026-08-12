package com.waterpal.app.ui.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waterpal.app.R;
import com.waterpal.app.databinding.ItemReminderBinding;
import com.waterpal.app.model.Reminder;
import com.waterpal.app.util.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.VH> {

    private final List<Reminder> reminders;
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    private OnReminderClickListener clickListener;
    private int themeColor = ThemeManager.DEFAULT_PRIMARY;

    public interface OnReminderClickListener {
        void onReminderClick(Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminders) { this.reminders = reminders; }
    public void setOnReminderClickListener(OnReminderClickListener l) { this.clickListener = l; }
    public void setThemeColor(int color) { this.themeColor = color; notifyDataSetChanged(); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderBinding b = ItemReminderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        h.bind(reminders.get(pos), pos);
    }

    @Override
    public int getItemCount() { return reminders.size(); }

    class VH extends RecyclerView.ViewHolder {
        private final ItemReminderBinding b;

        VH(ItemReminderBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(Reminder r, int pos) {
            String name = r.getSenderName();
            b.tvSenderName.setText(name != null && !name.isEmpty()
                ? name + " 提醒你喝水" : "好友提醒你喝水");
            b.tvAvatarText.setText(name != null && !name.isEmpty() ? name.substring(0, 1) : "💧");
            b.tvMessage.setText(r.getMessage() != null ? r.getMessage() : "该喝水啦！💧");

            b.viewAvatarBg.setBackground(ThemeManager.avatarCircle(themeColor));

            Date ct = r.getCreatedAt();
            b.tvTime.setText(ct != null ? formatTime(ct) : "");

            boolean unread = r.getIsRead() != null && r.getIsRead() == 0;
            b.vUnreadDot.setVisibility(unread ? View.VISIBLE : View.GONE);
            if (unread) {
                GradientDrawable dot = new GradientDrawable();
                dot.setShape(GradientDrawable.OVAL);
                dot.setColor(themeColor);
                dot.setSize(dp(10), dp(10));
                b.vUnreadDot.setBackground(dot);
                // elevation 必须高于卡片（1dp），否则被卡片盖住
                b.vUnreadDot.setElevation(dp(2));
            } else {
                b.vUnreadDot.setElevation(0);
            }

            // 清掉绿色背景（已由 ItemTouchHelper.onChildDraw 绘制），保留 View 维持高度
            b.layoutSwipeBg.setBackgroundColor(0x00000000);

            // 前景卡片点击 → 详情弹窗
            b.cardForeground.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReminderClick(r);
                }
            });
        }

        private String formatTime(Date d) {
            return new Date().getTime() - d.getTime() < 86400000L ? timeFmt.format(d) : dateFmt.format(d);
        }

        private int dp(int value) {
            return (int) (value * itemView.getResources().getDisplayMetrics().density);
        }
    }
}
