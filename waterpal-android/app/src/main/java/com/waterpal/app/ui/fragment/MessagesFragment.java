package com.waterpal.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.waterpal.app.R;
import com.waterpal.app.databinding.DialogReminderDetailBinding;
import com.waterpal.app.databinding.FragmentMessagesBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.model.Reminder;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.adapter.ReminderAdapter;
import com.waterpal.app.ui.adapter.SwipeController;
import com.waterpal.app.util.PreferenceManager;
import com.waterpal.app.util.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesFragment extends Fragment implements
        ReminderAdapter.OnReminderClickListener, SwipeController.OnSwipeActionListener {

    private FragmentMessagesBinding b;
    private ReminderAdapter adapter;
    private final List<Reminder> reminders = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentMessagesBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        b.swipeRefresh.setOnRefreshListener(this::loadReminders);
        applyThemeToViews();
        loadReminders();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setThemeColor(ThemeManager.getPrimaryColor(getContext()));
        }
        applyThemeToViews();
    }

    private void applyThemeToViews() {
        int color = ThemeManager.getPrimaryColor(getContext());
        b.tvEmptyAvatar.setBackground(ThemeManager.avatarCircle(color));
    }

    private void setupRecyclerView() {
        adapter = new ReminderAdapter(reminders);
        adapter.setOnReminderClickListener(this);
        adapter.setThemeColor(ThemeManager.getPrimaryColor(getContext()));

        b.rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        b.rvMessages.setAdapter(adapter);

        // ItemTouchHelper 左滑 —— 80dp 限距，滑到位自动触发已读回调
        SwipeController swipeController = new SwipeController(80,
            getResources().getDisplayMetrics().density, this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(b.rvMessages);
    }

    private void loadReminders() {
        b.swipeRefresh.setRefreshing(true);
        ApiClient.getClient().create(ApiService.class).getReceivedReminders()
            .enqueue(new Callback<ApiResponse<List<Reminder>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Reminder>>> call, Response<ApiResponse<List<Reminder>>> r) {
                    if (!isAdded()) return;
                    b.swipeRefresh.setRefreshing(false);
                    if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                        reminders.clear();
                        List<Reminder> data = r.body().getData();
                        if (data != null) { reminders.addAll(data); adapter.notifyDataSetChanged(); }
                        updateEmpty();
                    } else if (r.code() == 401) {
                        logout();
                    } else {
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<List<Reminder>>> call, Throwable t) {
                    if (!isAdded()) return;
                    b.swipeRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updateEmpty() {
        boolean empty = reminders.isEmpty();
        b.layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        b.swipeRefresh.setVisibility(empty ? View.GONE : View.VISIBLE);
        b.tvMsgCount.setText(reminders.size() + " 条消息");
    }

    // ===== 点击 → 详情弹窗（未读则标记已读） =====
    @Override
    public void onReminderClick(Reminder r) {
        DialogReminderDetailBinding d = DialogReminderDetailBinding.inflate(LayoutInflater.from(getContext()));

        int themeColor = ThemeManager.getPrimaryColor(getContext());
        d.viewDetailAvatarBg.setBackground(ThemeManager.avatarCircle(themeColor));

        String name = r.getSenderName();
        d.tvDetailAvatar.setText(name != null && !name.isEmpty() ? name.substring(0, 1) : "💧");
        d.tvDetailSender.setText(name != null ? name : "好友");
        Date ct = r.getCreatedAt();
        if (ct != null) d.tvDetailTime.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(ct));
        d.tvDetailMessage.setText(r.getMessage() != null ? r.getMessage() : "该喝水啦！💧");

        new MaterialAlertDialogBuilder(getContext())
            .setView(d.getRoot())
            .setPositiveButton("知道了", null)
            .show();

        // 未读 → 调接口标记已读 → 重新拉列表
        if (r.getIsRead() != null && r.getIsRead() == 0) {
            ApiClient.getClient().create(ApiService.class).markReminderRead(r.getId())
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> resp) {
                        if (isAdded()) loadReminders();
                    }
                    @Override public void onFailure(Call<ApiResponse<Void>> c, Throwable t) { }
                });
        }
    }

    // ===== 左滑到位 → 确认弹窗（已读⇄未读 切换） =====
    @Override
    public void onSwipeAction(int pos) {
        if (pos < 0 || pos >= reminders.size()) return;
        Reminder r = reminders.get(pos);
        boolean wasRead = r.getIsRead() != null && r.getIsRead() == 1;

        String title = wasRead ? "标记未读" : "标记已读";
        String msg = wasRead ? "确定将这条提醒标记为未读吗？" : "确定将这条提醒标记为已读吗？";

        new MaterialAlertDialogBuilder(getContext())
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton("确定", (dia, which) -> {
                // 调接口 → 成功后重新拉列表
                retrofit2.Call<ApiResponse<Void>> call = wasRead
                    ? ApiClient.getClient().create(ApiService.class).markReminderUnread(r.getId())
                    : ApiClient.getClient().create(ApiService.class).markReminderRead(r.getId());
                call.enqueue(new Callback<ApiResponse<Void>>() {
                    @Override public void onResponse(retrofit2.Call<ApiResponse<Void>> c, retrofit2.Response<ApiResponse<Void>> resp) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), wasRead ? "已标记为未读" : "已标记为已读", Toast.LENGTH_SHORT).show();
                            loadReminders();
                        }
                    }
                    @Override public void onFailure(retrofit2.Call<ApiResponse<Void>> c, Throwable t) {
                        if (isAdded()) Toast.makeText(getContext(), "操作失败", Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("取消", null)
            .setOnCancelListener(null)
            .show();
    }

    @Override
    public String getSwipeLabel(int pos) {
        if (pos < 0 || pos >= reminders.size()) return "已读";
        Reminder r = reminders.get(pos);
        return (r.getIsRead() != null && r.getIsRead() == 1) ? "未读" : "已读";
    }

    private void logout() {
        Toast.makeText(getContext(), "会话已过期", Toast.LENGTH_SHORT).show();
        PreferenceManager.clear(getContext());
        ApiClient.setAuthToken(null);
        startActivity(new android.content.Intent(getContext(), com.waterpal.app.ui.activity.LoginActivity.class)
            .setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override public void onDestroyView() { super.onDestroyView(); b = null; }
}
