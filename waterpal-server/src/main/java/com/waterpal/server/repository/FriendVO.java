package com.waterpal.server.repository;

import lombok.Data;

/**
 * 好友列表 VO
 */
@Data
public class FriendVO {
    
    private Long id;
    
    private Long friendId;
    
    private String nickname;
    
    private String avatarUrl;
}
