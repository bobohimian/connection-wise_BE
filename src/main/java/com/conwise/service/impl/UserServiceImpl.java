package com.conwise.service.impl;

import com.conwise.mapper.UserMapper;
import com.conwise.model.*;
import com.conwise.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<User> login(LoginUser loginUser, HttpSession session) {
        String username = loginUser.getUsername();
        if (username.isEmpty())
            return ApiResponse.fail(ResponseCode.USER_LOGIN_FAILED);
        User user = userMapper.findByUsername(username);
        if (user == null)
            return ApiResponse.fail(ResponseCode.USER_NOT_FOUND);
        String loginUserPassword = loginUser.getPassword();
        String password = user.getPassword();
        if (!password.equals(loginUserPassword))
            return ApiResponse.fail(ResponseCode.USER_PASSWORD_ERROR);
        session.setAttribute("user", user);
        return ApiResponse.ok(user);
    }

    @Override
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.ok();
    }


    @Override
    public ApiResponse<User> validate(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.invalidate();
            return ApiResponse.fail(ResponseCode.UNAUTHORIZED);
        }
        return ApiResponse.ok(user);
    }

    @Override
    public ApiResponse<Void> register(RegisterUser user) {
        String username = user.getUsername();
        System.out.println("username = " + username);
        String password = user.getPassword();
        System.out.println("password = " + password);
        String email = user.getEmail();
        System.out.println("email = " + email);
        if (username.isEmpty() || password.isEmpty() || email.isEmpty())
            return ApiResponse.fail(ResponseCode.USER_REGISTER_FAILED);

        User byUsername = userMapper.findByUsername(username);
        if (byUsername != null)
            return ApiResponse.fail(ResponseCode.USER_ALREADY_EXISTS);
        User byEmail = userMapper.findByEmail(email);
        if (byEmail != null)
            return ApiResponse.fail(ResponseCode.USER_EMAIL_ALREADY_USED);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        int insert = userMapper.insert(newUser);
        return insert > 0 ? ApiResponse.ok() : ApiResponse.fail(ResponseCode.USER_REGISTER_FAILED);
    }

    @Override
    public ApiResponse<User> searchByUserName(String username) {
        User byUsername = userMapper.findByUsername(username);
        return byUsername != null ? ApiResponse.ok(byUsername) : ApiResponse.fail(ResponseCode.USER_NOT_FOUND);
    }

}
