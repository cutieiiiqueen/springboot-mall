package com.chloetsai.springbootmall.service.Impl;

import com.chloetsai.springbootmall.dao.UserDao;
import com.chloetsai.springbootmall.dto.UserLoginRequest;
import com.chloetsai.springbootmall.dto.UserRegisterRequest;
import com.chloetsai.springbootmall.model.User;
import com.chloetsai.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        // 檢查註冊的 email
        // 根據用戶註冊請求中的 Email 呼叫 userDao 查詢是否已有相同 Email 的用戶存在
        User user = userDao.getUserByEmail(userRegisterRequest.getEmail());

        // 如果用戶已存在（查詢結果不為 null），則拋出 HTTP 400 (BAD_REQUEST) 的異常中斷方法的執行
        if(user != null){
            log.warn("該 email {}  已經被註冊", userRegisterRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        userRegisterRequest.setPassword(hashedPassword);

        // 創建帳號
        // 如果用戶不存在，呼叫 userDao 的 createUser 方法來創建新的用戶，並返回新創建用戶的 ID
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        // 檢查 user 是否存在
        // 根據用戶登入請求中的 Email 呼叫 userDao 查詢是否有該用戶
        User user = userDao.getUserByEmail(userLoginRequest.getEmail());

        // 如果查無此用戶，記錄警告日誌，並拋出 HTTP 400 (BAD_REQUEST) 的異常，表示用戶未註冊
        if(user == null){
            log.warn("該 email {} 尚未註冊", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // 使用 MD5 生成密碼的雜湊值
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        // 檢查密碼
        // 檢查用戶提供的密碼是否與資料庫中的密碼匹配
        if(user.getPassword().equals(hashedPassword)) {
            // 如果密碼正確，返回用戶物件
            return user;
        } else {
            // 如果密碼不正確，記錄警告日誌，並拋出 HTTP 400 (BAD_REQUEST) 的異常
            log.warn("email {} 的密碼不正確", userLoginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
