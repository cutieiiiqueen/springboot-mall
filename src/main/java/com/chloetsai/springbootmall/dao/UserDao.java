package com.chloetsai.springbootmall.dao;

import com.chloetsai.springbootmall.dto.UserRegisterRequest;
import com.chloetsai.springbootmall.model.User;

public interface UserDao {

    User getUserById(Integer userId);

    Integer createUser(UserRegisterRequest userRegisterRequest);
}
