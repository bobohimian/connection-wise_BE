package com.conwise.service.impl;

import com.conwise.mapper.UserMapper;
import com.conwise.model.LoginUser;
import com.conwise.model.RegisterUser;
import com.conwise.model.User;
import com.conwise.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User login(LoginUser loginUser) {
        String username = loginUser.getUsername();
        if (username.isEmpty())
            return null;
        User user = userMapper.findByUsername(username);
        if (user == null)
            return null;
        String loginUserPassword = loginUser.getPassword();
        String password = user.getPassword();
        if (!password.equals(loginUserPassword))
            return null;
        return user;
    }

    @Override
    public boolean register(RegisterUser user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        if (username.isEmpty() || password.isEmpty() || email.isEmpty())
            return false;

        User byUsername = userMapper.findByUsername(username);
        if (byUsername != null)
            return false;
        User byEmail = userMapper.findByEmail(email);
        if (byEmail != null)
            return false;
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        int insert = userMapper.insert(newUser);
        return insert > 0;
    }

    @Override
    public User searchByUserName(String username) {
        User byUsername = userMapper.findByUsername(username);
        return byUsername;
    }

}
