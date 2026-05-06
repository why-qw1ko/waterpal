#!/bin/bash
# WaterPal 一键部署脚本

set -e

echo "🚀 WaterPal 一键部署脚本"
echo "========================"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose
if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose 未安装"
    exit 1
fi

echo "✅ Docker 环境检查通过"

# 构建后端
echo "📦 构建后端..."
cd /home/admin/openclaw/workspace/waterpal-server
mvn clean package -DskipTests

# 复制配置文件
echo "📋 复制配置文件..."
cp application-prod.yml src/main/resources/application-prod.yml 2>/dev/null || true

# 启动 Docker 容器
echo "🐳 启动 Docker 容器..."
docker compose up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 15

# 检查服务状态
echo "🔍 检查服务状态..."
docker compose ps

echo ""
echo "✅ 部署完成！"
echo "========================"
echo "📡 后端地址：http://121.4.98.92:8080"
echo "📖 API 文档：http://121.4.98.92:8080/swagger-ui.html"
echo "🔧 查看日志：docker compose logs -f backend"
echo "🛑 停止服务：docker compose down"
echo ""
