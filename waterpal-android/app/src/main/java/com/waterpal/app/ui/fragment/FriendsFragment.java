package com.waterpal.app.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.waterpal.app.R;
import com.waterpal.app.databinding.DialogAddFriendBinding;
import com.waterpal.app.databinding.FragmentFriendsBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.model.Friend;
import com.waterpal.app.model.SendReminderRequest;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.activity.LoginActivity;
import com.waterpal.app.ui.adapter.FriendAdapter;
import com.waterpal.app.util.PreferenceManager;
import com.waterpal.app.util.ThemeManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends Fragment implements FriendAdapter.OnSendReminderListener, FriendAdapter.OnFriendLongClickListener {

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
        applyThemeToViews();
        loadFriends();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setThemeColor(ThemeManager.getPrimaryColor(getContext()));
            adapter.setShowAvatars(PreferenceManager.isShowAvatars(getContext()));
        }
        applyThemeToViews();
    }

    private void applyThemeToViews() {
        int color = ThemeManager.getPrimaryColor(getContext());
        binding.btnAddFriend.setBackground(ThemeManager.gradientButton(color));
        binding.btnAddFriendEmpty.setBackground(ThemeManager.gradientButton(color));
        binding.tvEmptyAvatar.setBackground(ThemeManager.avatarCircle(0xFFFFFFFF));

    }

    private void setupRecyclerView() {
        int color = ThemeManager.getPrimaryColor(getContext());
        adapter = new FriendAdapter(friendList, this);
        adapter.setThemeColor(color);
        adapter.setOnFriendLongClickListener(this);
        adapter.setShowAvatars(PreferenceManager.isShowAvatars(getContext()));
        binding.rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFriends.setAdapter(adapter);
    }

    private void setupViews() {
        binding.btnAddFriend.setOnClickListener(v -> showAddFriendDialog());
        binding.btnAddFriendEmpty.setOnClickListener(v -> showAddFriendDialog());
        binding.swipeRefresh.setOnRefreshListener(this::loadFriends);
    }

    private void showAddFriendDialog() {
        DialogAddFriendBinding dialogBinding = DialogAddFriendBinding.inflate(
            LayoutInflater.from(getContext()));
        int color = ThemeManager.getPrimaryColor(getContext());
        // 在 setView / create 之前应用主题色，避免默认颜色一闪而过
        dialogBinding.btnConfirmAdd.setBackground(ThemeManager.gradientButton(color));
        dialogBinding.btnCancel.setTextColor(color);
        dialogBinding.tilPhone.setBoxStrokeColor(color);
        dialogBinding.tilPhone.setHintTextColor(ColorStateList.valueOf(ThemeManager.alpha(color, 0.7f)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dialogBinding.tilPhone.setCursorColor(ColorStateList.valueOf(color));
        }
        dialogBinding.etFriendPhone.setHintTextColor(ThemeManager.alpha(color, 0.5f));
        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
            .setView(dialogBinding.getRoot())
            .create();
        dialogBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.btnConfirmAdd.setOnClickListener(v -> {
            String phone = dialogBinding.etFriendPhone.getText().toString().trim();
            if (phone.length() != 11) {
                Toast.makeText(getContext(), "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            dialog.dismiss();
            addFriend(phone);
        });
        dialog.show();
    }

    private void addFriend(String friendPhone) {
        ApiClient.getClient().create(ApiService.class)
            .addFriend(friendPhone)
            .enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (!isAdded()) return;
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(getContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
                        loadFriends();
                    } else {
                        String msg = extractErrorMessage(response);
                        Toast.makeText(getContext(), msg != null ? msg : "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String extractErrorMessage(Response<?> response) {
        try {
            if (response.body() instanceof ApiResponse) {
                ApiResponse<?> r = (ApiResponse<?>) response.body();
                if (r.getMessage() != null && !r.getMessage().isEmpty()) return r.getMessage();
            }
            if (response.errorBody() != null) {
                String json = response.errorBody().string();
                int s = json.indexOf("\"message\":\"");
                if (s != -1) { s += 11; int e = json.indexOf("\"", s); if (e != -1) return json.substring(s, e); }
            }
        } catch (Exception ignored) { }
        return null;
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
                    if (data != null) { friendList.addAll(data); adapter.notifyDataSetChanged(); }
                    updateEmptyState();
                } else if (response.code() == 401) {
                    handleAuthError();
                } else {
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Friend>>> call, Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAuthError() {
        Toast.makeText(getContext(), "会话已过期，请重新登录", Toast.LENGTH_SHORT).show();
        PreferenceManager.clear(getContext());
        ApiClient.setAuthToken(null);
        startActivity(new android.content.Intent(getContext(), LoginActivity.class)
            .setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void updateEmptyState() {
        boolean empty = friendList.isEmpty();
        binding.layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.swipeRefresh.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.tvFriendCount.setText(friendList.size() + " 位水友");
    }

    @Override
    public void onSendReminder(Friend friend) {
        SendReminderRequest req = new SendReminderRequest(friend.getFriendId(), "该喝水啦！💧");
        ApiClient.getClient().create(ApiService.class).sendReminder(req).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                    response.isSuccessful() && response.body() != null && response.body().isSuccess()
                        ? R.string.send_success : R.string.send_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFriendLongClick(Friend friend) {
        new MaterialAlertDialogBuilder(getContext())
            .setTitle("删除好友")
            .setMessage("确定要删除好友「" + friend.getNickname() + "」吗？\n删除后你们将不再互相关注。")
            .setPositiveButton("删除", (dialog, which) -> deleteFriend(friend))
            .setNegativeButton("取消", null)
            .show();
    }

    private void deleteFriend(Friend friend) {
        ApiClient.getClient().create(ApiService.class).deleteFriend(friend.getFriendId())
            .enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (!isAdded()) return;
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(getContext(), "已删除好友", Toast.LENGTH_SHORT).show();
                        loadFriends();
                    } else if (response.code() == 401) {
                        handleAuthError();
                    } else {
                        Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
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
