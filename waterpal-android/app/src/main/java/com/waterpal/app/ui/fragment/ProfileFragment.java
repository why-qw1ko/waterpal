package com.waterpal.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.waterpal.app.databinding.FragmentProfileBinding;
import com.waterpal.app.ui.activity.LoginActivity;
import com.waterpal.app.util.PreferenceManager;

/**
 * 个人中心 Fragment
 */
public class ProfileFragment extends Fragment {
    
    private FragmentProfileBinding binding;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 显示用户信息
        String nickname = PreferenceManager.getNickname(getContext());
        binding.tvNickname.setText(nickname != null ? nickname : "用户");
        
        // 退出登录
        binding.btnLogout.setOnClickListener(v -> {
            PreferenceManager.clear(getContext());
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}
