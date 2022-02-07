package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author hyw
 * @description 用户微服务
 */
@Table(name = "tb_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;  //用户名

    //对象序列化为json字符串时，忽略该属性
    @JsonIgnore
    private String password; //密码

    private String phone; //电话号码
    private Date created; //创建时间

    @JsonIgnore
    private String salt; //密码加盐
}
