package com.waterpal.app.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.waterpal.app.R;
import com.waterpal.app.databinding.ActivityMainBinding;
import com.waterpal.app.ui.fragment.FriendsFragment;
import com.waterpal.app.ui.fragment.MessagesFragment;
import com.waterpal.app.ui.fragment.ProfileFragment;

/**
 * 主页（包含底部导航）
 */
public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setupNavigation();
        
        // 默认显示好友列表
        if (savedInstanceState == null) {
            loadFragment(new FriendsFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_friends);
        }
    }
    
    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_friends) {
                fragment = new FriendsFragment();
            } else if (itemId == R.id.nav_messages) {
                fragment = new MessagesFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }
            
            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
}
