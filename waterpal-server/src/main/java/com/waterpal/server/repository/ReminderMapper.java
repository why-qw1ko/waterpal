package com.waterpal.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.waterpal.server.entity.Reminder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 提醒消息 Mapper
 */
@Mapper
public interface ReminderMapper extends BaseMapper<Reminder> {
    
    @Select("SELECT r.*, s.nickname as sender_name, s.avatar_url as sender_avatar " +
            "FROM reminders r JOIN users s ON r.sender_id = s.id " +
            "WHERE r.receiver_id = #{userId} ORDER BY r.created_at DESC LIMIT 50")
    List<ReminderVO> getReceivedReminders(Long userId);
    
    @Select("SELECT r.*, s.nickname as receiver_name, s.avatar_url as receiver_avatar " +
            "FROM reminders r JOIN users s ON r.receiver_id = s.id " +
            "WHERE r.sender_id = #{userId} ORDER BY r.created_at DESC LIMIT 50")
    List<ReminderVO> getSentReminders(Long userId);
}
