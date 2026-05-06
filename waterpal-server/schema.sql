-- WaterPal 数据库初始化脚本

CREATE DATABASE IF NOT EXISTS waterpal DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE waterpal;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    phone VARCHAR(11) UNIQUE NOT NULL COMMENT '手机号',
    nickname VARCHAR(50) DEFAULT '' COMMENT '昵称',
    avatar_url VARCHAR(255) DEFAULT '' COMMENT '头像 URL',
    fcm_token VARCHAR(255) DEFAULT '' COMMENT 'Firebase 推送 Token',
    daily_goal INT DEFAULT 8 COMMENT '每日喝水目标（杯）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-否 1-是',
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 好友关系表
CREATE TABLE IF NOT EXISTS friends (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    friend_id BIGINT NOT NULL COMMENT '好友 ID',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-拉黑',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_friend (user_id, friend_id),
    INDEX idx_user_id (user_id),
    INDEX idx_friend_id (friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';

-- 提醒消息表
CREATE TABLE IF NOT EXISTS reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    sender_id BIGINT NOT NULL COMMENT '发送者 ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者 ID',
    message VARCHAR(200) DEFAULT '' COMMENT '提醒消息',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读 0-否 1-是',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_receiver (receiver_id),
    INDEX idx_sender (sender_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒消息表';

-- 插入测试数据
INSERT INTO users (phone, nickname, daily_goal) VALUES 
('13800138001', '小明', 8),
('13800138002', '小红', 10),
('13800138003', '小刚', 6);

-- 添加测试好友关系
INSERT INTO friends (user_id, friend_id, status) VALUES 
(1, 2, 1), (2, 1, 1),
(1, 3, 1), (3, 1, 1);

-- 添加测试提醒
INSERT INTO reminders (sender_id, receiver_id, message, is_read) VALUES 
(2, 1, '记得喝水哦！', 0),
(3, 1, '该喝水啦💧', 1);
