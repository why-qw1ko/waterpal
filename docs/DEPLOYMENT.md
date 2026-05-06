# WaterPal 部署指南

本文档提供详细的生产环境部署步骤。

---

## 📋 前置准备

### 服务器要求

| 配置 | 要求 |
|------|------|
| 操作系统 | Ubuntu 20.04+ / CentOS 7+ |
| CPU | 2 核心+ |
| 内存 | 2GB+ (推荐 4GB) |
| 磁盘 | 10GB+ |
| 网络 | 开放端口 8080、443 |

### 域名（可选）

- 如果有域名，可以配置 HTTPS
- 没有域名可直接用 IP 访问

---

## 🚀 方案一：Docker 部署（推荐）

### 步骤 1：安装 Docker

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER

# 验证安装
docker --version
docker compose version
```

### 步骤 2：克隆项目

```bash
git clone https://github.com/YOUR_USERNAME/waterpal.git
cd waterpal/waterpal-server
```

### 步骤 3：配置环境变量（可选）

创建 `.env` 文件：

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=your_secure_password

# JWT 配置
JWT_SECRET=your_jwt_secret_key_at_least_32_chars

# 服务器 IP（用于 Android 客户端）
SERVER_IP=121.4.98.92
```

### 步骤 4：配置 Firebase（可选）

如果需要推送功能：

```bash
# 复制示例文件
cp firebase-service-account.json.example firebase-service-account.json

# 编辑文件，填入你的 Firebase 服务账号密钥
# 获取方式：Firebase 控制台 → 项目设置 → 服务账号 → 生成新私钥
```

### 步骤 5：启动服务

```bash
docker compose up -d
```

### 步骤 6：验证部署

```bash
# 查看容器状态
docker compose ps

# 查看日志
docker compose logs -f backend

# 测试 API
curl http://localhost:8080/api/health

# 访问 Swagger UI
# http://<服务器IP>:8080/swagger-ui.html
```

### 步骤 7：配置防火墙

```bash
# Ubuntu (UFW)
sudo ufw allow 8080/tcp
sudo ufw allow 443/tcp  # HTTPS
sudo ufw deny 3306/tcp  # 禁止外部访问数据库
sudo ufw enable

# CentOS (FirewallD)
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=443/tcp
sudo firewall-cmd --reload
```

---

## 🔒 配置 HTTPS（推荐）

### 使用 Nginx 反向代理

#### 步骤 1：安装 Nginx

```bash
sudo apt-get install nginx  # Ubuntu/Debian
sudo yum install nginx      # CentOS
```

#### 步骤 2：配置 Nginx

创建 `/etc/nginx/sites-available/waterpal`：

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 或服务器 IP
    
    # 重定向到 HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    # SSL 证书
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    # 安全配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 步骤 3：启用配置

```bash
sudo ln -s /etc/nginx/sites-available/waterpal /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

#### 步骤 4：配置 Let's Encrypt 证书

```bash
sudo apt-get install certbot python3-certbot-nginx  # Ubuntu/Debian
sudo certbot --nginx -d your-domain.com
```

---

## 📱 Android 客户端配置

### 步骤 1：修改 API 地址

编辑 `waterpal-android/app/src/main/java/com/waterpal/app/network/ApiClient.java`：

```java
// 修改 BASE_URL 为你的服务器地址
private static final String BASE_URL = "https://your-domain.com/api/";
// 或使用 IP
// private static final String BASE_URL = "http://121.4.98.92:8080/api/";
```

### 步骤 2：配置 Firebase

1. 访问 [Firebase 控制台](https://console.firebase.google.com/)
2. 创建项目或选择现有项目
3. 添加 Android 应用，包名：`com.waterpal.app`
4. 下载 `google-services.json`
5. 放到 `waterpal-android/app/` 目录

### 步骤 3：打包 APK

```bash
# 在 Android Studio 中
# Build → Build Bundle(s) / APK(s) → Build APK(s)

# 或使用命令行
cd waterpal-android
./gradlew assembleDebug
```

APK 位置：`waterpal-android/app/build/outputs/apk/debug/app-debug.apk`

---

## 🔧 运维命令

### Docker 相关

```bash
# 查看状态
docker compose ps

# 查看日志
docker compose logs -f backend
docker compose logs -f mysql

# 重启服务
docker compose restart

# 停止服务
docker compose down

# 重新构建
docker compose up -d --build

# 进入容器
docker exec -it waterpal-backend sh
docker exec -it waterpal-mysql mysql -uroot -proot123
```

### 数据库备份

```bash
# 备份
docker exec waterpal-mysql mysqldump -uroot -proot123 waterpal > backup.sql

# 恢复
docker exec -i waterpal-mysql mysql -uroot -proot123 waterpal < backup.sql
```

### 日志管理

```bash
# 查看后端日志
docker compose logs backend

# 查看最近 100 行
docker compose logs --tail=100 backend

# 实时查看
docker compose logs -f backend
```

---

## ⚠️ 安全建议

### 1. 修改默认密码

编辑 `docker-compose.yml`：

```yaml
environment:
  MYSQL_ROOT_PASSWORD: your_secure_password  # 修改默认密码
```

### 2. 配置 JWT Secret

编辑 `application.yml` 或使用环境变量：

```yaml
jwt:
  secret: ${JWT_SECRET:your_very_secure_secret_key_at_least_32_chars}
```

### 3. 限制数据库访问

```bash
# 在 docker-compose.yml 中，注释掉 MySQL 的 ports 配置
# ports:
#   - "3306:3306"  # 生产环境不要暴露
```

### 4. 启用防火墙

```bash
sudo ufw allow 80/tcp   # HTTP (Let's Encrypt)
sudo ufw allow 443/tcp  # HTTPS
sudo ufw deny 8080/tcp  # 后端只允许 Nginx 访问
sudo ufw enable
```

### 5. 定期更新

```bash
# 拉取最新代码
git pull origin main

# 重新构建并重启
docker compose up -d --build
```

---

## 🐛 故障排查

### 后端无法启动

```bash
# 查看日志
docker compose logs backend

# 常见问题：
# 1. 端口被占用：修改 docker-compose.yml 中的端口
# 2. 数据库连接失败：检查 MySQL 是否启动
# 3. Firebase 配置错误：检查 firebase-service-account.json
```

### 数据库连接失败

```bash
# 进入 MySQL 容器
docker exec -it waterpal-mysql mysql -uroot -proot123

# 检查数据库
SHOW DATABASES;
USE waterpal;
SHOW TABLES;
```

### API 返回 500 错误

```bash
# 查看后端日志
docker compose logs backend | grep ERROR

# 常见原因：
# 1. 数据库表不存在：检查 schema.sql 是否执行
# 2. Firebase 配置缺失：复制示例文件
# 3. JWT 配置错误：检查 secret 配置
```

---

## 📞 技术支持

遇到问题？

1. 查看日志：`docker compose logs -f`
2. 检查配置：确认所有配置文件正确
3. 提交 Issue：https://github.com/YOUR_USERNAME/waterpal/issues

---

**部署愉快！💧**
