package com.leyou.user.service;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hyw
 * @description
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 效验用户或手机号是否存在
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(user) == 0; //检测到有数据，说明已存在该用户，检测到没有数据，说明不存在该用户
    }
}
