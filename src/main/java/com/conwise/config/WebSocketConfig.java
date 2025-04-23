package com.conwise.config;

import com.conwise.controller.CanvasWebSocketHandler;
import com.conwise.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CanvasWebSocketHandler canvasWebSocketHandler;
    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    public WebSocketConfig(CanvasWebSocketHandler canvasWebSocketHandler, WebSocketHandshakeInterceptor webSocketHandshakeInterceptor) {
        this.canvasWebSocketHandler = canvasWebSocketHandler;
        this.webSocketHandshakeInterceptor = webSocketHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(canvasWebSocketHandler, "/ws/canvas/{canvasId}")
                .addInterceptors(webSocketHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}