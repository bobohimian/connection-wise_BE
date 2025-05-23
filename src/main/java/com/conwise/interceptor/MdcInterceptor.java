package com.conwise.interceptor;

import com.conwise.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class MdcInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_ID_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成唯一请求ID
        String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        MDC.put(REQUEST_ID_KEY, requestId);

        // 如果有用户信息，也可以放入MDC
        // 例如从JWT token或session中获取用户ID
        String userId = getUserIdFromRequest(request);
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求完成后清除MDC，防止内存泄漏
        MDC.clear();
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        int userId = (int) request.getAttribute(USER_ID_KEY);
        return String.valueOf(userId);
    }
}