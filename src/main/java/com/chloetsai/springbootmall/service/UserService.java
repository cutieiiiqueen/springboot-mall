package com.chloetsai.springbootmall.service;

import com.chloetsai.springbootmall.dto.UserRegisterRequest;
import com.chloetsai.springbootmall.model.User;

public interface UserService {

    User getUserById(Integer userId);

    Integer register(UserRegisterRequest userRegisterRequest);

}
