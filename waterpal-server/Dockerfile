# WaterPal Backend Dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 复制 Maven 构建的 jar 包
COPY target/*.jar app.jar

# 复制 Firebase 配置文件（如果存在）
COPY firebase-service-account.json* ./

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
