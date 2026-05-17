package com.waterpal.app.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.waterpal.app.R;
import com.waterpal.app.databinding.FragmentFriendsBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.model.Friend;
import com.waterpal.app.model.SendReminderRequest;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.adapter.FriendAdapter;
import com.waterpal.app.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 好友列表 Fragment
 */
public class FriendsFragment extends Fragment implements FriendAdapter.OnSendReminderListener {
    
    private FragmentFriendsBinding binding;
    private FriendAdapter adapter;
    private List<Friend> friendList = new ArrayList<>();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupRecyclerView();
        setupViews();
        loadFriends();
    }
    
    private void setupRecyclerView() {
        adapter = new FriendAdapter(friendList, this);
        binding.rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFriends.setAdapter(adapter);
    }
    
    private void setupViews() {
        binding.btnAddFriend.setOnClickListener(v -> showAddFriendDialog());
        binding.swipeRefresh.setOnRefreshListener(this::loadFriends);
    }
    
    private void showAddFriendDialog() {
        // 简化：直接输入手机号添加
        android.widget.EditText input = new android.widget.EditText(getContext());
        input.setHint(R.string.friend_hint);
        
        new AlertDialog.Builder(getContext())
            .setTitle(R.string.add_friend)
            .setView(input)
            .setPositiveButton(R.string.confirm, (dialog, which) -> {
                String phone = input.getText().toString().trim();
                // TODO: 实现添加好友逻辑
                Toast.makeText(getContext(), "添加好友功能开发中", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }
    
    private void loadFriends() {
        binding.swipeRefresh.setRefreshing(true);
        ApiClient.getClient().create(ApiService.class).getFriendList().enqueue(new Callback<ApiResponse<List<Friend>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Friend>>> call, Response<ApiResponse<List<Friend>>> response) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    friendList.clear();
                    List<Friend> data = response.body().getData();
                    if (data != null) {
                        friendList.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "会话已过期，请重新登录", Toast.LENGTH_SHORT).show();
                        // 清除本地信息并跳转登录
                        PreferenceManager.clear(getContext());
                        ApiClient.setAuthToken(null);
                        startActivity(new android.content.Intent(getContext(), com.waterpal.app.ui.activity.LoginActivity.class)
                            .setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        Toast.makeText(getContext(), "权限不足(403)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Friend>>> call, Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);
                // 加载失败时显示空列表
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    public void onSendReminder(Friend friend) {
        // 发送喝水提醒
        SendReminderRequest request = new SendReminderRequest(friend.getFriendId(), "该喝水啦！💧");
        ApiClient.getClient().create(ApiService.class).sendReminder(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), R.string.send_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.send_failed, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
