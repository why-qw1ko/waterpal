package com.waterpal.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.waterpal.app.databinding.ItemFriendBinding;
import com.waterpal.app.model.Friend;
import com.waterpal.app.util.ThemeManager;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<Friend> friendList;
    private final OnSendReminderListener sendListener;
    private OnFriendLongClickListener longClickListener;
    private boolean showAvatars = true;
    private int themeColor;

    public interface OnSendReminderListener {
        void onSendReminder(Friend friend);
    }

    public interface OnFriendLongClickListener {
        void onFriendLongClick(Friend friend);
    }

    public FriendAdapter(List<Friend> friendList, OnSendReminderListener sendListener) {
        this.friendList = friendList;
        this.sendListener = sendListener;
        this.themeColor = ThemeManager.DEFAULT_PRIMARY;
    }

    public void setOnFriendLongClickListener(OnFriendLongClickListener listener) { this.longClickListener = listener; }
    public void setShowAvatars(boolean show) { this.showAvatars = show; notifyDataSetChanged(); }
    public void setThemeColor(int color) { this.themeColor = color; notifyDataSetChanged(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(friendList.get(position));
    }

    @Override
    public int getItemCount() { return friendList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFriendBinding binding;

        ViewHolder(ItemFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Friend friend) {
            String nickname = friend.getNickname();
            binding.tvNickname.setText(nickname);

            // 头像背景使用主题色
            binding.viewAvatarBg.setBackground(ThemeManager.avatarCircle(themeColor));
            binding.btnSendReminder.setBackground(ThemeManager.avatarCircle(themeColor));

            String avatarUrl = friend.getAvatarUrl();
            boolean hasAvatar = showAvatars && avatarUrl != null && !avatarUrl.isEmpty();

            if (hasAvatar) {
                binding.ivAvatar.setVisibility(android.view.View.VISIBLE);
                binding.viewAvatarBg.setVisibility(android.view.View.GONE);
                binding.tvAvatarText.setVisibility(android.view.View.GONE);
                Glide.with(binding.ivAvatar.getContext())
                    .load(avatarUrl)
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(binding.ivAvatar);
            } else {
                binding.ivAvatar.setVisibility(android.view.View.GONE);
                binding.viewAvatarBg.setVisibility(android.view.View.VISIBLE);
                binding.tvAvatarText.setVisibility(android.view.View.VISIBLE);
                binding.tvAvatarText.setText(nickname != null && !nickname.isEmpty() ? nickname.substring(0, 1) : "💧");
            }

            binding.tvStatus.setText("💙 水友");
            binding.btnSendReminder.setOnClickListener(v -> {
                if (sendListener != null) sendListener.onSendReminder(friend);
            });
            binding.getRoot().setOnLongClickListener(v -> {
                if (longClickListener != null) longClickListener.onFriendLongClick(friend);
                return true;
            });
        }
    }
}
