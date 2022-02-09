package com.leyou.user.controller;


import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hyw
 * @description 用户操作服务
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 检查用户数据的正确性
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data, @PathVariable("type") Integer type){
        Boolean checkResult = this.userService.checkUser(data, type);
        if(checkResult == null){ //参数请求有误
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(checkResult);
    }

    /**
     * 发送手机短信
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(String phone){
        Boolean boo = this.userService.sendVerifyCode(phone);
        if(boo == null || !boo){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
