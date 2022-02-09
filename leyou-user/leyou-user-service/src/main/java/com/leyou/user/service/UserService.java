package com.leyou.user.service;

import com.leyou.common.utils.CodecUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hyw
 * @description
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    //短信模版
    static final String KEY_PREFIX = "user:code:phone:";

    //打印日志
    static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

    /**
     * 生成短信验证码
     * @param phone
     * @return
     */
    public Boolean sendVerifyCode(String phone){
        //1、生成验证码
        String code = NumberUtils.generateCode(6);

        try {
            //2、发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            this.amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);

            //3、验证码缓存到redis，便于注册验证
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.HOURS);
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;
        }
    }

    /**
     * 发送短信
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        //1、效验短信验证码
        String cacheCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if(!StringUtils.equals(cacheCode, code)){
            return false;
        }

        //2、生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //3、对密码加盐
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        //4、添加数据库
        user.setId(null);
        user.setCreated(new Date());

        boolean boo = this.userMapper.insertSelective(user) == 1;
        if(boo){
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        }
        return boo;
    }
}
