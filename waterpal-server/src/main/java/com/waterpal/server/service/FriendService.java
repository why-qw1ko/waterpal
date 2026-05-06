package com.waterpal.server.service;

import com.waterpal.server.entity.Friend;
import com.waterpal.server.repository.FriendMapper;
import com.waterpal.server.repository.FriendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 好友服务
 */
@Service
@RequiredArgsConstructor
public class FriendService {
    
    private final FriendMapper friendMapper;
    
    /**
     * 获取好友列表
     */
    public List<FriendVO> getFriendList(Long userId) {
        return friendMapper.getFriendList(userId);
    }
    
    /**
     * 添加好友
     */
    @Transactional
    public void addFriend(Long userId, Long friendId) {
        // 检查是否已是好友
        Friend existing = friendMapper.findByUserAndFriend(userId, friendId);
        if (existing != null) {
            throw new RuntimeException("已是好友");
        }
        
        // 创建双向好友关系
        Friend f1 = new Friend();
        f1.setUserId(userId);
        f1.setFriendId(friendId);
        f1.setStatus(1);
        friendMapper.insert(f1);
        
        Friend f2 = new Friend();
        f2.setUserId(friendId);
        f2.setFriendId(userId);
        f2.setStatus(1);
        friendMapper.insert(f2);
    }
}
