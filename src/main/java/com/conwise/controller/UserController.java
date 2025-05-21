package com.conwise.controller;

import com.conwise.model.*;
import com.conwise.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody LoginUser loginUser, HttpSession session) {
        return ResponseEntity.ok(userService.login(loginUser,session));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        return ResponseEntity.ok(userService.logout(session));
    }

    @PostMapping("/check-auth")
    public ResponseEntity<ApiResponse<User>> checkAuth(HttpSession session) {
        return ResponseEntity.ok(userService.validate(session));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody RegisterUser user) {
        return ResponseEntity.ok(userService.register(user));

    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<User>> searchByUserName(@RequestParam("username") String username) {
        return ResponseEntity.ok(userService.searchByUserName(username));
    }
}
