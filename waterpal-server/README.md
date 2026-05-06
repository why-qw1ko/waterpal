# 💧 WaterPal 水友 - 社交喝水提醒应用

一款基于社交关系的喝水提醒应用，用户可以添加好友并互相发送喝水提醒通知。

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![Android](https://img.shields.io/badge/Android-7.0+-green)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)

---

## 📱 功能特性

### 已实现
- ✅ 手机号登录/注册（模拟验证码：1234）
- ✅ 好友列表展示
- ✅ 发送喝水提醒
- ✅ 消息记录页面
- ✅ 个人中心
- ✅ Firebase 推送通知
- ✅ JWT 认证

### 计划中
- ⏳ 添加好友功能
- ⏳ 消息已读状态
- ⏳ 每日喝水统计
- ⏳ 个性化提醒设置
- ⏳ 国内厂商推送适配

---

## 🏗️ 技术架构

### 后端
| 组件 | 技术 |
|------|------|
| 框架 | SpringBoot 3.2+ |
| 数据库 | MySQL 8.0 |
| ORM | MyBatis-Plus |
| 认证 | JWT |
| 推送 | Firebase Admin SDK |
| API 文档 | Swagger/OpenAPI |

### Android 客户端
| 组件 | 技术 |
|------|------|
| 语言 | Java 17 |
| 最低版本 | Android 7.0 (API 24) |
| 网络 | Retrofit + OkHttp |
| 推送 | Firebase Messaging |
| UI | Material Design 3 |
| 架构 | MVVM |

---

## 🚀 快速开始

### 方式一：Docker 部署（推荐）

**一键启动所有服务：**

```bash
# 1. 克隆项目
git clone https://github.com/YOUR_USERNAME/waterpal.git
cd waterpal/waterpal-server

# 2. 配置 Firebase（可选，用于推送功能）
# 复制示例文件并填入你的 Firebase 服务账号密钥
cp firebase-service-account.json.example firebase-service-account.json

# 3. 启动 Docker 容器
docker compose up -d

# 4. 查看日志
docker compose logs -f backend

# 5. 访问 API 文档
# http://localhost:8080/swagger-ui.html
```

**服务地址：**
- 后端 API：`http://localhost:8080`
- API 文档：`http://localhost:8080/swagger-ui.html`
- MySQL：`localhost:3306`

### 方式二：本地开发

#### 前置要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Android Studio（用于客户端开发）

#### 后端启动

```bash
# 1. 创建数据库
mysql -u root -p < schema.sql

# 2. 配置 Firebase（可选）
cp firebase-service-account.json.example firebase-service-account.json

# 3. 修改数据库配置
# 编辑 src/main/resources/application.yml

# 4. 启动后端
mvn spring-boot:run
```

#### Android 客户端

```bash
# 1. 用 Android Studio 打开 waterpal-android 目录

# 2. 配置 Firebase
# 在 Firebase 控制台创建项目，添加 Android 应用
# 下载 google-services.json 到 app/ 目录

# 3. 修改 API 地址
# 编辑 ApiClient.java 中的 BASE_URL
# 模拟器：http://10.0.2.2:8080/api/
# 真机：http://<你的电脑IP>:8080/api/

# 4. 运行到模拟器或真机
```

---

## 📦 生产环境部署

### 服务器要求
- Ubuntu 20.04+ / CentOS 7+
- 2GB+ RAM
- Docker + Docker Compose
- 开放端口：8080（后端）、3306（数据库，可选）

### 部署步骤

```bash
# 1. 安装 Docker（Ubuntu/Debian）
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 2. 克隆项目
git clone https://github.com/YOUR_USERNAME/waterpal.git
cd waterpal/waterpal-server

# 3. 修改配置（可选）
# 编辑 docker-compose.yml 中的密码、端口等

# 4. 一键部署
chmod +x deploy.sh
./deploy.sh

# 5. 验证部署
curl http://<服务器IP>:8080/api/health
```

### 安全建议

1. **修改默认密码**
   - MySQL root 密码（docker-compose.yml）
   - JWT secret（application.yml）

2. **启用 HTTPS**
   - 使用 Nginx 反向代理
   - 配置 Let's Encrypt 证书

3. **防火墙配置**
   ```bash
   # 只开放必要端口
   ufw allow 8080/tcp
   ufw allow 443/tcp  # HTTPS
   ufw deny 3306/tcp  # 禁止外部访问数据库
   ```

4. **Firebase 配置**
   - 生产环境使用真实的 Firebase 服务账号
   - 不要将密钥提交到 Git

---

## 🔌 API 接口

| 接口 | 方法 | 说明 | 认证 |
|------|------|------|------|
| `/api/auth/login` | POST | 登录 | ❌ |
| `/api/auth/fcm-token` | POST | 上报 FCM Token | ✅ |
| `/api/friends/list` | GET | 获取好友列表 | ✅ |
| `/api/friends/add` | POST | 添加好友 | ✅ |
| `/api/reminders/send` | POST | 发送提醒 | ✅ |
| `/api/reminders/received` | GET | 收到的提醒 | ✅ |
| `/api/reminders/sent` | GET | 发送的提醒 | ✅ |

**测试账号：** 使用任意 11 位手机号登录，验证码固定为：`1234`

---

## 📁 项目结构

```
waterpal/
├── waterpal-server/          # 后端 SpringBoot 项目
│   ├── src/main/java/
│   │   └── com.waterpal.server/
│   │       ├── config/       # 配置类
│   │       ├── controller/   # REST API 控制器
│   │       ├── service/      # 业务逻辑
│   │       ├── repository/   # 数据访问
│   │       ├── entity/       # 实体类
│   │       ├── dto/          # 数据传输对象
│   │       ├── filter/       # 过滤器
│   │       └── util/         # 工具类
│   ├── src/main/resources/
│   │   ├── application.yml   # 配置文件
│   │   └── schema.sql        # 数据库初始化脚本
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── deploy.sh
│   └── pom.xml
│
└── waterpal-android/         # Android 客户端项目
    ├── app/
    │   ├── src/main/
    │   │   ├── java/com.waterpal.app/
    │   │   │   ├── ui/       # UI 页面
    │   │   │   ├── network/  # 网络层
    │   │   │   ├── model/    # 数据模型
    │   │   │   ├── service/  # 服务
    │   │   │   └── util/     # 工具类
    │   │   ├── res/          # 资源文件
    │   │   └── AndroidManifest.xml
    │   └── build.gradle
    └── build.gradle
```

---

## 🔧 配置说明

### Firebase 配置

1. 访问 [Firebase 控制台](https://console.firebase.google.com/)
2. 创建新项目
3. **后端推送配置：**
   - 项目设置 → 服务账号 → 生成新私钥
   - 下载 JSON 文件，重命名为 `firebase-service-account.json`
   - 放到 `waterpal-server/` 目录
4. **Android 推送配置：**
   - 添加 Android 应用，包名：`com.waterpal.app`
   - 下载 `google-services.json`
   - 放到 `waterpal-android/app/` 目录

### 数据库配置

默认配置：
```yaml
url: jdbc:mysql://localhost:3306/waterpal
username: root
password: root123
```

Docker 部署时会自动创建数据库和表结构。

---

## 🧪 测试

### API 测试

```bash
# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","code":"1234"}'

# 获取好友列表（需要 token）
curl http://localhost:8080/api/friends/list \
  -H "Authorization: Bearer <your-token>"
```

### 使用 Swagger UI

访问 `http://localhost:8080/swagger-ui.html` 进行交互式测试。

---

## 📝 常见问题

### 1. Docker 容器启动失败

```bash
# 查看日志
docker compose logs backend
docker compose logs mysql

# 重启服务
docker compose restart
```

### 2. Android 无法连接后端

- 模拟器使用 `10.0.2.2` 代替 `localhost`
- 真机确保手机和电脑在同一网络
- 检查防火墙设置

### 3. Firebase 推送不工作

- 检查 `firebase-service-account.json` 是否正确
- 确认 Firebase 项目已启用 Cloud Messaging
- Android 客户端需要正确的 `google-services.json`

---

## 📄 License

MIT License

---

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📧 联系方式

如有问题，请提交 Issue 或联系开发者。

---

**Made with ❤️ by WaterPal Team**
