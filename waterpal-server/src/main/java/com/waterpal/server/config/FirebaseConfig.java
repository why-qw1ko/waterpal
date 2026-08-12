package com.waterpal.server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Firebase Admin SDK 初始化配置
 */
@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.config-path}")
    private String configPath;

    @Value("${firebase.proxy-host:}")
    private String proxyHost;

    @Value("${firebase.proxy-port:0}")
    private int proxyPort;

    @PostConstruct
    public void init() {
        try {
            // 设置全局 HTTPS 代理（国内访问 Google 服务必需）
            if (proxyHost != null && !proxyHost.isBlank() && proxyPort > 0) {
                System.setProperty("https.proxyHost", proxyHost);
                System.setProperty("https.proxyPort", String.valueOf(proxyPort));
                log.info("Firebase 代理已设置: {}:{}", proxyHost, proxyPort);
            }

            GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ClassPathResource(configPath).getInputStream());
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase 初始化成功, configPath={}", configPath);
            }
        } catch (IOException e) {
            log.error("Firebase 初始化失败: {}", e.getMessage());
        }
    }
}
