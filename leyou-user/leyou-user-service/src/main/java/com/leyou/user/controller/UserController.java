package com.leyou.user.controller;


import com.leyou.user.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author
 * @description
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

}
