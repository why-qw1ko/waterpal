# WaterPal 水友 - 完整项目

> 社交喝水提醒应用 - 让朋友提醒你多喝水 💧

## 📂 项目结构

```
waterpal/
├── waterpal-server/     # 后端 (SpringBoot + MySQL)
├── waterpal-android/    # Android 客户端
├── docs/                # 文档
└── README.md           # 本文件
```

## 🚀 快速开始

### 后端部署

```bash
cd waterpal-server

# Docker 部署（推荐）
docker compose up -d

# 或本地开发
mvn spring-boot:run
```

访问：http://localhost:8080/swagger-ui.html

### Android 客户端

1. 用 Android Studio 打开 `waterpal-android/`
2. 配置 Firebase（google-services.json）
3. 修改 API 地址为后端地址
4. 运行到模拟器或真机

## 📖 详细文档

- [后端 README](./waterpal-server/README.md)
- API 文档：`/swagger-ui.html`
- 部署指南：见后端 README

## 🔐 测试账号

- 手机号：任意 11 位数字
- 验证码：`1234`

## 📱 功能特性

- ✅ 手机号登录/注册
- ✅ 好友列表
- ✅ 发送喝水提醒
- ✅ 消息记录
- ✅ Firebase 推送通知
- ✅ JWT 认证

## 🛠️ 技术栈

**后端：** SpringBoot 3.2 + MySQL 8.0 + MyBatis-Plus + JWT + Firebase

**客户端：** Android (Java 17) + Retrofit + Material Design 3 + MVVM

## 📄 License

MIT

---

**让喝水变得更有趣！💧**
