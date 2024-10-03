package com.chloetsai.springbootmall.controller;

import com.chloetsai.springbootmall.dto.UserRegisterRequest;
import com.chloetsai.springbootmall.model.User;
import com.chloetsai.springbootmall.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用於處理用戶註冊請求的控制器方法
     *
     * @PostMapping 表示此方法處理發送到 "/user/register" 路徑的 POST 請求
     */
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {
        // 呼叫 userService 的 register 方法來執行用戶註冊, 並返回新註冊用戶的 ID (存在userId)
        Integer userId = userService.register(userRegisterRequest);

        // 呼叫 userService 的 getUserById 方法, 傳入剛才的返回的 userId, 獲取對應的用戶資料
        User user = userService.getUserById(userId);

        // 返回 HTTP 狀態碼 201 (已創建), 並將註冊成功的用戶物件返回給前端
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}
