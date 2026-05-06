package com.waterpal.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waterpal.app.databinding.ItemFriendBinding;
import com.waterpal.app.model.Friend;

import java.util.List;

/**
 * 好友列表适配器
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    
    private List<Friend> friendList;
    private OnSendReminderListener listener;
    
    public interface OnSendReminderListener {
        void onSendReminder(Friend friend);
    }
    
    public FriendAdapter(List<Friend> friendList, OnSendReminderListener listener) {
        this.friendList = friendList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.bind(friend);
    }
    
    @Override
    public int getItemCount() {
        return friendList.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemFriendBinding binding;
        
        ViewHolder(ItemFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        void bind(Friend friend) {
            binding.tvNickname.setText(friend.getNickname());
            
            // 加载头像（简化处理，实际项目用 Glide）
            if (friend.getAvatarUrl() != null && !friend.getAvatarUrl().isEmpty()) {
                // Glide.with(itemView.getContext()).load(friend.getAvatarUrl()).into(binding.ivAvatar);
            }
            
            binding.btnSendReminder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSendReminder(friend);
                }
            });
        }
    }
}
