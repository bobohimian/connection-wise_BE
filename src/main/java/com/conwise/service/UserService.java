package com.conwise.service;

import com.conwise.model.LoginUser;
import com.conwise.model.RegisterUser;
import com.conwise.model.User;

import java.util.List;

public interface UserService {
    User login(LoginUser loginUser);

    boolean register(RegisterUser user);

    User searchByUserName(String username);
}
