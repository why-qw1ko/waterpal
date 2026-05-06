package com.waterpal.server.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.waterpal.server.entity.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 好友 Mapper
 */
@Mapper
public interface FriendMapper extends BaseMapper<Friend> {
    
    @Select("SELECT f.*, u.nickname, u.avatar_url FROM friends f " +
            "JOIN users u ON f.friend_id = u.id " +
            "WHERE f.user_id = #{userId} AND f.status = 1")
    List<FriendVO> getFriendList(Long userId);
    
    @Select("SELECT * FROM friends WHERE user_id = #{userId} AND friend_id = #{friendId}")
    Friend findByUserAndFriend(Long userId, Long friendId);
}
