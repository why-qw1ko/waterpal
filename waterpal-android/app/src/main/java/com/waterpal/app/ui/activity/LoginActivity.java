package com.waterpal.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.waterpal.app.R;
import com.waterpal.app.databinding.ActivityLoginBinding;
import com.waterpal.app.model.ApiResponse;
import com.waterpal.app.model.LoginRequest;
import com.waterpal.app.model.LoginResponse;
import com.waterpal.app.network.ApiClient;
import com.waterpal.app.network.ApiService;
import com.waterpal.app.util.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录页
 */
public class LoginActivity extends AppCompatActivity {
    
    private ActivityLoginBinding binding;
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        apiService = ApiClient.getClient().create(ApiService.class);
        
        // 检查是否已登录
        String savedToken = PreferenceManager.getToken(this);
        long savedUserId = PreferenceManager.getUserId(this);
        if (savedToken != null && savedUserId != -1) {
            ApiClient.setAuthToken(savedToken, String.valueOf(savedUserId));
            navigateToMain();
            return;
        }
        
        setupViews();
    }
    
    private void setupViews() {
        // 获取验证码按钮（模拟）
        binding.btnGetCode.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            if (phone.length() != 11) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "验证码已发送：1234", Toast.LENGTH_LONG).show();
        });
        
        // 登录按钮
        binding.btnLogin.setOnClickListener(v -> {
            String phone = binding.etPhone.getText().toString().trim();
            String code = binding.etCode.getText().toString().trim();
            
            if (phone.length() != 11) {
                Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                return;
            }
            if (code.length() != 4) {
                Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            login(phone, code);
        });
    }
    
    private void login(String phone, String code) {
        binding.btnLogin.setEnabled(false);
        binding.btnLogin.setText(R.string.loading);
        
        LoginRequest request = new LoginRequest(phone, code);
        apiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(R.string.login);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    LoginResponse data = response.body().getData();
                    // 保存 token 和用户信息
                    PreferenceManager.saveToken(LoginActivity.this, data.getToken());
                    PreferenceManager.saveUserId(LoginActivity.this, data.getUserId());
                    PreferenceManager.saveNickname(LoginActivity.this, data.getNickname());
                    
                    // 设置全局网络请求 Token 和 UserId
                    ApiClient.setAuthToken(data.getToken(), String.valueOf(data.getUserId()));
                    
                    Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "登录失败";
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setText(R.string.login);
                Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
