package com.waterpal.app.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.waterpal.app.R;
import com.waterpal.app.databinding.ActivityMainBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.ui.fragment.FriendsFragment;
import com.waterpal.app.ui.fragment.MessagesFragment;
import com.waterpal.app.ui.fragment.ProfileFragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.waterpal.app.util.PreferenceManager;
import com.waterpal.app.util.ThemeManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主页（ViewPager2 + 底部导航） — 支持左右滑动切换 Tab
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applyThemeColors();
        setupViewPager();
        requestNotificationPermission();
        uploadFcmToken();

        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_friends);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyThemeColors();
    }

    private void applyThemeColors() {
        ThemeManager.applyToActivity(this, binding.toolbar, binding.bottomNavigation);
    }

    private void setupViewPager() {
        // ViewPager2 适配器
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        // 预加载左右各一页，保持流畅
        binding.viewPager.setOffscreenPageLimit(2);
        // 禁用 ViewPager2 滑动，避免与消息左滑冲突，切换仅通过底部 Tab 点击
        binding.viewPager.setUserInputEnabled(false);

        // ViewPager2 页面切换 → 同步底部导航选中
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int itemId;
                if (position == 0) itemId = R.id.nav_friends;
                else if (position == 1) itemId = R.id.nav_messages;
                else itemId = R.id.nav_profile;
                binding.bottomNavigation.setSelectedItemId(itemId);
            }
        });

        // 底部导航点击 → 切换 ViewPager2 页面
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_friends) {
                binding.viewPager.setCurrentItem(0, false);
                return true;
            } else if (itemId == R.id.nav_messages) {
                binding.viewPager.setCurrentItem(1, false);
                return true;
            } else if (itemId == R.id.nav_profile) {
                binding.viewPager.setCurrentItem(2, false);
                return true;
            }
            return false;
        });
    }

    /**
     * Android 13+ 必须动态请求通知权限，否则通知不会显示
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    /**
     * 每次进入主页时上报 FCM Token，确保服务端有最新 token
     */
    private void uploadFcmToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("MainActivity", "获取 FCM Token 失败");
                    return;
                }
                String token = task.getResult();
                String userId = String.valueOf(PreferenceManager.getUserId(getApplicationContext()));
                Log.d("MainActivity", "上报 FCM Token: " + token.substring(0, Math.min(20, token.length())) + "...");
                java.util.Map<String, String> body = java.util.Collections.singletonMap("fcmToken", token);
                ApiClient.getClient().create(ApiService.class)
                    .updateFcmToken(userId, body)
                    .enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            Log.d("MainActivity", "FCM Token 上传: " + (response.isSuccessful() ? "成功" : "失败 " + response.code()));
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Log.e("MainActivity", "FCM Token 上传异常: " + t.getMessage());
                        }
                    });
            });
    }

    /**
     * ViewPager2 页面适配器（3 个 Tab：好友 / 消息 / 我的）
     */
    private static class MainPagerAdapter extends FragmentStateAdapter {

        MainPagerAdapter(@NonNull MainActivity activity) {
            super(activity);
        }

        @NonNull @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new FriendsFragment();
                case 1: return new MessagesFragment();
                case 2: return new ProfileFragment();
                default: return new FriendsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
