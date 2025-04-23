package com.conwise.controller;

import com.conwise.model.LoginUser;
import com.conwise.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginUser loginUser, HttpSession session) {
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setEmail("1719692158@qq.com");
        user.setAvatar("dj918eu310e3190e");
        if ("chenhong".equals(username) && "111".equals(password)) {
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
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(user);
    }
//    @PostMapping("/check-auth")
//    public ResponseEntity<User> checkAuth(HttpServletRequest request) {
//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        User user = (User) session.getAttribute("user");
//        if(user == null) {
//            return ResponseEntity.status(401).body(null);
//        }
//        return ResponseEntity.ok(user);
//    }
}
