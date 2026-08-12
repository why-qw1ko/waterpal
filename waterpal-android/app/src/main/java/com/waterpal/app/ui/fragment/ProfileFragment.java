package com.waterpal.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.waterpal.app.R;
import com.waterpal.app.databinding.DialogEditNicknameBinding;
import com.waterpal.app.databinding.FragmentProfileBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.activity.LoginActivity;
import com.waterpal.app.ui.activity.SettingsActivity;
import com.waterpal.app.util.PreferenceManager;
import com.waterpal.app.util.ThemeManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding b;
    private int dailyGoal = 8;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLocal();
        loadServer();
        setupViews();
        applyTheme();
    }

    @Override public void onResume() { super.onResume(); applyTheme(); }

    private void applyTheme() {
        int c = ThemeManager.getPrimaryColor(getContext());
        b.btnGoalMinus.setBackground(ThemeManager.outlineCircle(c));
        b.btnGoalMinus.setTextColor(c);
        b.btnGoalPlus.setBackground(ThemeManager.gradientButton(c));
        b.tvDailyGoal.setTextColor(c);
        // 头像背景跟随主题色
        b.viewProfileAvatarBg.setBackground(ThemeManager.avatarCircle(c));
    }

    private void loadLocal() {
        String nick = PreferenceManager.getNickname(getContext());
        if (nick != null && !nick.isEmpty()) {
            b.tvNickname.setText(nick);
            b.tvAvatarText.setText(nick.substring(0, 1));
        } else {
            b.tvNickname.setText("用户");
            b.tvAvatarText.setText("💧");
        }
        // 显示手机号
        String phone = PreferenceManager.getPhone(getContext());
        b.tvPhone.setText(phone != null ? phone : "");
        dailyGoal = PreferenceManager.getDailyGoal(getContext());
        updateGoal();
    }

    private void loadServer() {
        ApiClient.getClient().create(ApiService.class).getUserProfile()
            .enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                @Override
                public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> r) {
                    if (!isAdded()) return;
                    if (r.isSuccessful() && r.body() != null && r.body().isSuccess()) {
                        Map<String, Object> d = r.body().getData();
                        if (d != null) {
                            String nick = (String) d.get("nickname");
                            if (nick != null && !nick.isEmpty()) {
                                b.tvNickname.setText(nick);
                                b.tvAvatarText.setText(nick.substring(0, 1));
                                PreferenceManager.saveNickname(getContext(), nick);
                            }
                            Object g = d.get("dailyGoal");
                            if (g instanceof Number) {
                                dailyGoal = ((Number) g).intValue();
                                PreferenceManager.saveDailyGoal(getContext(), dailyGoal);
                                updateGoal();
                            }
                        }
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) { }
            });
    }

    private void setupViews() {
        b.btnEditNickname.setOnClickListener(v -> showNickDialog());
        b.tvNickname.setOnClickListener(v -> showNickDialog());
        b.btnGoalMinus.setOnClickListener(v -> { if (dailyGoal > 1) { dailyGoal--; updateGoal(); saveGoal(); } });
        b.btnGoalPlus.setOnClickListener(v -> { if (dailyGoal < 20) { dailyGoal++; updateGoal(); saveGoal(); } });
        b.btnSettings.setOnClickListener(v -> startActivity(new Intent(getContext(), SettingsActivity.class)));
        b.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void updateGoal() {
        b.tvDailyGoal.setText(dailyGoal + " 杯/天");
        b.tvGoalNumber.setText(String.valueOf(dailyGoal));
    }

    private void saveGoal() {
        PreferenceManager.saveDailyGoal(getContext(), dailyGoal);
        Toast.makeText(getContext(), R.string.goal_saved, Toast.LENGTH_SHORT).show();
        Map<String, Object> u = new HashMap<>();
        u.put("dailyGoal", dailyGoal);
        ApiClient.getClient().create(ApiService.class).updateUserProfile(u)
            .enqueue(new Callback<ApiResponse<Void>>() {
                @Override public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> r) { }
                @Override public void onFailure(Call<ApiResponse<Void>> c, Throwable t) { }
            });
    }

    private void showNickDialog() {
        DialogEditNicknameBinding d = DialogEditNicknameBinding.inflate(LayoutInflater.from(getContext()));

        // 设置弹窗头像为当前主题色
        int themeColor = ThemeManager.getPrimaryColor(getContext());
        d.viewDialogAvatarBg.setBackground(ThemeManager.avatarCircle(themeColor));

        String cur = b.tvNickname.getText().toString();
        d.etNicknameInput.setText(cur);
        d.etNicknameInput.setSelection(cur.length());

        if (cur != null && !cur.isEmpty() && !cur.equals("用户")) d.tvDialogAvatar.setText(cur.substring(0, 1));
        d.etNicknameInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int bf, int c) {
                d.tvDialogAvatar.setText(s.length() > 0 ? s.subSequence(0, 1) : "💧");
            }
            @Override public void afterTextChanged(android.text.Editable e) { }
        });

        new MaterialAlertDialogBuilder(getContext())
            .setTitle(R.string.edit_nickname)
            .setView(d.getRoot())
            .setPositiveButton(R.string.confirm, (dia, which) -> {
                String nn = d.etNicknameInput.getText().toString().trim();
                if (!nn.isEmpty()) {
                    b.tvNickname.setText(nn);
                    b.tvAvatarText.setText(nn.substring(0, 1));
                    PreferenceManager.saveNickname(getContext(), nn);
                    Toast.makeText(getContext(), R.string.nickname_saved, Toast.LENGTH_SHORT).show();
                    Map<String, Object> u = new HashMap<>();
                    u.put("nickname", nn);
                    ApiClient.getClient().create(ApiService.class).updateUserProfile(u)
                        .enqueue(new Callback<ApiResponse<Void>>() {
                            @Override public void onResponse(Call<ApiResponse<Void>> c, Response<ApiResponse<Void>> r) { }
                            @Override public void onFailure(Call<ApiResponse<Void>> c, Throwable t) { }
                        });
                }
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(getContext())
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？退出后需要重新登录才能使用。")
            .setPositiveButton("退出", (dia, which) -> {
                PreferenceManager.clear(getContext());
                ApiClient.setAuthToken(null);
                Intent i = new Intent(getContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                if (getActivity() != null) getActivity().finish();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    @Override public void onDestroyView() { super.onDestroyView(); b = null; }
}
