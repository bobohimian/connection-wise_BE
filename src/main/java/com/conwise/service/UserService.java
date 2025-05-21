package com.conwise.service;

import com.conwise.model.ApiResponse;
import com.conwise.model.LoginUser;
import com.conwise.model.RegisterUser;
import com.conwise.model.User;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface UserService {

    ApiResponse<Void> logout(HttpSession session);

    ApiResponse<User> login(LoginUser loginUser, HttpSession session);

    ApiResponse<User> validate(HttpSession session);

    ApiResponse<Void> register(RegisterUser user);

    ApiResponse<User> searchByUserName(String username);
}
