package com.leyou.entiy;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserInfo {

    private Long id;

    private String username;

    public UserInfo() {
    }

    public UserInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}