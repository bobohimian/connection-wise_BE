package com.conwise.controller;

import com.conwise.model.LoginUser;
import com.conwise.model.RegisterUser;
import com.conwise.model.User;
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
    public ResponseEntity<User> login(@RequestBody LoginUser loginUser, HttpSession session) {
        User user = userService.login(loginUser);
        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        }
        session.invalidate();
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<User> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(null);
    }

    @PostMapping("/check-auth")
    public ResponseEntity<User> checkAuth(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.invalidate();
            System.out.println("未登录");
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUser user) {
        String username = user.getUsername();
        System.out.println("username = " + username);
        String password = user.getPassword();
        System.out.println("password = " + password);
        String email = user.getEmail();
        System.out.println("email = " + email);
        boolean registerSuccess = userService.register(user);
        if (!registerSuccess)
            return ResponseEntity.status(409).body(null);
        return ResponseEntity.ok("success");

    }
    @GetMapping("/search")
    public ResponseEntity<User> searchByUserName(@RequestParam("username") String username) {
        User user=userService.searchByUserName(username);
        return ResponseEntity.ok(user);
    }
}
