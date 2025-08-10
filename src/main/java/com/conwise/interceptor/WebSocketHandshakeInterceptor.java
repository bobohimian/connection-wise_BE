package com.conwise.interceptor;

import com.conwise.model.ApiResponse;
import com.conwise.model.Canvas;
import com.conwise.model.User;
import com.conwise.service.CanvasService;
import com.conwise.service.CanvasShareService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {
    private final CanvasShareService canvasShareService;
    private final CanvasService canvasService;

    public WebSocketHandshakeInterceptor(CanvasShareService canvasShareService, CanvasService canvasService) {
        this.canvasShareService = canvasShareService;
        this.canvasService = canvasService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 将 ServerHttpRequest 转换为 ServletServerHttpRequest 以访问 Servlet 相关功能
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            // 提取 Cookie 中的 SESSION
            Map<String, List<String>> headers = servletRequest.getHeaders();
            List<String> cookieList = headers.get("Cookie");
            if (cookieList == null || cookieList.isEmpty()) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }
            String[] cookies = cookieList.get(0).split(";");
            String sessionId = null;

            for (String cookie : cookies) {
                if (cookie.contains("SESSIONID=")) {
                    String encodedSessionId = cookie.substring(cookie.lastIndexOf("SESSIONID=") + 10);
                    byte[] bytes = Base64.getDecoder().decode(encodedSessionId);
                    sessionId = new String(bytes);
                }
            }
            // 无 SESSION,拒绝握手
            if (sessionId == null) {
                System.out.println("无sessionId");
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }

            // 获取 HttpSession
            HttpSession session = servletRequest.getServletRequest().getSession();
            if (session == null || !session.getId().equals(sessionId)) {
                System.out.println("session不存在,或不是该用户");
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }

            // todo 验证会话是否过期
            // ...


            // 验证通过,验证权限
            Integer canvasIdFromUri = Integer.parseInt(Objects.requireNonNull(extractCanvasIdFromUri(servletRequest.getURI().toString())));
            User user = (User) session.getAttribute("user");
            Integer userId = user.getId();
            if (!this.hasPermission(userId, canvasIdFromUri)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }
            attributes.put("canvasId", canvasIdFromUri);
            attributes.put("userId", userId);
            return true;
        }
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private String extractCanvasIdFromUri(String uri) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);
        String path = builder.build().getPath();
        int index = path.lastIndexOf("canvas/");
        if (index != -1) {
            return path.substring(index + 7); // 提取 canvas/ 后的值
        }
        return null;
    }

    private Boolean hasPermission(Integer userId, Integer canvasId) {
        // 1.是否是自己的画布
        List<Canvas> canvasList = canvasService.getCanvasesByUserId(userId).getData();
        List<Canvas> list = canvasList.stream().filter(obj -> canvasId.equals(obj.getId())).toList();
        if (!list.isEmpty()) {
            return true;
        }
        // 2.判断是否有权限

        List<Canvas> sharedCanvasId = canvasShareService.getCanvasShareByUserId(userId).getData();
        List<Canvas> sharedList = sharedCanvasId.stream().filter(obj -> canvasId.equals(obj.getId())).toList();
        if (!sharedList.isEmpty()) {
            return true;
        }
        return false;
    }
}
