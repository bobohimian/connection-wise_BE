package com.conwise.interceptor;


import com.conwise.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String USER_KEY = "user"; // 假设用户信息存储在session中的key

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取HttpSession,如果没有则创建
        HttpSession session = request.getSession(true);
        // 2. 获取session中的用户对象
        User userObj = (User)session.getAttribute(USER_KEY);
        if (userObj == null) {
            // session中没有用户信息，返回未授权错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未登录或会话已过期");
            return false;
        }

        // 3. 从用户对象中获取用户ID
        int userId = userObj.getId();

        // 4. 检查用户是否有权限访问资源
        String requestURI = request.getRequestURI();
        if (!hasPermission(userId, requestURI)) {
            // 用户没有权限访问该资源
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("没有权限访问该资源");
            return false;
        }
        request.setAttribute("userId", userId);
        // 验证通过，继续处理请求
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 请求处理后的操作，如果需要的话
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        // 请求完成后的操作，如果需要的话
    }


    // 检查用户是否有权限访问资源的方法
    private boolean hasPermission(int userId, String requestURI) {
        // 实现您的权限检查逻辑
        // 例如：检查资源是否属于该用户，或者用户是否有操作权限
        return true; // 替换为实际实现
    }
}