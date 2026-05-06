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

import com.waterpal.app.databinding.FragmentFriendsBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.model.Friend;
import com.waterpal.app.model.SendReminderRequest;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.adapter.FriendAdapter;

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
    private ApiService apiService;
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
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
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
        apiService.getFriendList().enqueue(new Callback<ApiResponse<List<Friend>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Friend>>> call, Response<ApiResponse<List<Friend>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    friendList.clear();
                    List<Friend> data = response.body().getData();
                    if (data != null) {
                        friendList.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Friend>>> call, Throwable t) {
                // 加载失败时显示空列表
            }
        });
    }
    
    @Override
    public void onSendReminder(Friend friend) {
        // 发送喝水提醒
        SendReminderRequest request = new SendReminderRequest(friend.getFriendId(), "该喝水啦！💧");
        apiService.sendReminder(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), R.string.send_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.send_failed, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
